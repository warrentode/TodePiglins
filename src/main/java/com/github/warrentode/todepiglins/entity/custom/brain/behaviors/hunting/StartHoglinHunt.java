package com.github.warrentode.todepiglins.entity.custom.brain.behaviors.hunting;

import com.github.warrentode.todepiglins.entity.custom.brain.ModMemoryTypes;
import com.github.warrentode.todepiglins.entity.custom.brain.behaviors.combat.SetAngerTarget;
import com.github.warrentode.todepiglins.entity.custom.brain.sensors.TodePiglinSpecificSensor;
import com.github.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StartHoglinHunt extends ExtendedBehaviour<TodePiglinMerchant> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_ABSENT),
                    Pair.of(MemoryModuleType.HUNTED_RECENTLY, MemoryStatus.VALUE_ABSENT),
                    Pair.of(ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get(), MemoryStatus.REGISTERED)
            );

    protected static boolean hasAnyoneNearbyHuntedRecently(TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY) ||
                TodePiglinSpecificSensor.getVisibleAdultTodePiglins(todePiglinMerchant).stream().anyMatch((todePiglinMerchant1) ->
                        todePiglinMerchant1.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY)) ||
                TodePiglinSpecificSensor.getVisibleAdultPiglins(todePiglinMerchant).stream().anyMatch((abstractPiglin) ->
                        abstractPiglin.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY));
    }

    protected boolean checkExtraStartConditions(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant) {
        return !hasAnyoneNearbyHuntedRecently(todePiglinMerchant);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    protected void start(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
        Hoglin hoglin = todePiglinMerchant.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN).get();
        SetAngerTarget.setAngerTarget(todePiglinMerchant, hoglin);
        SetAngerTarget.dontKillAnyMoreHoglinsForAWhile(todePiglinMerchant);
        SetAngerTarget.broadcastAngerTarget(todePiglinMerchant, hoglin);
        SetAngerTarget.broadcastDontKillAnyMoreHoglinsForAWhile(todePiglinMerchant);
    }
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}