package net.warrentode.todepiglins.entity.custom.brain.behaviors;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.GameRules;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.warrentode.todepiglins.entity.custom.brain.sensors.TodePiglinSpecificSensor;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant.TIME_BETWEEN_HUNTS;

public class SetAngerTarget extends ExtendedBehaviour<TodePiglinMerchant> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.NEAREST_ATTACKABLE, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT),
                    Pair.of(MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_ABSENT),
                    Pair.of(MemoryModuleType.UNIVERSAL_ANGER, MemoryStatus.VALUE_ABSENT)
            );

    public static void setAngerTarget(TodePiglinMerchant todePiglinMerchant, LivingEntity angerTarget) {
        if (Sensor.isEntityAttackableIgnoringLineOfSight(todePiglinMerchant, angerTarget)) {
            todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            BrainUtils.setForgettableMemory(todePiglinMerchant, MemoryModuleType.ANGRY_AT, angerTarget.getUUID(), TodePiglinMerchant.ANGER_DURATION);
            if (angerTarget.getType() == EntityType.HOGLIN && todePiglinMerchant.canHunt()) {
                broadcastDontKillAnyMoreHoglinsForAWhile(todePiglinMerchant);
            }
            if (angerTarget.getType() == EntityType.PLAYER && todePiglinMerchant.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.UNIVERSAL_ANGER, true, TodePiglinMerchant.ANGER_DURATION);
            }
        }
    }
    public static void broadcastAngerTarget(TodePiglinMerchant todePiglinMerchant, LivingEntity angerTarget) {
        TodePiglinSpecificSensor.getAdultTodePiglins(todePiglinMerchant).forEach((TodePiglinMerchant) -> {
            if (angerTarget.getType() != EntityType.HOGLIN || todePiglinMerchant.canHunt() && ((Hoglin)angerTarget).canBeHunted()) {
                setAngerTargetIfCloserThanCurrent(todePiglinMerchant, angerTarget);
            }
        });
    }
    private static void setAngerTargetIfCloserThanCurrent(TodePiglinMerchant todePiglinMerchant, LivingEntity currentTarget) {
        Optional<LivingEntity> optional = getAngerTarget(todePiglinMerchant);
        LivingEntity livingentity = BehaviorUtils.getNearestTarget(todePiglinMerchant, optional, currentTarget);
        if (optional.isEmpty() || optional.get() != livingentity) {
            setAngerTarget(todePiglinMerchant, livingentity);
        }
    }
    public static void broadcastDontKillAnyMoreHoglinsForAWhile(TodePiglinMerchant todePiglinMerchant) {
        TodePiglinSpecificSensor.getVisibleAdultTodePiglins(todePiglinMerchant).forEach(SetAngerTarget::dontKillAnyMoreHoglinsForAWhile);
    }
    public static void dontKillAnyMoreHoglinsForAWhile(TodePiglinMerchant todePiglinMerchant) {
        BrainUtils.setForgettableMemory(todePiglinMerchant,
                MemoryModuleType.HUNTED_RECENTLY, true, TIME_BETWEEN_HUNTS.sample(todePiglinMerchant.level.random));
    }
    private static @NotNull Optional<LivingEntity> getAngerTarget(TodePiglinMerchant todePiglinMerchant) {
        return BehaviorUtils.getLivingEntityFromUUIDMemory(todePiglinMerchant, MemoryModuleType.ANGRY_AT);
    }
    /** this is based on player block interaction that is written within the block files - I may need to write an event interception
     * or some other type of behavior like a sensor to activate this method in order to replicate this vanilla piglin behavior with this entity
     * otherwise I will need it to trigger on some other interesting thing that still feels piglin appropriate in response to the player
     * need to figure out event handling for this most likely **/
    // I know that calling this via the start method in here greatly alters the behavior from the vanilla version
    public static void angerNearbyTodePiglins(@NotNull Player targetPlayer, boolean onlyIfTargetSeen) {
        List<TodePiglinMerchant> list = targetPlayer.level.getEntitiesOfClass(TodePiglinMerchant.class,
                targetPlayer.getBoundingBox().inflate(TodePiglinMerchant.PLAYER_ANGER_RANGE));
        list.stream().filter(TodePiglinMerchant::isIdle).filter((nearestTarget) -> !onlyIfTargetSeen ||
                BrainUtils.canSee(nearestTarget, targetPlayer)).forEach((angryTodePiglin) -> {
            if (angryTodePiglin.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                setAngerTargetToNearestTargetablePlayerIfFound(angryTodePiglin, targetPlayer);
            } else {
                setAngerTarget(angryTodePiglin, targetPlayer);
            }
        });
    }
    public static void setAngerTargetToNearestTargetablePlayerIfFound(@NotNull TodePiglinMerchant todePiglinMerchant, LivingEntity currentTarget) {
        if (Objects.requireNonNull(BrainUtils.getMemory(todePiglinMerchant, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER))
                .closerThan(currentTarget, TodePiglinMerchant.PLAYER_ANGER_RANGE)) {
            setAngerTarget(todePiglinMerchant, currentTarget);
        } else {
            setAngerTarget(todePiglinMerchant, currentTarget);
        }
    }
    public static void broadcastUniversalAnger(TodePiglinMerchant todePiglinMerchant) {
        TodePiglinSpecificSensor.getAdultTodePiglins(todePiglinMerchant).forEach((todePiglinMerchant1) ->
                getNearestVisibleTargetablePlayer(todePiglinMerchant1).ifPresent((player) ->
                        SetAngerTarget.setAngerTarget(todePiglinMerchant1, player)));
    }
    public static Optional<Player> getNearestVisibleTargetablePlayer(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) ?
                todePiglinMerchant.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) : Optional.empty();
    }
    public static void maybeRetaliate(@NotNull TodePiglinMerchant todePiglinMerchant, LivingEntity angerTarget) {
        if (!todePiglinMerchant.getBrain().isActive(Activity.AVOID)) {
            if (Sensor.isEntityAttackableIgnoringLineOfSight(todePiglinMerchant, angerTarget)) {
                if (!BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(todePiglinMerchant, angerTarget, 4.0D)) {
                    if (angerTarget.getType() == EntityType.PLAYER && todePiglinMerchant.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                        SetAngerTarget.setAngerTargetToNearestTargetablePlayerIfFound(todePiglinMerchant, angerTarget);
                        SetAngerTarget.broadcastUniversalAnger(todePiglinMerchant);
                    } else {
                        SetAngerTarget.setAngerTarget(todePiglinMerchant, angerTarget);
                        SetAngerTarget.broadcastAngerTarget(todePiglinMerchant, angerTarget);
                    }
                }
            }
        }
    }

    protected boolean checkExtraStartConditions(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant) {
        return Objects.requireNonNull(BrainUtils.getMemory(todePiglinMerchant, MemoryModuleType.ATTACK_TARGET))
                .closerThan(todePiglinMerchant, TodePiglinMerchant.DESIRED_AVOID_DISTANCE);
    }

    protected void start(TodePiglinMerchant todePiglinMerchant) {
        Player targetPlayer = TodePiglinSpecificSensor.getNearestVisibleTargetablePlayer(todePiglinMerchant);

        if (targetPlayer == null) {
            BrainUtils.clearMemory(todePiglinMerchant, MemoryModuleType.ATTACK_TARGET);
            BrainUtils.clearMemory(todePiglinMerchant, MemoryModuleType.ANGRY_AT);
        }
        else {
            BrainUtils.setMemory(todePiglinMerchant, MemoryModuleType.ANGRY_AT, targetPlayer.getUUID());
            BrainUtils.setMemory(todePiglinMerchant, MemoryModuleType.ATTACK_TARGET, targetPlayer);
            BrainUtils.clearMemory(todePiglinMerchant, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            angerNearbyTodePiglins(targetPlayer, true);
        }
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}