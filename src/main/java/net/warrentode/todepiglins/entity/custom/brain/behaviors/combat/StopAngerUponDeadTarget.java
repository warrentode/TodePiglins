package net.warrentode.todepiglins.entity.custom.brain.behaviors.combat;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.GameRules;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StopAngerUponDeadTarget extends ExtendedBehaviour<TodePiglinMerchant> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_PRESENT)
            );

    protected void start(@NotNull ServerLevel level, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
        BehaviorUtils.getLivingEntityFromUUIDMemory(todePiglinMerchant, MemoryModuleType.ANGRY_AT).ifPresent((angerTarget) -> {
            if (angerTarget.isDeadOrDying() && (angerTarget.getType() != EntityType.PLAYER ||
                    level.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS))) {
                todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
            }

        });
    }
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
