package net.warrentode.todepiglins.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.warrentode.todepiglins.TodePiglins.MODID;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    @SuppressWarnings("unused")
    public static final RegistryObject<RecipeSerializer<BarterCurrencyRecipe>> BARTER_CURRENCY_SERIALIZER =
            SERIALIZERS.register("bartering", ()-> BarterCurrencyRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
