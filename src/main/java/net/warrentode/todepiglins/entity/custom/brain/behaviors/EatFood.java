package net.warrentode.todepiglins.entity.custom.brain.behaviors;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    @SuppressWarnings("SameReturnValue")
    public static boolean eat(@NotNull TodePiglinMerchant todePiglinMerchant) {
        ItemStack stack = ItemStack.EMPTY;
        BrainUtils.setForgettableMemory(todePiglinMerchant,MemoryModuleType.ATE_RECENTLY, true, TodePiglinMerchant.EAT_COOLDOWN);
        stack.shrink(1);
        todePiglinMerchant.gameEvent(GameEvent.EAT);
        todePiglinMerchant.setHoldingItem(false);
        return true;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
