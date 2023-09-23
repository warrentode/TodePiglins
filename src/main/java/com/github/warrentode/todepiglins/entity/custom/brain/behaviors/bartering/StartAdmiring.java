package com.github.warrentode.todepiglins.entity.custom.brain.behaviors.bartering;

import com.github.warrentode.todepiglins.entity.custom.brain.sensors.TodePiglinBarterCurrencySensor;
import com.github.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StartAdmiring extends ExtendedBehaviour<TodePiglinMerchant> {
    private final int admireDuration;
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_ABSENT),
                    Pair.of(MemoryModuleType.ADMIRING_DISABLED, MemoryStatus.VALUE_ABSENT),
                    Pair.of(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryStatus.VALUE_ABSENT)
            );

    public StartAdmiring(int admireDuration) {
        this.admireDuration = admireDuration;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    protected boolean checkExtraStartConditions(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant) {
        ItemEntity itemEntity = todePiglinMerchant.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
        return TodePiglinBarterCurrencySensor.isLovedItem(itemEntity.getItem());
    }

    protected void start(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
        todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.ADMIRING_ITEM, true, this.admireDuration);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}