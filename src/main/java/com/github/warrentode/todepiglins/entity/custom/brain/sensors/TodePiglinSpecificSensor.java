package com.github.warrentode.todepiglins.entity.custom.brain.sensors;

import com.github.warrentode.todepiglins.entity.custom.brain.ModMemoryTypes;
import com.github.warrentode.todepiglins.entity.custom.brain.ModSensorTypes;
import com.github.warrentode.todepiglins.entity.custom.brain.behaviors.combat.SetAngerTarget;
import com.github.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.github.warrentode.todepiglins.entity.custom.brain.behaviors.EatFood.countFoodPointsInInventory;

public class TodePiglinSpecificSensor<E extends LivingEntity> extends ExtendedSensor<E> {
    private static final ImmutableList<MemoryModuleType<?>> MEMORIES =
            ImmutableList.of(
                    // vanilla memory types
                    MemoryModuleType.LOOK_TARGET,
                    MemoryModuleType.NEAREST_LIVING_ENTITIES,
                    MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                    MemoryModuleType.NEAREST_VISIBLE_PLAYER,
                    MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
                    MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS,
                    MemoryModuleType.NEARBY_ADULT_PIGLINS,
                    MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
                    MemoryModuleType.HURT_BY,
                    MemoryModuleType.HURT_BY_ENTITY,
                    MemoryModuleType.WALK_TARGET,
                    MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                    MemoryModuleType.ATTACK_TARGET,
                    MemoryModuleType.ATTACK_COOLING_DOWN,
                    MemoryModuleType.INTERACTION_TARGET,
                    MemoryModuleType.PATH,
                    MemoryModuleType.ANGRY_AT,
                    MemoryModuleType.UNIVERSAL_ANGER,
                    MemoryModuleType.AVOID_TARGET,
                    MemoryModuleType.ADMIRING_ITEM,
                    MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM,
                    MemoryModuleType.ADMIRING_DISABLED,
                    MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM,
                    MemoryModuleType.CELEBRATE_LOCATION,
                    MemoryModuleType.DANCING,
                    MemoryModuleType.HUNTED_RECENTLY,
                    MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN,
                    MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
                    MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED,
                    MemoryModuleType.RIDE_TARGET,
                    MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
                    MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
                    MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN,
                    MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD,
                    MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM,
                    MemoryModuleType.ATE_RECENTLY,
                    MemoryModuleType.NEAREST_REPELLENT,
                    // custom memory types
                    ModMemoryTypes.NEARBY_ADULT_TODEPIGLINS.get(),
                    ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get(),
                    ModMemoryTypes.VISIBLE_ADULT_TODEPIGLIN_COUNT.get()
            );

    public ImmutableList<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ModSensorTypes.TODEPIGLIN_SPECIFIC.get();
    }

    @Override
    protected void doTick(ServerLevel level, @NotNull E entity) {
        SmartBrain<?> brain = (SmartBrain<?>) entity.getBrain();
        TodePiglinMerchant todePiglinMerchant = (TodePiglinMerchant) entity;
        ItemStack stack = ItemStack.EMPTY.getItem().getDefaultInstance();
        countFoodPointsInInventory(todePiglinMerchant, stack);

        List<TodePiglinMerchant> adultTodePiglins = new ObjectArrayList<>();
        List<AbstractPiglin> adultPiglins = new ObjectArrayList<>();

        BrainUtils.withMemory(brain, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, nearestVisible -> {
            Mob nemesis = null;
            Hoglin huntableHoglin = null;
            Hoglin babyHoglin = null;
            LivingEntity zombified = null;
            Player playerNoFriendlyGear = null;
            Player playerWithGoodie = null;
            Player visiblePlayer = null;

            List<TodePiglinMerchant> visibleAdultTodePiglins = new ObjectArrayList<>();
            List<AbstractPiglin> visibleAdultPiglins = new ObjectArrayList<>();

            int adultHoglinCount = 0;

            for (LivingEntity target : nearestVisible.findAll(obj -> true)) {
                // DETECT HOGLIN POPULATION
                if (target instanceof Hoglin hoglin) {
                    // check for baby hoglins
                    if (hoglin.isBaby() && babyHoglin == null) {
                        babyHoglin = hoglin;
                    }
                    // check for adult hoglins
                    else if (hoglin.isAdult()) {
                        adultHoglinCount++;

                        // check for huntable hoglins
                        if (huntableHoglin == null && hoglin.canBeHunted() && adultHoglinCount > 2 && !(hoglin.isBaby())) {
                            huntableHoglin = hoglin;
                        }
                    }
                }
                // DETECT COMMUNITY & ALLIES
                else if (target instanceof PiglinBrute brute) {
                    visibleAdultPiglins.add(brute);
                }
                else if (target instanceof Piglin piglin) {
                    if (piglin.isAdult()) {
                        visibleAdultPiglins.add(piglin);
                    }
                }
                else if (target instanceof TodePiglinMerchant) {
                    if (todePiglinMerchant.isAdult()) {
                        visibleAdultTodePiglins.add(todePiglinMerchant);
                    }
                }
                // PLAYER DETECTION
                else if (target instanceof ServerPlayer player) {
                    if (!player.isCreative() && !player.isSpectator()) {
                        if (playerNoFriendlyGear == null && !isWearingFriendlyGear(player) && entity.canAttack(player)) {
                            playerNoFriendlyGear = player;
                        }
                    }
                    else if (playerWithGoodie == null && !player.isSpectator() && isPlayerHoldingLovedItem(player)) {
                        playerWithGoodie = player;
                    }
                    else if (visiblePlayer == null) {
                        visiblePlayer = player;
                    }
                }
                // DETECT ZOMBIFIED
                else if (nemesis != null || !(target instanceof WitherSkeleton) && !(target instanceof WitherBoss) && !(target instanceof AbstractIllager)) {
                    if (zombified == null && isZombified(target.getType())) {
                        zombified = target;
                    }
                }
                // DETECT MORTAL ENEMIES
                else {
                    nemesis = (Mob) target;
                }

            }

            // set memories for nemesis and zombies
            BrainUtils.setMemory(brain, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, nemesis);
            BrainUtils.setMemory(brain, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, zombified);
            // set memories for hoglin population
            BrainUtils.setMemory(brain, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, babyHoglin);
            BrainUtils.setMemory(brain, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, huntableHoglin);
            BrainUtils.setMemory(brain, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, adultHoglinCount);
            // set memories for players
            BrainUtils.setMemory(brain, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, playerNoFriendlyGear);
            BrainUtils.setMemory(brain, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, playerWithGoodie);
            BrainUtils.setMemory(brain, MemoryModuleType.NEAREST_VISIBLE_PLAYER, visiblePlayer);
            // set memories for vanilla community
            BrainUtils.setMemory(entity, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, visibleAdultPiglins);
            BrainUtils.setMemory(entity, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, visibleAdultPiglins.size());
            // set memories for custom community
            BrainUtils.setMemory(entity, ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get(), visibleAdultTodePiglins);
            BrainUtils.setMemory(entity, ModMemoryTypes.VISIBLE_ADULT_TODEPIGLIN_COUNT.get(), visibleAdultTodePiglins.size());

            // DETECT REPELLENTS
            BrainUtils.setMemory(brain, MemoryModuleType.NEAREST_REPELLENT,
                    BlockPos.findClosestMatch(
                            entity.blockPosition(),
                            TodePiglinMerchant.REPELLENT_DETECTION_RANGE_HORIZONTAL,
                            TodePiglinMerchant.REPELLENT_DETECTION_RANGE_VERTICAL,
                            pos -> {
                                BlockState state = level.getBlockState(pos);
                                boolean isRepellent = state.is(BlockTags.PIGLIN_REPELLENTS);
                                return isRepellent && state.is(Blocks.SOUL_CAMPFIRE) ? CampfireBlock.isLitCampfire(state) : isRepellent;
                            }
                    ).orElse(null));
        });

        BrainUtils.withMemory(brain, MemoryModuleType.NEAREST_LIVING_ENTITIES, entities -> {
            // adding community entities to their lists
            for (LivingEntity target : entities) {
                if (target instanceof AbstractPiglin abstractPiglin && abstractPiglin.isAdult()){
                    // add to the list the nearest adult piglins
                    adultPiglins.add(abstractPiglin);
                }
                else if (target instanceof TodePiglinMerchant && todePiglinMerchant.isAdult()) {
                    // add to the list the nearest adult todepiglins
                    adultTodePiglins.add(todePiglinMerchant);
                }
            }
        });
        // vanilla piglin community
        BrainUtils.setMemory(entity, MemoryModuleType.NEARBY_ADULT_PIGLINS, adultPiglins);
        // custom piglin community
        BrainUtils.setMemory(entity, ModMemoryTypes.NEARBY_ADULT_TODEPIGLINS.get(), adultTodePiglins);
    }

    public static List<TodePiglinMerchant> getAdultTodePiglins(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return BrainUtils.getMemory(todePiglinMerchant, ModMemoryTypes.NEARBY_ADULT_TODEPIGLINS.get());
    }
    public static List<TodePiglinMerchant> getVisibleAdultTodePiglins(TodePiglinMerchant todePiglinMerchant) {
        return BrainUtils.getMemory(todePiglinMerchant, ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get());
    }

    public static List<AbstractPiglin> getVisibleAdultPiglins(TodePiglinMerchant todePiglinMerchant) {
        return BrainUtils.getMemory(todePiglinMerchant, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS);
    }

    public static Integer getVisibleHoglinCount(TodePiglinMerchant todePiglinMerchant) {
        if (BrainUtils.getMemory(todePiglinMerchant, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT) != null) {
            return BrainUtils.getMemory(todePiglinMerchant, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT);
        }
        else return 0;
    }

    public static boolean hoglinsOutnumberPiglins(@NotNull TodePiglinMerchant todePiglinMerchant) {
        int i = todePiglinMerchant.getBrain().getMemory(ModMemoryTypes.VISIBLE_ADULT_TODEPIGLIN_COUNT.get()).orElse(0) + 1;
        int j = todePiglinMerchant.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0);
        return j > i;
    }
    public static void setAvoidTargetAndDontHuntForAWhile(@NotNull TodePiglinMerchant todePiglinMerchant, LivingEntity target) {
        BrainUtils.clearMemory(todePiglinMerchant, MemoryModuleType.ANGRY_AT);
        BrainUtils.clearMemory(todePiglinMerchant, MemoryModuleType.ATTACK_TARGET);
        BrainUtils.clearMemory(todePiglinMerchant, MemoryModuleType.WALK_TARGET);
        BrainUtils.setForgettableMemory(todePiglinMerchant,
                MemoryModuleType.AVOID_TARGET,target, TodePiglinMerchant.RETREAT_DURATION.sample(todePiglinMerchant.level.random));
        SetAngerTarget.dontKillAnyMoreHoglinsForAWhile(todePiglinMerchant);
    }
    public static void broadcastRetreat(TodePiglinMerchant todePiglinMerchant, LivingEntity retreatTarget) {
        getVisibleAdultTodePiglins(todePiglinMerchant).stream().filter((todePiglinMerchant1) ->
                todePiglinMerchant != null).forEach((nearestTarget) ->
                retreatFromNearestTarget(nearestTarget, retreatTarget));
    }
    private static void retreatFromNearestTarget(@NotNull TodePiglinMerchant todePiglinMerchant, LivingEntity retreatTarget) {
        BrainUtils.getMemory(todePiglinMerchant, MemoryModuleType.ATTACK_TARGET);
        LivingEntity nearestTarget;
        nearestTarget = retreatTarget;
        BrainUtils.setMemory(todePiglinMerchant, MemoryModuleType.AVOID_TARGET, retreatTarget);
        if (retreatTarget instanceof Hoglin) {
            setAvoidTargetAndDontHuntForAWhile(todePiglinMerchant, nearestTarget);
        }
    }

    private boolean isZombified(EntityType<?> type) {
        return type == EntityType.ZOMBIFIED_PIGLIN
                || type == EntityType.ZOGLIN
                || type == EntityType.ZOMBIE
                || type == EntityType.ZOMBIE_VILLAGER
                || type == EntityType.ZOMBIE_HORSE;
    }
    public static boolean isNearRepellent(@NotNull TodePiglinMerchant todePiglinMerchant) {
        if (BrainUtils.hasMemory(todePiglinMerchant, MemoryModuleType.NEAREST_REPELLENT)) {
            return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_REPELLENT);
        }
        return false;
    }
    public static Optional<LivingEntity> getAvoidTarget(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET) ?
                todePiglinMerchant.getBrain().getMemory(MemoryModuleType.AVOID_TARGET) : Optional.empty();
    }
    public static boolean isNearAvoidTarget(TodePiglinMerchant todePiglinMerchant) {
        if (BrainUtils.hasMemory(todePiglinMerchant, MemoryModuleType.AVOID_TARGET)) {
            return Objects.requireNonNull(BrainUtils.getMemory(todePiglinMerchant, MemoryModuleType.AVOID_TARGET))
                    .closerThan(todePiglinMerchant, TodePiglinMerchant.DESIRED_AVOID_DISTANCE);
        }
        return false;
    }

    public static boolean isPlayerHoldingLovedItem(@NotNull Player player) {
        return player.getType() == EntityType.PLAYER && player.isHolding(TodePiglinBarterCurrencySensor::isLovedItem);
    }
    public static boolean seesPlayerHoldingLovedItem(TodePiglinMerchant todePiglinMerchant) {
        return BrainUtils.hasMemory(todePiglinMerchant, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
    }
    public static boolean isWearingFriendlyGear(@NotNull Player player) {
        for (ItemStack itemstack : player.getArmorSlots()) {
            itemstack.getItem();
            if (itemstack.getItem() instanceof ArmorItem && ((ArmorItem) itemstack.getItem()).getMaterial() == ArmorMaterials.GOLD) {
                return true;
            }
        }
        return false;
    }
    public static boolean isAlliedTo(Mob mob, @NotNull LivingEntity livingEntity) {
        if (livingEntity.isAlliedTo(mob)) {
            return true;
        }
        else if (livingEntity.getClass() == TodePiglinMerchant.class) {
            return mob.getTeam() == null && livingEntity.getTeam() == null;
        }
        else if (livingEntity.getClass() == Piglin.class) {
            return mob.getTeam() == null && livingEntity.getTeam() == null;
        }
        else if (livingEntity.getClass() == PiglinBrute.class) {
            return mob.getTeam() == null && livingEntity.getTeam() == null;
        }
        else {
            return false;
        }
    }
    @SuppressWarnings("SameReturnValue")
    public static @Nullable Player getNearestVisibleTargetablePlayer(@NotNull TodePiglinMerchant todePiglinMerchant) {
        if (todePiglinMerchant.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER).isPresent()) {
            todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
        }
        return null;
    }
}