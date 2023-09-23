package com.github.warrentode.todepiglins.entity.custom.brain.behaviors.bartering;

import com.github.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class StopAdmireTired extends ExtendedBehaviour<TodePiglinMerchant> {
    private final int maxTimeToReachItem;
    private final int disableTime;
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryStatus.REGISTERED),
                    Pair.of(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryStatus.REGISTERED)
            );

    public StopAdmireTired(int pMaxTimeToReachItem, int pDisableTime) {
        this.maxTimeToReachItem = pMaxTimeToReachItem;
        this.disableTime = pDisableTime;
    }

    protected boolean checkExtraStartConditions(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getOffhandItem().isEmpty();
    }

    protected void start(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
        Brain<?> todePiglinMerchantBrain = todePiglinMerchant.getBrain();
        Optional<Integer> optional = todePiglinMerchantBrain.getMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
        if (optional.isEmpty()) {
            todePiglinMerchantBrain.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, 0);
        } else {
            int i = optional.get();
            if (i > this.maxTimeToReachItem) {
                todePiglinMerchantBrain.eraseMemory(MemoryModuleType.ADMIRING_ITEM);
                todePiglinMerchantBrain.eraseMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
                todePiglinMerchantBrain.setMemoryWithExpiry(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, true, this.disableTime);
            } else {
                todePiglinMerchantBrain.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, i + 1);
            }
        }
    }
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}