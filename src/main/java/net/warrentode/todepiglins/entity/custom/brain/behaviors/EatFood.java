package net.warrentode.todepiglins.entity.custom.brain.behaviors;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant.EAT_COOLDOWN;

public class EatFood extends ExtendedBehaviour<TodePiglinMerchant> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATE_RECENTLY, MemoryStatus.VALUE_ABSENT),
                    Pair.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_ABSENT)
            );

    public static boolean isFood(@NotNull ItemStack pStack) {
        return pStack.is(ItemTags.PIGLIN_FOOD);
    }

    public static boolean hasNotEatenRecently(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return !todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.ATE_RECENTLY);
    }
    public static int countFoodPointsInInventory(@NotNull TodePiglinMerchant todePiglinMerchant, @NotNull ItemStack stack) {
        if (stack.is(ItemTags.PIGLIN_FOOD)) {
            todePiglinMerchant.foodLevel = todePiglinMerchant.getInventory().countItem(stack.getItem());
        }
        else {
            todePiglinMerchant.foodLevel = 0;
        }
        return todePiglinMerchant.foodLevel;
    }

    public static void eat(@NotNull TodePiglinMerchant todePiglinMerchant) {
        int i = 0;
        ItemStack stack = todePiglinMerchant.getInventory().getItem(i);
        if ((todePiglinMerchant.getHealth() < todePiglinMerchant.getMaxHealth())
                && !BrainUtils.hasMemory(todePiglinMerchant, MemoryModuleType.DANCING)) {
            if (hasNotEatenRecently(todePiglinMerchant) && countFoodPointsInInventory(todePiglinMerchant, stack) != 0) {
                for (i = 0; i < todePiglinMerchant.getInventory().getContainerSize(); ++i) {
                    if (!stack.isEmpty()) {
                        int integer = countFoodPointsInInventory(todePiglinMerchant, stack);
                        int j = stack.getCount();

                        for (int k = j; k > 0; --k) {
                            if (!hasNotEatenRecently(todePiglinMerchant)) {
                                return;
                            }
                            else {
                                todePiglinMerchant.setIsEating(true);
                                todePiglinMerchant.gameEvent(GameEvent.EAT);
                                todePiglinMerchant.foodLevel += integer;
                                BrainUtils.setForgettableMemory(todePiglinMerchant,MemoryModuleType.ATE_RECENTLY, true, EAT_COOLDOWN);
                                todePiglinMerchant.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 50, 0, false, false));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
