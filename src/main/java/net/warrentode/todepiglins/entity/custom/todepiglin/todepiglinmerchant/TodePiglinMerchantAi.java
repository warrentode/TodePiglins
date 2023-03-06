package net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.warrentode.todepiglins.entity.ModEntityTypes;
import net.warrentode.todepiglins.entity.custom.brain.behavior.todepiglinmerchant.bartering.StartAdmiringItemIfSeen;
import net.warrentode.todepiglins.entity.custom.brain.behavior.todepiglinmerchant.bartering.StopAdmiringIfItemTooFarAway;
import net.warrentode.todepiglins.entity.custom.brain.behavior.todepiglinmerchant.bartering.StopAdmiringIfTiredOfTryingToReachItem;
import net.warrentode.todepiglins.entity.custom.brain.behavior.todepiglinmerchant.bartering.StopHoldingItemIfNoLongerAdmiring;
import net.warrentode.todepiglins.entity.custom.brain.behavior.todepiglinmerchant.hoglin_hunting.RememberIfHoglinWasKilled;
import net.warrentode.todepiglins.entity.custom.brain.behavior.todepiglinmerchant.hoglin_hunting.StartHuntingHoglin;
import net.warrentode.todepiglins.entity.custom.brain.manager.todepiglinmerchant.BrainActivityGroup;
import net.warrentode.todepiglins.entity.custom.brain.memory.ModMemoryTypes;
import net.warrentode.todepiglins.entity.custom.brain.sensor.todepiglin.TodePiglinBarterCurrencySensor;
import net.warrentode.todepiglins.entity.custom.todepiglin.TodeAbstractPiglin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TodePiglinMerchantAi {
    public static final int REPELLENT_DETECTION_RANGE_HORIZONTAL = 8;
    public static final int REPELLENT_DETECTION_RANGE_VERTICAL = 4;
    private static final double PLAYER_ANGER_RANGE = 16.0D;
    private static final int ANGER_DURATION = 600;
    public static final int ADMIRE_DURATION = 120;
    private static final int MAX_DISTANCE_TO_WALK_TO_ITEM = 9;
    private static final int MAX_TIME_TO_WALK_TO_ITEM = 200;
    private static final int HOW_LONG_TIME_TO_DISABLE_ADMIRE_WALKING_IF_CANT_REACH_ITEM = 200;
    public static final int CELEBRATION_TIME = 300;
    private static final UniformInt TIME_BETWEEN_HUNTS = TimeUtil.rangeOfSeconds(30, 120);
    private static final long BABY_FLEE_DURATION_AFTER_GETTING_HIT = 100L;
    private static final int HIT_BY_PLAYER_MEMORY_TIMEOUT = 400;
    private static final int MAX_WALK_DISTANCE_TO_START_RIDING = 8;
    private static final UniformInt RIDE_START_INTERVAL = TimeUtil.rangeOfSeconds(10, 40);
    private static final UniformInt RIDE_DURATION = TimeUtil.rangeOfSeconds(10, 30);
    private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);
    public static final int MELEE_ATTACK_COOLDOWN = 20;
    private static final int EAT_COOLDOWN = 200;
    private static final int DESIRED_DISTANCE_FROM_ENTITY_WHEN_AVOIDING = 12;
    private static final int MAX_LOOK_DIST = 8;
    private static final int MAX_LOOK_DIST_FOR_PLAYER_HOLDING_LOVED_ITEM = 14;
    private static final int INTERACTION_RANGE = 8;
    public static final int MIN_DESIRED_DIST_FROM_TARGET_WHEN_HOLDING_CROSSBOW = 5;
    public static final float SPEED_WHEN_STRAFING_BACK_FROM_TARGET = 0.75F;
    private static final int DESIRED_DISTANCE_FROM_ZOMBIFIED = 6;
    private static final UniformInt AVOID_ZOMBIFIED_DURATION = TimeUtil.rangeOfSeconds(5, 7);
    private static final UniformInt BABY_AVOID_NEMESIS_DURATION = TimeUtil.rangeOfSeconds(5, 7);
    private static final float PROBABILITY_OF_CELEBRATION_DANCE = 0.1F;
    private static final float SPEED_MULTIPLIER_WHEN_AVOIDING = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_RETREATING = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_MOUNTING = 0.8F;
    private static final float SPEED_MULTIPLIER_WHEN_GOING_TO_WANTED_ITEM = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_GOING_TO_CELEBRATE_LOCATION = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_DANCING = 0.6F;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.6F;

    public static SmartBrain<?> makeBrain(TodePiglinMerchant todePiglinMerchant, SmartBrain<TodePiglinMerchant> todepiglinmerchantBrain) {
        getCoreTasks();
        getIdleTasks();
        getAdmireTasks();
        getFightTasks(todePiglinMerchant, todepiglinmerchantBrain);
        getCelebrateTasks();
        getRetreatTasks();
        getRideTasks();
        todePiglinMerchant.getActivityPriorities();
        todePiglinMerchant.getAlwaysRunningActivities();
        todePiglinMerchant.getDefaultActivity();
        return todepiglinmerchantBrain;
    }

    protected static void initMemories(@NotNull TodePiglinMerchant todePiglinMerchant, RandomSource pRandom) {
        int i = TIME_BETWEEN_HUNTS.sample(pRandom);
        todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.HUNTED_RECENTLY, true, i);
    }

    public static void getCoreTasks() {
        BrainActivityGroup.coreTasks(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new InteractWithDoor(),
                babyAvoidNemesis(),
                avoidZombified(),
                new StopHoldingItemIfNoLongerAdmiring(),
                new StartAdmiringItemIfSeen(ADMIRE_DURATION),
                new StartCelebratingIfTargetDead(CELEBRATION_TIME, TodePiglinMerchantAi::wantsToDance),
                new StopBeingAngryIfTargetDead<>()
        );
    }

    public static void getIdleTasks() {
        BrainActivityGroup.idleTasks(
                new SetEntityLookTarget(TodePiglinMerchantAi::isPlayerHoldingLovedItem, MAX_LOOK_DIST_FOR_PLAYER_HOLDING_LOVED_ITEM),
                new StartAttacking<>(TodePiglinMerchantAi::findNearestValidAttackTarget),
                new RunIf<>(TodePiglinMerchant::canHunt, new StartHuntingHoglin()),
                avoidRepellent(),
                babySometimesRideBabyHoglin(),
                createIdleLookBehaviors(),
                createIdleMovementBehaviors(),
                new SetLookAndInteract(EntityType.PLAYER, 4));
    }

    @SuppressWarnings("unused")
    public static void getFightTasks(TodePiglinMerchant todePiglinMerchant, Brain<TodePiglinMerchant> todepiglinmerchantBrain) {
        BrainActivityGroup.fightTasks(
                new StopAttackingIfTargetInvalid<>((livingEntity) ->
                        !isNearestValidAttackTarget(todePiglinMerchant, livingEntity)),
                new RunIf<>(TodePiglinMerchantAi::hasCrossbow, new BackUpIfTooClose<>(MIN_DESIRED_DIST_FROM_TARGET_WHEN_HOLDING_CROSSBOW, SPEED_WHEN_STRAFING_BACK_FROM_TARGET)),
                new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.0F),
                new MeleeAttack(MELEE_ATTACK_COOLDOWN),
                new CrossbowAttack<>(),
                new RememberIfHoglinWasKilled(),
                new EraseMemoryIf<>(TodePiglinMerchantAi::isNearZombified, MemoryModuleType.ATTACK_TARGET)
        );
    }

    public static void getCelebrateTasks() {
        BrainActivityGroup.celebrateTasks(
                avoidRepellent(),
                new SetEntityLookTarget(TodePiglinMerchantAi::isPlayerHoldingLovedItem, MAX_LOOK_DIST_FOR_PLAYER_HOLDING_LOVED_ITEM),
                new StartAttacking<>(TodePiglinMerchantAi::findNearestValidAttackTarget),
                new RunIf<>((todePiglinMerchant) -> !TodePiglinMerchant.piglinBoogie,
                        new GoToTargetLocation<>(MemoryModuleType.CELEBRATE_LOCATION, 2, SPEED_MULTIPLIER_WHEN_GOING_TO_CELEBRATE_LOCATION)),
                new RunIf<>(TodePiglinMerchant::isDancing,
                        new GoToTargetLocation<>(MemoryModuleType.CELEBRATE_LOCATION, 4, SPEED_MULTIPLIER_WHEN_DANCING)),
                new RunOne<>(ImmutableList.of(
                        Pair.of(new SetEntityLookTarget(ModEntityTypes.TODEPIGLINMERCHANT.get(), MAX_LOOK_DIST), 1),
                        Pair.of(new RandomStroll(SPEED_MULTIPLIER_WHEN_IDLING, 2, 1), 1),
                        Pair.of(new DoNothing(10, 20), 1)))
        );
    }

    public static void getAdmireTasks() {
        BrainActivityGroup.admireTasks(
                new GoToWantedItem<>(TodePiglinMerchantAi::isNotHoldingLovedItemInOffHand, SPEED_MULTIPLIER_WHEN_GOING_TO_WANTED_ITEM, true, MAX_DISTANCE_TO_WALK_TO_ITEM),
                new StopAdmiringIfItemTooFarAway(MAX_DISTANCE_TO_WALK_TO_ITEM),
                new StopAdmiringIfTiredOfTryingToReachItem(MAX_TIME_TO_WALK_TO_ITEM, HOW_LONG_TIME_TO_DISABLE_ADMIRE_WALKING_IF_CANT_REACH_ITEM)
        );
    }

    public static void getRetreatTasks() {
        BrainActivityGroup.retreatTasks(
                SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, SPEED_MULTIPLIER_WHEN_RETREATING, DESIRED_DISTANCE_FROM_ENTITY_WHEN_AVOIDING, true),
                createIdleLookBehaviors(),
                createIdleMovementBehaviors(),
                new EraseMemoryIf<>(TodePiglinMerchantAi::wantsToStopFleeing, MemoryModuleType.AVOID_TARGET)
        );
    }

    public static void getRideTasks() {
        BrainActivityGroup.rideTasks(
                new Mount<>(SPEED_MULTIPLIER_WHEN_MOUNTING),
                new SetEntityLookTarget(TodePiglinMerchantAi::isPlayerHoldingLovedItem, MAX_LOOK_DIST),
                new RunIf<>(Entity::isPassenger, createIdleLookBehaviors()),
                new DismountOrSkipMounting<>(MAX_WALK_DISTANCE_TO_START_RIDING, TodePiglinMerchantAi::wantsToStopRiding)
        );
    }

    @Contract(" -> new")
    private static @NotNull RunOne<TodePiglinMerchant> createIdleLookBehaviors() {
        return new RunOne<>(
                ImmutableList.of(
                        Pair.of(new SetEntityLookTarget(EntityType.PLAYER, MAX_LOOK_DIST), 1),
                        Pair.of(new SetEntityLookTarget(ModEntityTypes.TODEPIGLINMERCHANT.get(), MAX_LOOK_DIST), 1),
                        Pair.of(new SetEntityLookTarget(MAX_LOOK_DIST), 1),
                        Pair.of(new DoNothing(30, 60), 1)));
    }

    @Contract(" -> new")
    private static @NotNull RunOne<TodePiglinMerchant> createIdleMovementBehaviors() {
        return new RunOne<>(
                ImmutableList.of(
                        Pair.of(new RandomStroll(SPEED_MULTIPLIER_WHEN_IDLING), 2),
                        Pair.of(InteractWith.of(ModEntityTypes.TODEPIGLINMERCHANT.get(), INTERACTION_RANGE, MemoryModuleType.INTERACTION_TARGET, SPEED_MULTIPLIER_WHEN_IDLING, 2), 2),
                        Pair.of(new RunIf<>(TodePiglinMerchantAi::doesntSeeAnyPlayerHoldingLovedItem, new SetWalkTargetFromLookTarget(SPEED_MULTIPLIER_WHEN_IDLING, 3)), 2),
                        Pair.of(new DoNothing(30, 60), 1)));
    }

    @Contract(" -> new")
    private static @NotNull SetWalkTargetAwayFrom<BlockPos> avoidRepellent() {
        return SetWalkTargetAwayFrom.pos(MemoryModuleType.NEAREST_REPELLENT, SPEED_MULTIPLIER_WHEN_AVOIDING, 8, false);
    }

    @Contract(" -> new")
    private static @NotNull CopyMemoryWithExpiry<TodePiglinMerchant, LivingEntity> babyAvoidNemesis() {
        return new CopyMemoryWithExpiry<>(TodePiglinMerchant::isBaby, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.AVOID_TARGET, BABY_AVOID_NEMESIS_DURATION);
    }
    @Contract(" -> new")
    public static @NotNull CopyMemoryWithExpiry<TodePiglinMerchant, LivingEntity> avoidZombified() {
        return new CopyMemoryWithExpiry<>(TodePiglinMerchantAi::isNearZombified, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.AVOID_TARGET, AVOID_ZOMBIFIED_DURATION);
    }

    protected static void updateActivity(@NotNull TodePiglinMerchant todePiglinMerchant) {
        Brain<TodePiglinMerchant> todepiglinmerchantBrain = todePiglinMerchant.getBrain();
        Activity activity = todepiglinmerchantBrain.getActiveNonCoreActivity().orElse(null);
        todepiglinmerchantBrain.setActiveActivityToFirstValid(ImmutableList.of(Activity.ADMIRE_ITEM, Activity.FIGHT, Activity.AVOID, Activity.CELEBRATE, Activity.RIDE, Activity.IDLE));
        Activity activity1 = todepiglinmerchantBrain.getActiveNonCoreActivity().orElse(null);
        if (activity != activity1) {
            getMerchantSoundForCurrentActivity(todePiglinMerchant).ifPresent(todePiglinMerchant::playSoundEvent);
        }

        todePiglinMerchant.setAggressive(todepiglinmerchantBrain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
        if (!todepiglinmerchantBrain.hasMemoryValue(MemoryModuleType.RIDE_TARGET) && isBabyRidingBaby(todePiglinMerchant)) {
            todePiglinMerchant.stopRiding();
        }

        if (!todepiglinmerchantBrain.hasMemoryValue(MemoryModuleType.CELEBRATE_LOCATION)) {
            todepiglinmerchantBrain.eraseMemory(MemoryModuleType.DANCING);
        }

        todePiglinMerchant.setDancing(todepiglinmerchantBrain.hasMemoryValue(MemoryModuleType.DANCING));
    }

    private static boolean isBabyRidingBaby(TodePiglinMerchant pPassenger) {
        if (!pPassenger.isBaby()) {
            return false;
        } else {
            Entity entity = pPassenger.getVehicle();
            return entity instanceof TodePiglinMerchant && ((TodePiglinMerchant)entity).isBaby() || entity instanceof Hoglin && ((Hoglin)entity).isBaby();
        }
    }


    /** this is where the barter recipe check begins **/
    protected static void pickUpItem(TodePiglinMerchant todePiglinMerchant, @NotNull ItemEntity itemEntity) {
        stopWalking(todePiglinMerchant);
        ItemStack itemstack;
        if (itemEntity.getItem().is(Items.GOLD_NUGGET)) {
            todePiglinMerchant.take(itemEntity, itemEntity.getItem().getCount());
            itemstack = itemEntity.getItem();
            itemEntity.discard();
        } else {
            todePiglinMerchant.take(itemEntity, 1);
            itemstack = removeOneItemFromItemEntity(itemEntity);
        }
        if (isLovedItem(itemstack)) {
            todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
            holdInOffhand(todePiglinMerchant, itemstack);
            admireGoldItem(todePiglinMerchant);
            // currency sensor activated here //
            TodePiglinBarterCurrencySensor.isBarterCurrency();
        } else if (isFood(itemstack) && !hasEatenRecently(todePiglinMerchant)) {
            eat(todePiglinMerchant);
        } else {
            boolean flag = todePiglinMerchant.equipItemIfPossible(itemstack);
            if (!flag) {
                putInMerchantInventory(todePiglinMerchant, itemstack);
            }
        }
    }

    private static void holdInOffhand(TodePiglinMerchant todePiglinMerchant, ItemStack stack) {
        if (isHoldingItemInOffHand(todePiglinMerchant)) {
            todePiglinMerchant.spawnAtLocation(todePiglinMerchant.getItemInHand(InteractionHand.OFF_HAND));
        }
        todePiglinMerchant.holdInOffHand(stack);
    }

    private static ItemStack removeOneItemFromItemEntity(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        ItemStack stack1 = stack.split(1);
        if (stack.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(stack);
        }
        return stack1;
    }

    public static void stopHoldingOffHandItem(TodePiglinMerchant todePiglinMerchant, boolean pShouldBarter) {
        ItemStack stack = todePiglinMerchant.getItemInHand(InteractionHand.OFF_HAND);
        todePiglinMerchant.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        if (todePiglinMerchant.isAdult()) {
            boolean flag = stack.isPiglinCurrency();
            if (pShouldBarter && flag) {
                //accepts the trade - happy emote here & and gives an item
                //particle emote set in sensor
                //yes nod set in behavior
                todePiglinMerchant.setBarterSuccess(true);
                throwItems(todePiglinMerchant, getBarterResponseItems(todePiglinMerchant));
            } else if (!flag) {
                //accepts the gift, pleasant emote, but just shakes his head
                //particle emote set in sensor
                boolean flag1 = todePiglinMerchant.equipItemIfPossible(stack);
                if (!flag1) {
                    //particle emote set in sensor
                    putInMerchantInventory(todePiglinMerchant, stack);
                }
            }
        } else {
            boolean flag2 = todePiglinMerchant.equipItemIfPossible(stack);
            if (!flag2) {
                ItemStack stack1 = todePiglinMerchant.getMainHandItem();
                if (isLovedItem(stack1)) {
                    putInMerchantInventory(todePiglinMerchant, stack1);
                } else {
                    throwItems(todePiglinMerchant, Collections.singletonList(stack1));
                }
                todePiglinMerchant.nodsNo(todePiglinMerchant);
                todePiglinMerchant.holdInMainHand(stack);
            }
        }
    }

    protected static void cancelAdmiring(TodePiglinMerchant todePiglinMerchant) {
        if (isAdmiringItem(todePiglinMerchant) && !todePiglinMerchant.getOffhandItem().isEmpty()) {
            todePiglinMerchant.spawnAtLocation(todePiglinMerchant.getOffhandItem());
            todePiglinMerchant.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        }
    }

    private static void putInMerchantInventory(TodePiglinMerchant todePiglinMerchant, ItemStack stack) {
        todePiglinMerchant.addToMerchantInventory(stack);
        throwItemsTowardRandomPos(todePiglinMerchant, Collections.singletonList(stack));
    }

    private static void throwItems(TodePiglinMerchant todePiglinMerchant, List<ItemStack> stacks) {
        Optional<Player> optional = todePiglinMerchant.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
        if (optional.isPresent()) {
            throwItemsTowardPlayer(todePiglinMerchant, optional.get(), stacks);
        } else {
            throwItemsTowardRandomPos(todePiglinMerchant, stacks);
        }
    }

    private static void throwItemsTowardRandomPos(TodePiglinMerchant todePiglinMerchant, List<ItemStack> stacks) {
        throwItemsTowardPos(todePiglinMerchant, stacks, getRandomNearbyPos(todePiglinMerchant));
    }

    private static void throwItemsTowardPlayer(TodePiglinMerchant todePiglinMerchant, Player pPlayer, List<ItemStack> stacks) {
        throwItemsTowardPos(todePiglinMerchant, stacks, pPlayer.position());
    }

    private static void throwItemsTowardPos(TodePiglinMerchant todePiglinMerchant, List<ItemStack> stacks, Vec3 pPos) {
        if (!stacks.isEmpty()) {
            todePiglinMerchant.swing(InteractionHand.OFF_HAND);
            for(ItemStack itemstack : stacks) {
                BehaviorUtils.throwItem(todePiglinMerchant, itemstack, pPos.add(0.0D, 1.0D, 0.0D));
            }
        }
    }

    /** if I can get the custom currency working then maybe I can then get custom loot tables running too
     * it's very likely that I can get this running with the sensor **/
    private static List<ItemStack> getBarterResponseItems(TodePiglinMerchant todePiglinMerchant) {
        //maybe dance?
        barterDance(todePiglinMerchant);
        LootTable lootTable = Objects.requireNonNull(todePiglinMerchant.level.getServer()).getLootTables().get(BuiltInLootTables.PIGLIN_BARTERING);
        return lootTable.getRandomItems((
                new LootContext.Builder((ServerLevel)todePiglinMerchant.level))
                .withParameter(LootContextParams.THIS_ENTITY, todePiglinMerchant)
                .withRandom(todePiglinMerchant.level.random).create(LootContextParamSets.PIGLIN_BARTER));
    }

    private static void barterDance(TodePiglinMerchant todePiglinMerchant) {
        float DanceChance;
        DanceChance = RandomSource.create(todePiglinMerchant.level.getGameTime()).nextFloat();

        if (DanceChance > PROBABILITY_OF_CELEBRATION_DANCE) {
            Vec3 vec3 = todePiglinMerchant.getDeltaMovement();
            todePiglinMerchant.level.addParticle(ParticleTypes.SNEEZE,
                    todePiglinMerchant.getX() - (todePiglinMerchant.getBbWidth() + 1.0F) * 0.5D *
                            (double) Mth.sin(todePiglinMerchant.yBodyRot * ((float)Math.PI / 180F)),
                    todePiglinMerchant.getEyeY() - (double)0.1F, todePiglinMerchant.getZ() +
                            (todePiglinMerchant.getBbWidth() + 1.0F) * 0.5D *
                                    (double)Mth.cos(todePiglinMerchant.yBodyRot * ((float)Math.PI / 180F)), vec3.x, 0.0D, vec3.z);
            todePiglinMerchant.playSound(SoundEvents.PANDA_SNEEZE, 1.0F, 1.0F);
        }
        else if (DanceChance < PROBABILITY_OF_CELEBRATION_DANCE) {
            todePiglinMerchant.setBarterSuccess(false);
            todePiglinMerchant.setDancing(true);
        }
    }

    public static boolean wantsToDance(LivingEntity gameTime, LivingEntity livingEntity) {
        if (livingEntity.getType() != EntityType.HOGLIN) {
            return false;
        }
        else {
            return RandomSource.create(gameTime.level.getGameTime()).nextFloat() < PROBABILITY_OF_CELEBRATION_DANCE;
        }
    }

    protected static boolean wantsToPickup(TodePiglinMerchant todePiglinMerchant, ItemStack stack) {
        if (todePiglinMerchant.isBaby() && stack.is(ItemTags.IGNORED_BY_PIGLIN_BABIES)) {
            return false;
        } else if (stack.is(ItemTags.PIGLIN_REPELLENTS)) {
            return false;
        } else if (isAdmiringDisabled(todePiglinMerchant) && todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
            return false;
        } else if (stack.isPiglinCurrency()) {
            return isNotHoldingLovedItemInOffHand(todePiglinMerchant);
        } else {
            boolean flag = todePiglinMerchant.canAddToMerchantInventory(stack);
            if (stack.is(Items.GOLD_NUGGET)) {
                return flag;
            } else if (isFood(stack)) {
                return !hasEatenRecently(todePiglinMerchant) && flag;
            } else if (!isLovedItem(stack)) {
                return todePiglinMerchant.canReplaceCurrentItem(stack);
            } else {
                return isNotHoldingLovedItemInOffHand(todePiglinMerchant) && flag;
            }
        }
    }

    public static boolean isLovedItem(ItemStack stack) {
        return stack.is(ItemTags.PIGLIN_LOVED);
    }

    private static boolean wantsToStopRiding(TodePiglinMerchant todePiglinMerchant, Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return false;
        } else {
            return !mob.isBaby() || !mob.isAlive() || wasHurtRecently(todePiglinMerchant)
                    || wasHurtRecently(mob) || mob instanceof TodeAbstractPiglin && mob.getVehicle() == null;
        }
    }

    public static boolean isNearestValidAttackTarget(TodePiglinMerchant todePiglinMerchant, LivingEntity attackTarget) {
        return findNearestValidAttackTarget(todePiglinMerchant).filter((livingEntity) -> livingEntity == attackTarget).isPresent();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static boolean isNearZombified(TodePiglinMerchant todePiglinMerchant) {
        Brain<TodePiglinMerchant> todepiglinmerchantBrain = todePiglinMerchant.getBrain();
        if (todepiglinmerchantBrain.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED)) {
            LivingEntity livingentity = todepiglinmerchantBrain.getMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED).get();
            return todePiglinMerchant.closerThan(livingentity, DESIRED_DISTANCE_FROM_ZOMBIFIED);
        } else {
            return false;
        }
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(TodePiglinMerchant todePiglinMerchant) {
        Brain<TodePiglinMerchant> piglinMerchantBrain = todePiglinMerchant.getBrain();
        if (isNearZombified(todePiglinMerchant)) {
            return Optional.empty();
        } else {
            Optional<LivingEntity> optional = BehaviorUtils.getLivingEntityFromUUIDMemory(todePiglinMerchant, MemoryModuleType.ANGRY_AT);
            if (optional.isPresent() && Sensor.isEntityAttackableIgnoringLineOfSight(todePiglinMerchant, optional.get())) {
                return optional;
            } else {
                if (piglinMerchantBrain.hasMemoryValue(MemoryModuleType.UNIVERSAL_ANGER)) {
                    Optional<Player> optional1 = piglinMerchantBrain.getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
                    if (optional1.isPresent()) {
                        return optional1;
                    }
                }
                Optional<Mob> optional3 = piglinMerchantBrain.getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
                if (optional3.isPresent()) {
                    return optional3;
                } else {
                    Optional<Player> optional2 = piglinMerchantBrain.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
                    return optional2.isPresent() && Sensor.isEntityAttackable(todePiglinMerchant, optional2.get()) ? optional2 : Optional.empty();
                }
            }
        }
    }

    /** this is based on player block interaction that in written within the block files - you may need to write an event interception
     * or some other type of behavior like a sensor to activate this method in order to replicate this vanilla piglin behavior with this entity
     * otherwise you will need it to trigger on some other interesting thing that still feels piglin appropriate in response to the player
     * need to figure out event handling most likely **/
    public static void angerNearbyPiglins(@NotNull Player pPlayer, boolean pAngerOnlyIfCanSee) {
        List<TodePiglinMerchant> list = pPlayer.level.getEntitiesOfClass(TodePiglinMerchant.class, pPlayer.getBoundingBox().inflate(PLAYER_ANGER_RANGE));
        list.stream().filter(TodePiglinMerchantAi::isIdle).filter((nearestTarget) -> !pAngerOnlyIfCanSee || BehaviorUtils.canSee(nearestTarget, pPlayer)).forEach((angerNearby) -> {
            if (angerNearby.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                setAngerTargetToNearestTargetablePlayerIfFound(angerNearby, pPlayer);
            } else {
                setAngerTarget(angerNearby, pPlayer);
            }
        });
    }

    /** this looks like where the piglin merchant entity can take a barter item or loved item from the player directly
     * possibly build a merchant trader screen and maybe make good use of the currency sensor with this **/
    public static InteractionResult mobInteract(TodePiglinMerchant todePiglinMerchant, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (canAdmire(todePiglinMerchant, itemstack)) {
            ItemStack itemstack1 = itemstack.split(1);
            holdInOffhand(todePiglinMerchant, itemstack1);
            admireGoldItem(todePiglinMerchant);
            stopWalking(todePiglinMerchant);
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.PASS;
        }
    }

    protected static boolean canAdmire(TodePiglinMerchant todePiglinMerchant, ItemStack stack) {
        return !isAdmiringDisabled(todePiglinMerchant) && !isAdmiringItem(todePiglinMerchant) && stack.isPiglinCurrency();
    }

    protected static void wasHurtBy(TodePiglinMerchant todePiglinMerchant, LivingEntity angerTarget) {
        if (!(angerTarget instanceof TodeAbstractPiglin)) {
            if (isHoldingItemInOffHand(todePiglinMerchant)) {
                stopHoldingOffHandItem(todePiglinMerchant, false);
            }
            Brain<TodePiglinMerchant> todePiglinMerchantBrain = todePiglinMerchant.getBrain();
            todePiglinMerchantBrain.eraseMemory(MemoryModuleType.CELEBRATE_LOCATION);
            todePiglinMerchantBrain.eraseMemory(MemoryModuleType.DANCING);
            todePiglinMerchantBrain.eraseMemory(MemoryModuleType.ADMIRING_ITEM);
            if (angerTarget instanceof Player) {
                todePiglinMerchantBrain.setMemoryWithExpiry(MemoryModuleType.ADMIRING_DISABLED, true, HIT_BY_PLAYER_MEMORY_TIMEOUT);
            }
            getAvoidTarget(todePiglinMerchant).ifPresent((wasHurtBy) -> {
                if (wasHurtBy.getType() != angerTarget.getType()) {
                    todePiglinMerchantBrain.eraseMemory(MemoryModuleType.AVOID_TARGET);
                }
            });
            if (todePiglinMerchant.isBaby()) {
                todePiglinMerchantBrain.setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, angerTarget, BABY_FLEE_DURATION_AFTER_GETTING_HIT);
                if (Sensor.isEntityAttackableIgnoringLineOfSight(todePiglinMerchant, angerTarget)) {
                    broadcastAngerTarget(todePiglinMerchant, angerTarget);
                }

            } else if (angerTarget.getType() == EntityType.HOGLIN && hoglinsOutnumberPiglins(todePiglinMerchant)) {
                setAvoidTargetAndDontHuntForAWhile(todePiglinMerchant, angerTarget);
                broadcastRetreat(todePiglinMerchant, angerTarget);
            } else {
                maybeRetaliate(todePiglinMerchant, angerTarget);
            }
        }
    }

    protected static void maybeRetaliate(TodePiglinMerchant todePiglinMerchant, LivingEntity angerTarget) {
        if (!todePiglinMerchant.getBrain().isActive(Activity.AVOID)) {
            if (Sensor.isEntityAttackableIgnoringLineOfSight(todePiglinMerchant, angerTarget)) {
                if (!BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(todePiglinMerchant, angerTarget, 4.0D)) {
                    if (angerTarget.getType() == EntityType.PLAYER && todePiglinMerchant.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                        setAngerTargetToNearestTargetablePlayerIfFound(todePiglinMerchant, angerTarget);
                        broadcastUniversalAnger(todePiglinMerchant);
                    } else {
                        setAngerTarget(todePiglinMerchant, angerTarget);
                        broadcastAngerTarget(todePiglinMerchant, angerTarget);
                    }
                }
            }
        }
    }

    public static Optional<SoundEvent> getMerchantSoundForCurrentActivity(TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().getActiveNonCoreActivity().map((activity) -> getSoundForMerchantActivity(todePiglinMerchant, activity));
    }

    private static SoundEvent getSoundForMerchantActivity(TodePiglinMerchant todePiglinMerchant, Activity merchantActivity) {
        if (merchantActivity == Activity.FIGHT) {
            return SoundEvents.PIGLIN_ANGRY;
        } else if (todePiglinMerchant.isConverting()) {
            return SoundEvents.PIGLIN_RETREAT;
        } else if (merchantActivity == Activity.AVOID && isNearAvoidTarget(todePiglinMerchant)) {
            return SoundEvents.PIGLIN_RETREAT;
        } else if (merchantActivity == Activity.ADMIRE_ITEM) {
            return SoundEvents.PIGLIN_ADMIRING_ITEM;
        } else if (merchantActivity == Activity.CELEBRATE) {
            return SoundEvents.PIGLIN_CELEBRATE;
        } else if (seesPlayerHoldingLovedItem(todePiglinMerchant)) {
            return SoundEvents.PIGLIN_JEALOUS;
        } else {
            return isNearRepellent(todePiglinMerchant) ? SoundEvents.PIGLIN_RETREAT : SoundEvents.PIGLIN_AMBIENT;
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static boolean isNearAvoidTarget(TodePiglinMerchant todePiglinMerchant) {
        Brain<TodePiglinMerchant> todePiglinMerchantBrain = todePiglinMerchant.getBrain();
        return todePiglinMerchantBrain.hasMemoryValue(MemoryModuleType.AVOID_TARGET) && 
                todePiglinMerchantBrain.getMemory(MemoryModuleType.AVOID_TARGET).get().closerThan(todePiglinMerchant, 12.0D);
    }

    public static boolean hasAnyoneNearbyHuntedRecently(TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY) ||
                getVisibleAdultPiglins(todePiglinMerchant).stream().anyMatch((brain) ->
                        todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY));
    }

    private static List<TodeAbstractPiglin> getVisibleAdultPiglins(TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().getMemory(ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get()).orElse(ImmutableList.of());
    }

    private static Optional<?> getAdultPiglins(TodeAbstractPiglin todeAbstractPiglin) {
        return todeAbstractPiglin.getBrain().getMemory(ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get());
    }

    public static boolean isWearingGold(Player player) {
        for(ItemStack itemstack : player.getArmorSlots()) {
            itemstack.getItem();
            if (itemstack.makesPiglinsNeutral(player)) {
                return true;
            }
        }
        return false;
    }

    private static void stopWalking(TodePiglinMerchant todePiglinMerchant) {
        todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        todePiglinMerchant.getNavigation().stop();
    }

    private static RunSometimes<TodePiglinMerchant> babySometimesRideBabyHoglin() {
        return new RunSometimes<>(new CopyMemoryWithExpiry<>(TodePiglinMerchant::isBaby, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.RIDE_TARGET, RIDE_DURATION), RIDE_START_INTERVAL);
    }

    public static void broadcastAngerTarget(TodePiglinMerchant todePiglinMerchant, LivingEntity angerTarget) {
        getAdultPiglins(todePiglinMerchant).ifPresent((TodeAbstractPiglin) -> {
            if (angerTarget.getType() != EntityType.HOGLIN || todePiglinMerchant.canHunt() && ((Hoglin)angerTarget).canBeHunted()) {
                setAngerTargetIfCloserThanCurrent(todePiglinMerchant, angerTarget);
            }
        });
    }

    protected static void broadcastUniversalAnger(TodePiglinMerchant todePiglinMerchant) {
        getAdultPiglins(todePiglinMerchant).flatMap(objectFocus ->
                getNearestVisibleTargetablePlayer(todePiglinMerchant)).ifPresent((angerTarget) ->
                setAngerTarget(todePiglinMerchant, angerTarget));
    }

    public static void broadcastDontKillAnyMoreHoglinsForAWhile(TodePiglinMerchant todePiglinMerchant) {
        getVisibleAdultPiglins(todePiglinMerchant).forEach(TodePiglinMerchantAi::dontKillAnyMoreHoglinsForAWhile);
    }

    public static void setAngerTarget(TodePiglinMerchant todePiglinMerchant, LivingEntity angerTarget) {
        if (Sensor.isEntityAttackableIgnoringLineOfSight(todePiglinMerchant, angerTarget)) {
            todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, angerTarget.getUUID(), ANGER_DURATION);
            if (angerTarget.getType() == EntityType.HOGLIN && todePiglinMerchant.canHunt()) {
                dontKillAnyMoreHoglinsForAWhile(todePiglinMerchant);
            }
            if (angerTarget.getType() == EntityType.PLAYER && todePiglinMerchant.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.UNIVERSAL_ANGER, true, ANGER_DURATION);
            }
        }
    }

    private static void setAngerTargetToNearestTargetablePlayerIfFound(TodePiglinMerchant todePiglinMerchant, LivingEntity currentTarget) {
        Optional<Player> optional = getNearestVisibleTargetablePlayer(todePiglinMerchant);
        if (optional.isPresent()) {
            setAngerTarget(todePiglinMerchant, optional.get());
        } else {
            setAngerTarget(todePiglinMerchant, currentTarget);
        }
    }

    private static void setAngerTargetIfCloserThanCurrent(TodePiglinMerchant todePiglinMerchant, LivingEntity currentTarget) {
        Optional<LivingEntity> optional = getAngerTarget(todePiglinMerchant);
        LivingEntity livingentity = BehaviorUtils.getNearestTarget(todePiglinMerchant, optional, currentTarget);
        if (optional.isEmpty() || optional.get() != livingentity) {
            setAngerTarget(todePiglinMerchant, livingentity);
        }
    }

    private static Optional<LivingEntity> getAngerTarget(TodeAbstractPiglin todeAbstractPiglin) {
        return BehaviorUtils.getLivingEntityFromUUIDMemory(todeAbstractPiglin, MemoryModuleType.ANGRY_AT);
    }

    public static Optional<LivingEntity> getAvoidTarget(TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET) ?
                todePiglinMerchant.getBrain().getMemory(MemoryModuleType.AVOID_TARGET) : Optional.empty();
    }

    public static Optional<Player> getNearestVisibleTargetablePlayer(TodeAbstractPiglin todeAbstractPiglin) {
        return todeAbstractPiglin.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) ?
                todeAbstractPiglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) : Optional.empty();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static boolean wantsToStopFleeing(TodePiglinMerchant todePiglinMerchant) {
        Brain<TodePiglinMerchant> todePiglinMerchantBrain = todePiglinMerchant.getBrain();
        if (!todePiglinMerchantBrain.hasMemoryValue(MemoryModuleType.AVOID_TARGET)) {
            return true;
        } else {
            LivingEntity livingentity = todePiglinMerchantBrain.getMemory(MemoryModuleType.AVOID_TARGET).get();
            EntityType<?> entitytype = livingentity.getType();
            if (entitytype == EntityType.HOGLIN) {
                return piglinsEqualOrOutnumberHoglins(todePiglinMerchant);
            } else if (isZombified(entitytype)) {
                return !todePiglinMerchantBrain.isMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, livingentity);
            } else {
                return false;
            }
        }
    }

    private static void broadcastRetreat(TodePiglinMerchant todePiglinMerchant, LivingEntity retreatTarget) {
        getVisibleAdultPiglins(todePiglinMerchant).stream().filter((todeAbstractPiglin) ->
                todeAbstractPiglin instanceof TodePiglinMerchant).forEach((todeAbstractPiglin) ->
                retreatFromNearestTarget((TodePiglinMerchant)todeAbstractPiglin, retreatTarget));
    }

    private static void retreatFromNearestTarget(TodePiglinMerchant todePiglinMerchant, LivingEntity retreatTarget) {
        Brain<TodePiglinMerchant> todePiglinMerchantBrain = todePiglinMerchant.getBrain();
        LivingEntity $$3 = BehaviorUtils.getNearestTarget(todePiglinMerchant, todePiglinMerchantBrain.getMemory(MemoryModuleType.AVOID_TARGET), retreatTarget);
        $$3 = BehaviorUtils.getNearestTarget(todePiglinMerchant, todePiglinMerchantBrain.getMemory(MemoryModuleType.ATTACK_TARGET), $$3);
        setAvoidTargetAndDontHuntForAWhile(todePiglinMerchant, $$3);
    }

    private static boolean piglinsEqualOrOutnumberHoglins(TodePiglinMerchant todePiglinMerchant) {
        return !hoglinsOutnumberPiglins(todePiglinMerchant);
    }

    private static boolean hoglinsOutnumberPiglins(TodePiglinMerchant todePiglinMerchant) {
        int i = todePiglinMerchant.getBrain().getMemory(ModMemoryTypes.VISIBLE_ADULT_TODEPIGLIN_COUNT.get()).orElse(0) + 1;
        int j = todePiglinMerchant.getBrain().getMemory(ModMemoryTypes.VISIBLE_ADULT_TODEPIGLIN_COUNT.get()).orElse(0);
        return j > i;
    }

    private static void setAvoidTargetAndDontHuntForAWhile(TodePiglinMerchant todePiglinMerchant, LivingEntity pTarget) {
        todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
        todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, pTarget, RETREAT_DURATION.sample(todePiglinMerchant.level.random));
        dontKillAnyMoreHoglinsForAWhile(todePiglinMerchant);
    }

    public static void dontKillAnyMoreHoglinsForAWhile(TodeAbstractPiglin todeAbstractPiglin) {
        todeAbstractPiglin.getBrain().setMemoryWithExpiry(
                MemoryModuleType.HUNTED_RECENTLY, true, TIME_BETWEEN_HUNTS.sample(todeAbstractPiglin.level.random));
    }

    public static boolean seesPlayerHoldingWantedItem(Player todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
    }

    // would be amusing if this piglin type ate more often
    private static void eat(TodePiglinMerchant todePiglinMerchant) {
        todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.ATE_RECENTLY, true, EAT_COOLDOWN);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasEatenRecently(TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.ATE_RECENTLY);
    }

    private static Vec3 getRandomNearbyPos(TodePiglinMerchant todePiglinMerchant) {
        Vec3 vec3 = LandRandomPos.getPos(todePiglinMerchant, 4, 2);
        return vec3 == null ? todePiglinMerchant.position() : vec3;
    }

    public static boolean isIdle(TodeAbstractPiglin todeAbstractPiglin) {
        return todeAbstractPiglin.getBrain().isActive(Activity.IDLE);
    }

    public static boolean hasCrossbow(LivingEntity livingEntity) {
        return livingEntity.isHolding(is -> is.getItem() instanceof CrossbowItem);
    }

    public static void admireGoldItem(LivingEntity todePiglinMerchant) {
        todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.ADMIRING_ITEM, true, ADMIRE_DURATION);
    }

    public static boolean isAdmiringItem(TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.ADMIRING_ITEM);
    }

    public static void isBarterCurrency() {
        TodeAbstractPiglin.getBarterItems();
        TodePiglinBarterCurrencySensor.isBarterCurrency();
    }

    private static boolean isFood(ItemStack pStack) {
        return pStack.is(ItemTags.PIGLIN_FOOD);
    }

    private static boolean isNearRepellent(TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_REPELLENT);
    }

    private static boolean seesPlayerHoldingLovedItem(LivingEntity todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
    }

    private static boolean doesntSeeAnyPlayerHoldingLovedItem(LivingEntity player) {
        return !seesPlayerHoldingLovedItem(player);
    }

    public static boolean isPlayerHoldingLovedItem(LivingEntity player) {
        return player.getType() == EntityType.PLAYER && player.isHolding(TodePiglinMerchantAi::isLovedItem);
    }

    private static boolean isAdmiringDisabled(TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.ADMIRING_DISABLED);
    }

    private static boolean wasHurtRecently(LivingEntity todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
    }

    private static boolean isHoldingItemInOffHand(TodePiglinMerchant todePiglinMerchant) {
        return !todePiglinMerchant.getOffhandItem().isEmpty();
    }

    private static boolean isNotHoldingLovedItemInOffHand(TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getOffhandItem().isEmpty() || !isLovedItem(todePiglinMerchant.getOffhandItem());
    }

    public static boolean isZombified(EntityType<?> pEntityType) {
        return pEntityType == EntityType.ZOMBIFIED_PIGLIN || pEntityType == EntityType.ZOGLIN;
    }
}