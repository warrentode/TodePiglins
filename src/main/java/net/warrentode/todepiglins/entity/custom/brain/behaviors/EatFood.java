package net.warrentode.todepiglins.entity.custom.brain.behaviors;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant.EAT_COOLDOWN;

public class EatFood extends ExtendedBehaviour<TodePiglinMerchant> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATE_RECENTLY, MemoryStatus.VALUE_ABSENT)
            );

    public static boolean isFood(@NotNull ItemStack pStack) {
        return pStack.is(ItemTags.PIGLIN_FOOD);
    }

    public static boolean hasEatenRecently(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return !todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.ATE_RECENTLY);
    }

    public static boolean eat(@NotNull TodePiglinMerchant todePiglinMerchant) {
        ItemStack stack = todePiglinMerchant.getItemInHand(InteractionHand.OFF_HAND);
        todePiglinMerchant.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        if (stack.getCount() <= stack.getMaxStackSize() && (todePiglinMerchant.getHealth() < todePiglinMerchant.getMaxHealth())) {
            BrainUtils.setForgettableMemory(todePiglinMerchant,MemoryModuleType.ATE_RECENTLY, true, EAT_COOLDOWN);
            stack.shrink(1);
            todePiglinMerchant.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 50, 0));
            return true;
        }
        return false;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
