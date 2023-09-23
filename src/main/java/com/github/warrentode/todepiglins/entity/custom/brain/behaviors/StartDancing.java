package com.github.warrentode.todepiglins.entity.custom.brain.behaviors;

import com.github.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StartDancing extends ExtendedBehaviour<TodePiglinMerchant> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.CELEBRATE_LOCATION, MemoryStatus.VALUE_ABSENT),
                    Pair.of(MemoryModuleType.DANCING, MemoryStatus.REGISTERED)
            );

    private final int celebrateDuration;

    public StartDancing(int celebrateDuration) {
        this.celebrateDuration = celebrateDuration;
    }

    protected void start(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
        BrainUtils.setForgettableMemory(todePiglinMerchant, MemoryModuleType.DANCING, true, this.celebrateDuration);
        BrainUtils.setForgettableMemory(todePiglinMerchant, MemoryModuleType.CELEBRATE_LOCATION, todePiglinMerchant.blockPosition(), this.celebrateDuration);
        todePiglinMerchant.setDancing(true);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}