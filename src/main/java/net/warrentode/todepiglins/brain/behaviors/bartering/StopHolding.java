package net.warrentode.todepiglins.brain.behaviors.bartering;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraftforge.common.ToolActions;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StopHolding extends ExtendedBehaviour<TodePiglinMerchant> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_ABSENT)
            );

    protected boolean checkExtraStartConditions(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant) {
        TodePiglinMerchant.isBarterCurrency();
        return !todePiglinMerchant.getOffhandItem().isEmpty() && !todePiglinMerchant.getOffhandItem().canPerformAction(ToolActions.SHIELD_BLOCK);
    }

    protected void start(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
        TodePiglinMerchant.stopHoldingOffHandItem(todePiglinMerchant, true);
    }
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
