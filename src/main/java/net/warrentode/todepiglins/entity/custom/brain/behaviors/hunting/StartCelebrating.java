package net.warrentode.todepiglins.entity.custom.brain.behaviors.hunting;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.GameRules;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiPredicate;

public class StartCelebrating extends ExtendedBehaviour<TodePiglinMerchant> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.ANGRY_AT, MemoryStatus.REGISTERED),
                    Pair.of(MemoryModuleType.CELEBRATE_LOCATION, MemoryStatus.VALUE_ABSENT),
                    Pair.of(MemoryModuleType.DANCING, MemoryStatus.REGISTERED)
            );

    private final int celebrateDuration;
    private final BiPredicate<LivingEntity, LivingEntity> dancePredicate;

    public StartCelebrating(int celebrateDuration, BiPredicate<LivingEntity, LivingEntity> dancePredicate) {
        this.celebrateDuration = celebrateDuration;
        this.dancePredicate = dancePredicate;
    }

    protected boolean checkExtraStartConditions(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant) {
        return this.getAttackTarget(todePiglinMerchant).isDeadOrDying();
    }

    protected void start(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
        LivingEntity livingentity = this.getAttackTarget(todePiglinMerchant);
        if (this.dancePredicate.test(todePiglinMerchant, livingentity)) {
            todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.DANCING, true, this.celebrateDuration);
        }
        todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.CELEBRATE_LOCATION, livingentity.blockPosition(), this.celebrateDuration);
        if (livingentity.getType() != EntityType.PLAYER || pLevel.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
            todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
        }
    }

    private @NotNull LivingEntity getAttackTarget(@NotNull LivingEntity pLivingEntity) {
        //noinspection OptionalGetWithoutIsPresent
        return pLivingEntity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
