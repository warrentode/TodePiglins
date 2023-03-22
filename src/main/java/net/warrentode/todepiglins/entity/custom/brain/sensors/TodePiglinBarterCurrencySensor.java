package net.warrentode.todepiglins.entity.custom.brain.sensors;

import com.google.common.collect.ImmutableList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.warrentode.todepiglins.entity.custom.brain.ModSensorTypes;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import net.warrentode.todepiglins.util.ModTags;
import org.jetbrains.annotations.NotNull;

public class TodePiglinBarterCurrencySensor<E extends LivingEntity> extends ExtendedSensor<E> {
    private static final ImmutableList<MemoryModuleType<?>> MEMORIES =
            ImmutableList.of(
                    // vanilla memory types
                    MemoryModuleType.LOOK_TARGET,
                    MemoryModuleType.NEAREST_VISIBLE_PLAYER,
                    MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
                    MemoryModuleType.ADMIRING_ITEM,
                    MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM,
                    MemoryModuleType.ADMIRING_DISABLED,
                    MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM,
                    MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM
            );

    public ImmutableList<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ModSensorTypes.TODEPIGLIN_BARTER_CURRENCY.get();
    }

    @SuppressWarnings("unused")
    public final Ingredient barterItems;

    public TodePiglinBarterCurrencySensor(Ingredient pBarterItems) {
        this.barterItems = pBarterItems;
    }

    // checks the held item being admired if it's tagged as barter currency
    public static void isBarterCurrency() {
        ItemStack stack = ItemStack.EMPTY;
        stack.is(ModTags.Items.PIGLIN_BARTER_ITEMS);
    }

    public static boolean isLovedItem(@NotNull ItemStack stack) {
        return stack.is(ItemTags.PIGLIN_LOVED);
    }
    public static boolean isWantedItem(@NotNull ItemStack stack) {
        return stack.is(ModTags.Items.PIGLIN_WANTED_ITEMS);
    }
    public static boolean canAdmire(TodePiglinMerchant todePiglinMerchant, ItemStack stack) {
        return !isAdmiringDisabled(todePiglinMerchant) && !isAdmiringItem(todePiglinMerchant)
                && stack.is(ModTags.Items.PIGLIN_WANTED_ITEMS);
    }
    public static void admireItem(@NotNull TodePiglinMerchant todePiglinMerchant) {
        BrainUtils.setForgettableMemory(todePiglinMerchant, MemoryModuleType.ADMIRING_ITEM, true, TodePiglinMerchant.ADMIRE_DURATION);
    }
    public static boolean isAdmiringItem(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return BrainUtils.hasMemory(todePiglinMerchant, MemoryModuleType.ADMIRING_ITEM);
    }
    public static boolean isAdmiringDisabled(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return BrainUtils.hasMemory(todePiglinMerchant, MemoryModuleType.ADMIRING_DISABLED);
    }
    public static boolean isNotHoldingWantedItemInOffHand(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getOffhandItem().isEmpty() || !isWantedItem(todePiglinMerchant.getOffhandItem());
    }
}
