package net.warrentode.todepiglins.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;
import org.jetbrains.annotations.NotNull;

import static net.warrentode.todepiglins.TodePiglins.MODID;

public class BarterCurrencyRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    final String group;
    private final ItemStack result;
    private final NonNullList<Ingredient> barterItems;
    private final boolean isSimple;
    static int width = 1;
    static int height = 1;
    static final int MAX_WIDTH = 1;
    static final int MAX_HEIGHT = 1;

    public BarterCurrencyRecipe(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> barterItems) {
        this.id = id;
        this.barterItems = barterItems;
        this.group = group;
        this.result = result;
        this.isSimple = barterItems.stream().allMatch(Ingredient::isSimple);
        width = MAX_WIDTH;
        height = MAX_HEIGHT;
    }

    @Override
    public boolean matches(@NotNull SimpleContainer inventory, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }
        StackedContents stackedcontents = new StackedContents();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for (int j = 0; j < 1; ++j) {
            ItemStack itemstack = inventory.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                if (isSimple)
                    stackedcontents.accountStack(itemstack, 1);
                else inputs.add(itemstack);
            }
        }

        return i == this.barterItems.size()
                && (isSimple ? stackedcontents.canCraft(this, null)
                : RecipeMatcher.findMatches(inputs,  this.barterItems) != null);
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {

        return width * height >= this.barterItems.size();
    }

    @Override
    public ItemStack getResultItem() {
        return result.copy();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Items.GOLD_INGOT);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<BarterCurrencyRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        @SuppressWarnings("unused")
        public static final String ID = "bartering";
    }

    public static class Serializer implements RecipeSerializer<BarterCurrencyRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        @SuppressWarnings("unused")
        public static final ResourceLocation ID =
                new ResourceLocation(MODID, "bartering");
        private static final int width = MAX_WIDTH;
        private static final int height = MAX_HEIGHT;

        @Override
        public @NotNull BarterCurrencyRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject pSerializedRecipe) {
            String group = GsonHelper.getAsString(pSerializedRecipe, "group", "");
            NonNullList<Ingredient> inputs = itemsFromJson(GsonHelper.getAsJsonArray(pSerializedRecipe, "currency"));
            if (inputs.isEmpty()) {
                throw new JsonParseException("No Currency for Recipe");
            } else if (inputs.size() > width * height) {
                throw new JsonParseException("Too Many Currencies for Recipe. The Max is " + width * height);
            } else {
                ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));
                return new BarterCurrencyRecipe(id, group, itemstack, inputs);
            }
        }

        private @NotNull NonNullList<Ingredient> itemsFromJson(@NotNull JsonArray ingredients) {
            NonNullList<Ingredient> inputs = NonNullList.create();

            for(int i = 0; i < ingredients.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredients.get(i));
                //noinspection ConstantValue,PointlessBooleanExpression
                if (true || !ingredient.isEmpty()) {
                    // FORGE: Skip checking if an ingredient is empty during shapeless recipe deserialization
                    // to prevent complex ingredients from caching tags too early.
                    // Can not be done using a config value due to sync issues.
                    inputs.add(ingredient);
                }
            }

            return inputs;
        }

        public BarterCurrencyRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            String group = buf.readUtf();
            int i = buf.readVarInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(i, Ingredient.EMPTY);

            //noinspection Java8ListReplaceAll
            for(int j = 0; j < inputs.size(); ++j) {
                inputs.set(j, Ingredient.fromNetwork(buf));
            }

            ItemStack itemstack = buf.readItem();
            return new BarterCurrencyRecipe(id, group, itemstack, inputs);
        }

        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull BarterCurrencyRecipe pRecipe) {
            buf.writeVarInt(BarterCurrencyRecipe.width);
            buf.writeVarInt(BarterCurrencyRecipe.height);
            buf.writeUtf(pRecipe.group);

            for (Ingredient ingredient : pRecipe.barterItems) {
                ingredient.toNetwork(buf);
            }

            buf.writeItem(pRecipe.result);
        }
    }
}
