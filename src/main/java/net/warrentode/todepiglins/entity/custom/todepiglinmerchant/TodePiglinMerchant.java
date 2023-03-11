package net.warrentode.todepiglins.entity.custom.todepiglinmerchant;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.*;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.*;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearestItemSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.warrentode.todepiglins.client.todepiglinmerchant.TodePiglinMerchantArmPose;
import net.warrentode.todepiglins.entity.custom.brain.ModMemoryTypes;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.bartering.StartAdmiring;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.bartering.StopAdmireTired;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.bartering.StopAdmiringTooFar;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.bartering.StopHolding;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.hunting.RememberDeadHoglin;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.hunting.StartCelebrating;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.hunting.StartHoglinHunt;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.hunting.StopAngerUponDeadTarget;
import net.warrentode.todepiglins.entity.custom.brain.sensors.TodePiglinBarterCurrencySensor;
import net.warrentode.todepiglins.entity.custom.brain.sensors.TodePiglinSpecificSensor;
import net.warrentode.todepiglins.util.ModTags;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.*;

import static software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes.*;

public class TodePiglinMerchant extends Monster implements SmartBrainOwner<TodePiglinMerchant>, IAnimatable, CrossbowAttackMob, InventoryCarrier {
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING_CROSSBOW =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_DANCING =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt TIME_BETWEEN_HUNTS = TimeUtil.rangeOfSeconds(30, 120);
    private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);
    private static final int MAX_HEALTH = 50;
    private static final double MOVEMENT_SPEED = 0.25;
    private static final double KNOCKBACK_RESISTANCE = 1;
    private static final int MELEE_ATTACK_COOLDOWN = 20;
    private static final double ATTACK_DAMAGE = 7.0D;
    private static final float CROSSBOW_POWER = 1.6F;
    private static final int MIN_DESIRED_DIST_FROM_TARGET_WHEN_HOLDING_CROSSBOW = 5;
    private static final float SPEED_WHEN_STRAFING_BACK_FROM_TARGET = 0.75F;
    private static final float CHANCE_OF_WEARING_EACH_ARMOUR_ITEM = 0.1F;
    private static final double PROBABILITY_OF_SPAWNING_WITH_CROSSBOW_INSTEAD_OF_SWORD = 0.5D;
    public static final int DESIRED_DISTANCE_FROM_REPELLENT = 8;
    public static final int REPELLENT_DETECTION_RANGE_HORIZONTAL = 8;
    public static final int REPELLENT_DETECTION_RANGE_VERTICAL = 4;
    private static final double PLAYER_ANGER_RANGE = 16.0D;
    private static final int ANGER_DURATION = 600;
    public static final int ADMIRE_DURATION = 120;
    private static final int MAX_ADMIRE_DISTANCE = 9;
    private static final int MAX_ADMIRE_TIME_TO_REACH = 200;
    private static final int ADMIRE_DISABLE_TIME = 200;
    public static final int CELEBRATION_TIME = 300;
    private static final float PROBABILITY_OF_CELEBRATION_DANCE = 0.1F;
    private static final int HIT_BY_PLAYER_MEMORY_TIMEOUT = 400;
    private static final int EAT_COOLDOWN = 200;
    private static final int DESIRED_DISTANCE_FROM_ZOMBIFIED = 6;
    private static final float SPEED_MULTIPLIER_WHEN_AVOIDING = 1.0F;
    private static final double AVOID_DISTANCE = 12.0D;
    private static final float MAX_LOOK_DIST_FOR_PLAYER_HOLDING_LOVED_ITEM = 14.0F;
    private static final int INTERACTION_RANGE = 8;
    private static final float SPEED_IDLE = 0.6F;
    public final SimpleContainer inventory = new SimpleContainer(8);
    public static @NotNull Ingredient getBarterItems() {
        return Ingredient.of(ModTags.Items.PIGLIN_BARTER_ITEMS);
    }
    public static final Item BARTERING_ITEM = ItemStack.EMPTY.getItem();
    AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private boolean cannotHunt;
    public TodePiglinMerchant(EntityType<? extends TodePiglinMerchant> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setCanPickUpLoot(true);
        this.applyOpenDoorsAbility();
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.xpReward = 10;
    }

    @SuppressWarnings("unused")
    public static boolean checkTodePiglinMerchantSpawnRules(EntityType<TodePiglinMerchant> todePiglinMerchant, @NotNull LevelAccessor pLevel,
                                                            MobSpawnType pSpawnType, @NotNull BlockPos pPos, RandomSource pRandom) {
        return !pLevel.getBlockState(pPos.below()).is(Blocks.NETHER_WART_BLOCK);
    }

    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty,
                                        @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag nbtTag) {
        RandomSource randomsource = pLevel.getRandom();
        if (pReason != MobSpawnType.STRUCTURE) {
            this.setItemSlot(EquipmentSlot.MAINHAND, this.createSpawnWeapon());
        }
        this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(randomsource, pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, nbtTag);
    }
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return !this.isPersistenceRequired();
    }

    // Basic Abilities and Stats
    public static @NotNull AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE).build();
    }
    public int getExperienceReward() {
        return this.xpReward;
    }
    private void applyOpenDoorsAbility() {
        if (GoalUtils.hasGroundPathNavigation(this)) {
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        }
    }
    public boolean isAdult() {
        return !this.isBaby();
    }
    private @NotNull ItemStack createSpawnWeapon() {
        return (double)this.random.nextFloat() < PROBABILITY_OF_SPAWNING_WITH_CROSSBOW_INSTEAD_OF_SWORD ?
                new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
    }
    protected void populateDefaultEquipmentSlots(@NotNull RandomSource pRandom, @NotNull DifficultyInstance pDifficulty) {
        this.maybeWearArmor(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET), pRandom);
        this.maybeWearArmor(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE), pRandom);
        this.maybeWearArmor(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS), pRandom);
        this.maybeWearArmor(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS), pRandom);
    }
    private void maybeWearArmor(EquipmentSlot pSlot, ItemStack stack, @NotNull RandomSource pRandom) {
        if (pRandom.nextFloat() < CHANCE_OF_WEARING_EACH_ARMOUR_ITEM) {
            this.setItemSlot(pSlot, stack);
        }
    }

    // synced data management
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHARGING_CROSSBOW, false);
        this.entityData.define(DATA_IS_DANCING, false);
    }

    // hunting  switch
    private void setCannotHunt(boolean pCannotHunt) {
        this.cannotHunt = pCannotHunt;
    }
    protected boolean canHunt() {
        return !this.cannotHunt;
    }

    // dance switch
    public boolean isDancing() {
        return this.entityData.get(DATA_IS_DANCING);
    }
    public void setDancing(boolean pDancing) {
        this.entityData.set(DATA_IS_DANCING, pDancing);
    }

    // inventory management
    @Override
    @VisibleForDebug
    public @NotNull SimpleContainer getInventory() {
        return this.inventory;
    }
    public void addToInventory(ItemStack stack) {
        this.inventory.addItem(stack);
    }
    protected boolean canAddToInventory(ItemStack stack) {
        return this.inventory.canAddItem(stack);
    }
    private static void putInInventory(@NotNull TodePiglinMerchant todePiglinMerchant, ItemStack stack) {
        todePiglinMerchant.addToInventory(stack);
        throwItemsTowardRandomPos(todePiglinMerchant, Collections.singletonList(stack));
    }
    protected void dropCustomDeathLoot(@NotNull DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
    }

    //save
    public void addAdditionalSaveData(@NotNull CompoundTag nbtTag) {
        super.addAdditionalSaveData(nbtTag);
        if (this.cannotHunt) {
            nbtTag.putBoolean("CannotHunt", true);
        }
        nbtTag.put("Inventory", this.inventory.createTag());
    }

    //load
    public void readAdditionalSaveData(@NotNull CompoundTag nbtTag) {
        super.readAdditionalSaveData(nbtTag);
        this.setCannotHunt(nbtTag.getBoolean("CannotHunt"));
        this.inventory.fromTag(nbtTag.getList("Inventory", 10));
    }

    /** Start of the Brain Block **/
    @Override
    protected @NotNull Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }
    @Override
    public List<ExtendedSensor<TodePiglinMerchant>> getSensors() {
        //noinspection ConstantValue
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<TodePiglinMerchant>()
                        .setPredicate((target, entity)->
                                target instanceof Player ||
                                        target instanceof Zombie ||
                                        target instanceof ZombieHorse ||
                                        target instanceof ZombieVillager ||
                                        target instanceof ZombifiedPiglin ||
                                        target instanceof Hoglin ||
                                        target instanceof WitherBoss ||
                                        target instanceof WitherSkeleton ||
                                        target instanceof AbstractPiglin ||
                                        target instanceof TodePiglinMerchant),
                new NearbyPlayersSensor<>(),
                new NearbyBlocksSensor<>(),
                new NearestItemSensor<>(),
                new HurtBySensor<>(),
                new TodePiglinBarterCurrencySensor<>(TodePiglinMerchant.getBarterItems()),
                new TodePiglinSpecificSensor<>()
        );
    }
    @Override
    public BrainActivityGroup<TodePiglinMerchant> getCoreTasks() {
        //noinspection unchecked,ConstantValue
        return BrainActivityGroup.coreTasks(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new InteractWithDoor(),
                new AvoidEntity<>().startCondition((entity)->
                        entity instanceof Zombie ||
                                entity instanceof ZombieVillager ||
                                entity instanceof ZombieHorse ||
                                entity instanceof ZombifiedPiglin).whenStopping(TodePiglinMerchant::wantsToStopFleeing),
                new SetWalkTargetAwayFrom<>(MemoryModuleType.NEAREST_REPELLENT, SPEED_MULTIPLIER_WHEN_AVOIDING, DESIRED_DISTANCE_FROM_REPELLENT, false, Vec3::atBottomCenterOf),
                new StopHolding(),
                new StartAdmiring(ADMIRE_DURATION)
                        .whenStarting((TodePiglinMerchant) -> new WalkOrRunToWalkTarget<>())
                        .whenStopping((TodePiglinMerchant) -> new StopAdmiringTooFar(MAX_ADMIRE_DISTANCE)
                                .whenStopping((todePiglinMerchant -> new StopAdmireTired(MAX_ADMIRE_TIME_TO_REACH, ADMIRE_DISABLE_TIME)))),
                new StartCelebrating(CELEBRATION_TIME, TodePiglinMerchant::wantsToDance)
                        .startCondition(TodePiglinMerchant::isDancing)
                        .whenStarting((TodePiglinMerchant) -> new MoveToWalkTarget<>()),
                new StopAngerUponDeadTarget()
        );
    }
    @Override
    public BrainActivityGroup<TodePiglinMerchant> getIdleTasks() {
        //noinspection unchecked
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new TargetOrRetaliate<>().useMemory(MemoryModuleType.HURT_BY_ENTITY),
                        new TargetOrRetaliate<>().useMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER),
                        new SetAttackTarget<>().attackPredicate(TodePiglinMerchant::isNearestValidAttackTarget),
                        new StartHoglinHunt().startCondition(TodePiglinMerchant::canHunt),
                        new SetPlayerLookTarget<>().predicate(TodePiglinMerchant::isPlayerHoldingLovedItem)
                ),
                new OneRandomBehaviour<>(
                        new SetRandomLookTarget<>(),
                        new SetPlayerLookTarget<>().startCondition(TodePiglinMerchant::seesPlayerHoldingLovedItem),
                        new SetRandomWalkTarget<>().speedModifier(SPEED_IDLE).stopIf(TodePiglinMerchant::stopWalking),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
                )
        );
    }

    @Override
    public BrainActivityGroup<TodePiglinMerchant> getFightTasks() {
        //noinspection unchecked
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>().invalidateIf((livingEntity, livingEntity1) -> !isNearestValidAttackTarget(this)),
                new FirstApplicableBehaviour<>(
                        new AnimatableMeleeAttack<>(MELEE_ATTACK_COOLDOWN)
                                .whenStarting(entity -> setAggressive(true))
                                .whenStopping(entity -> setAggressive(false)),
                        new AnimatableRangedAttack<>(25)
                                .startCondition(TodePiglinMerchant::isHoldingCrossbow)
                                .whenStarting(entity -> setAggressive(true))
                                .whenStopping(entity -> setAggressive(false)),
                        new StrafeTarget<>().startCondition(TodePiglinMerchant::isHoldingCrossbow),
                        new StayWithinDistanceOfAttackTarget<>()
                                .minDistance(MIN_DESIRED_DIST_FROM_TARGET_WHEN_HOLDING_CROSSBOW)
                                .startCondition(TodePiglinMerchant::isHoldingCrossbow)
                ),
                new RememberDeadHoglin()
        );
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public Set<Activity> getAlwaysRunningActivities() {
        return SmartBrainOwner.super.getAlwaysRunningActivities();
    }
    @SuppressWarnings("EmptyMethod")
    @Override
    public Activity getDefaultActivity() {
        return SmartBrainOwner.super.getDefaultActivity();
    }
    @Override
    public void handleAdditionalBrainSetup(SmartBrain<TodePiglinMerchant> brain) {
        SmartBrainOwner.super.handleAdditionalBrainSetup(brain);
        int i = TIME_BETWEEN_HUNTS.sample(RandomSource.create());
        BrainUtils.setForgettableMemory(brain, MemoryModuleType.HUNTED_RECENTLY, true, i);
    }
    @Override
    protected void customServerAiStep() {
        if (!BrainUtils.hasMemory(this.brain, MemoryModuleType.CELEBRATE_LOCATION)) {
            BrainUtils.setMemory(this.brain, MemoryModuleType.DANCING, false);
        } else {
            BrainUtils.setMemory(this.brain, MemoryModuleType.DANCING, true);
        }

        // must be here to tick the brain server side //
        tickBrain(this);
    }
    /** End of the Brain Block **/

    // food and hunger
    private static boolean isFood(@NotNull ItemStack pStack) {
        return pStack.is(ItemTags.PIGLIN_FOOD);
    }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasEatenRecently(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.ATE_RECENTLY);
    }
    // would be amusing if this piglin type ate more often
    @SuppressWarnings("SameReturnValue")
    private static boolean eat(@NotNull TodePiglinMerchant todePiglinMerchant) {
        Level level = todePiglinMerchant.level;
        ItemStack stack = ItemStack.EMPTY;
        if (stack.isEdible()) {
            BrainUtils.setForgettableMemory(todePiglinMerchant,MemoryModuleType.ATE_RECENTLY, true, EAT_COOLDOWN);
            for(Pair<MobEffectInstance, Float> pair : Objects.requireNonNull(stack.getFoodProperties(todePiglinMerchant)).getEffects()) {
                if (!level.isClientSide && pair.getFirst() != null && level.random.nextFloat() < pair.getSecond()) {
                    todePiglinMerchant.addEffect(new MobEffectInstance(pair.getFirst()));
                }
            }
            stack.shrink(1);
            todePiglinMerchant.gameEvent(GameEvent.EAT);
        }
        return false;
    }

    /** this may also be where right-clicking empty-handed opens up a trade or bartering window?
     * creating a bartering GUI might bypass a lot of issues actually -
     * at first it seemed weird to me that this takes two separate methods to make this exchange happen
     * but when I got to looking at them together in the same file next to each other, what I see happening
     * is a couple of checks being made, one primary check for server side and the other being made
     * for whatever the entity is holding -
     * to compare this with something like a furnace, one method is basic happening before opening the GUI
     * and the other happens after it's opened (I think)
     * in any case, whenever I try to tinker with it, it becomes a pain so for now I'm going to leave it as they are
     * with this note until I decide if it needs to stay as is or be changed **/
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        InteractionResult interactionresult = super.mobInteract(player, hand);
        if (interactionresult.consumesAction()) {
            return interactionresult;
        } else if (!this.level.isClientSide) {
            return TodePiglinMerchant.mobInteract(this, player, hand);
        } else {
            //checks if the item the player is holding while clicking on this entity is a loved item and if the entity is able to admire it
            //may need to come back to edit this for barter currency specifically?
            boolean flag = TodePiglinMerchant.canAdmire(this, player.getItemInHand(hand)) &&
                    !TodePiglinMerchant.isLovedItem(this.getOffhandItem()) &&
                    (this.getArmPose() != TodePiglinMerchantArmPose.ADMIRING_ITEM);
            return flag ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
    }

    /** this looks like where the piglin merchant entity can take a barter item or loved item from the player directly
     * possibly build a merchant trader screen and maybe make good use of the currency sensor with this **/
    public static InteractionResult mobInteract(TodePiglinMerchant todePiglinMerchant, @NotNull Player pPlayer, InteractionHand pHand) {
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

    // bartering block
    public boolean wantsToPickUp(@NotNull ItemStack stack) {
        return ForgeEventFactory.getMobGriefingEvent(this.level, this) &&
                this.canPickUpLoot() && TodePiglinMerchant.wantsToPickup(this, stack);
    }
    protected static boolean wantsToPickup(@NotNull TodePiglinMerchant todePiglinMerchant, @NotNull ItemStack stack) {
        if (stack.is(ItemTags.PIGLIN_REPELLENTS)) {
            return false;
        } else if (isAdmiringDisabled(todePiglinMerchant) && todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
            return false;
        } else if (stack.is(ItemTags.PIGLIN_LOVED)) {
            return isNotHoldingLovedItemInOffHand(todePiglinMerchant);
        } else {
            boolean flag = todePiglinMerchant.canAddToInventory(stack);
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
    /** the only difference between the holding wanted item and holding loved item is the loved item activates a sound effect **/
    public static boolean seesPlayerHoldingWantedItem(@NotNull Player todePiglinMerchant) {
        return BrainUtils.hasMemory(todePiglinMerchant, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
    }
    private static boolean seesPlayerHoldingLovedItem(@NotNull LivingEntity todePiglinMerchant) {
        return BrainUtils.hasMemory(todePiglinMerchant, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
    }
    public static boolean isPlayerHoldingLovedItem(@NotNull LivingEntity player) {
        return player.getType() == EntityType.PLAYER && player.isHolding(TodePiglinMerchant::isLovedItem);
    }
    private static boolean isAdmiringDisabled(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return BrainUtils.hasMemory(todePiglinMerchant, MemoryModuleType.ADMIRING_DISABLED);
    }
    protected static boolean canAdmire(TodePiglinMerchant todePiglinMerchant, ItemStack stack) {
        return !isAdmiringDisabled(todePiglinMerchant) && !isAdmiringItem(todePiglinMerchant)
                && stack.is(ItemTags.PIGLIN_LOVED);
    }
    public static void admireGoldItem(@NotNull TodePiglinMerchant todePiglinMerchant) {
        BrainUtils.setForgettableMemory(todePiglinMerchant, MemoryModuleType.ADMIRING_ITEM, true, ADMIRE_DURATION);
    }
    public static boolean isAdmiringItem(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return BrainUtils.hasMemory(todePiglinMerchant, MemoryModuleType.ADMIRING_ITEM);
    }

    /** this is where the "barter recipe" check begins **/
    public static void isBarterCurrency() {
        TodePiglinMerchant.getBarterItems();
        TodePiglinBarterCurrencySensor.isBarterCurrency();
    }

    /** two methods are here for pickUpItem because I merged the main and abstract files and I'm not sure how to
     * get the method to light up with just the one so I might do something with the first one, like a sound effect
     * or what have you later down the road, we'll see **/
    protected void pickUpItem(@NotNull ItemEntity stack) {
        this.onItemPickup(stack);
        TodePiglinMerchant.pickUpItem(this, stack);
    }
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
                putInInventory(todePiglinMerchant, itemstack);
            }
        }
    }
    /** this is where the barter interaction event happens **/
    public static void stopHoldingOffHandItem(@NotNull TodePiglinMerchant todePiglinMerchant, boolean pShouldBarter) {
        ItemStack stack = todePiglinMerchant.getItemInHand(InteractionHand.OFF_HAND);
        todePiglinMerchant.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        if (todePiglinMerchant.isAdult()) {
            boolean flag = (stack.is(ModTags.Items.PIGLIN_BARTER_ITEMS));
            if (pShouldBarter && flag) {
                //accepts the trade - happy emote here & and gives an item
                //particle emote set in sensor
                throwItems(todePiglinMerchant, getBarterResponseItems(todePiglinMerchant));
            } else if (!flag) {
                //accepts the gift, pleasant emote
                //particle emote set in sensor
                boolean flag1 = todePiglinMerchant.equipItemIfPossible(stack);
                if (!flag1) {
                    //particle emote set in sensor
                    putInInventory(todePiglinMerchant, stack);
                }
            }
        } else {
            boolean flag2 = todePiglinMerchant.equipItemIfPossible(stack);
            if (!flag2) {
                ItemStack stack1 = todePiglinMerchant.getMainHandItem();
                if (isLovedItem(stack1)) {
                    putInInventory(todePiglinMerchant, stack1);
                } else {
                    throwItems(todePiglinMerchant, Collections.singletonList(stack1));
                }
                todePiglinMerchant.holdInMainHand(stack);
            }
        }
    }

    /** if I can get the custom currency working then maybe I can then get custom loot tables running too
     * it's very likely that I can get this running with the sensor **/
    private static @NotNull List<ItemStack> getBarterResponseItems(TodePiglinMerchant todePiglinMerchant) {
        //maybe dance?
        barterDance(todePiglinMerchant);
        LootTable lootTable = Objects.requireNonNull(todePiglinMerchant.level.getServer()).getLootTables().get(BuiltInLootTables.PIGLIN_BARTERING);
        return lootTable.getRandomItems((
                new LootContext.Builder((ServerLevel)todePiglinMerchant.level))
                .withParameter(LootContextParams.THIS_ENTITY, todePiglinMerchant)
                .withRandom(todePiglinMerchant.level.random).create(LootContextParamSets.PIGLIN_BARTER));
    }

    /** I don't know if I can get this to work, but I want it to for the merchant **/
    private static void barterDance(@NotNull TodePiglinMerchant todePiglinMerchant) {
        float DanceChance;
        DanceChance = RandomSource.create(todePiglinMerchant.level.getGameTime()).nextFloat();

        if (DanceChance < PROBABILITY_OF_CELEBRATION_DANCE) {
            Vec3 vec3 = todePiglinMerchant.getDeltaMovement();
            todePiglinMerchant.level.addParticle(ParticleTypes.SNEEZE,
                    todePiglinMerchant.getX() - (todePiglinMerchant.getBbWidth() + 1.0F) * 0.5D *
                            (double) Mth.sin(todePiglinMerchant.yBodyRot * ((float)Math.PI / 180F)),
                    todePiglinMerchant.getEyeY() - (double)0.1F, todePiglinMerchant.getZ() +
                            (todePiglinMerchant.getBbWidth() + 1.0F) * 0.5D *
                                    (double)Mth.cos(todePiglinMerchant.yBodyRot * ((float)Math.PI / 180F)), vec3.x, 0.0D, vec3.z);
            todePiglinMerchant.playSound(SoundEvents.PANDA_SNEEZE, 1.0F, 1.0F);
        }
        else if (DanceChance > PROBABILITY_OF_CELEBRATION_DANCE) {
            todePiglinMerchant.setDancing(true);
        }
    }

    // item shuffle as the entity examines what it has in hand and decides what to do with it
    protected void holdInMainHand(ItemStack stack) {
        this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, stack);
    }
    protected void holdInOffHand(@NotNull ItemStack stack) {
        if (stack.is(ItemTags.PIGLIN_LOVED)) {
            this.setItemSlot(EquipmentSlot.OFFHAND, stack);
            this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
        } else {
            this.setItemSlotAndDropWhenKilled(EquipmentSlot.OFFHAND, stack);
        }
    }
    private static void holdInOffhand(TodePiglinMerchant todePiglinMerchant, ItemStack stack) {
        if (isHoldingItemInOffHand(todePiglinMerchant)) {
            todePiglinMerchant.spawnAtLocation(todePiglinMerchant.getItemInHand(InteractionHand.OFF_HAND));
        }
        todePiglinMerchant.holdInOffHand(stack);
    }
    protected boolean canReplaceCurrentItem(ItemStack pCandidate) {
        EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(pCandidate);
        ItemStack itemstack = this.getItemBySlot(equipmentslot);
        return this.canReplaceCurrentItem(pCandidate, itemstack);
    }
    protected boolean canReplaceCurrentItem(@NotNull ItemStack pCandidate, @NotNull ItemStack pExisting) {
        if (EnchantmentHelper.hasBindingCurse(pExisting)) {
            return false;
        } else {
            boolean flag = TodePiglinMerchant.isLovedItem(pCandidate) || pCandidate.is(Items.CROSSBOW);
            boolean flag1 = TodePiglinMerchant.isLovedItem(pExisting) || pExisting.is(Items.CROSSBOW);
            if (flag && !flag1) {
                return true;
            } else if (!flag && flag1) {
                return false;
            } else {
                return (pCandidate.is(Items.CROSSBOW) || !pExisting.is(Items.CROSSBOW)) &&
                        super.canReplaceCurrentItem(pCandidate, pExisting);
            }
        }
    }
    private static @NotNull ItemStack removeOneItemFromItemEntity(@NotNull ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        ItemStack stack1 = stack.split(1);
        if (stack.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(stack);
        }
        return stack1;
    }
    public static boolean isLovedItem(@NotNull ItemStack stack) {
        return stack.is(ItemTags.PIGLIN_LOVED);
    }
    private static boolean isNotHoldingLovedItemInOffHand(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getOffhandItem().isEmpty() || !isLovedItem(todePiglinMerchant.getOffhandItem());
    }
    private static void throwItems(@NotNull TodePiglinMerchant todePiglinMerchant, List<ItemStack> stacks) {
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
    private static void throwItemsTowardPlayer(TodePiglinMerchant todePiglinMerchant, @NotNull Player pPlayer, List<ItemStack> stacks) {
        throwItemsTowardPos(todePiglinMerchant, stacks, pPlayer.position());
    }
    private static void throwItemsTowardPos(TodePiglinMerchant todePiglinMerchant, @NotNull List<ItemStack> stacks, Vec3 pPos) {
        if (!stacks.isEmpty()) {
            todePiglinMerchant.swing(InteractionHand.OFF_HAND);
            for(ItemStack itemstack : stacks) {
                BehaviorUtils.throwItem(todePiglinMerchant, itemstack, pPos.add(0.0D, 1.0D, 0.0D));
            }
        }
    }
    private static @NotNull Vec3 getRandomNearbyPos(TodePiglinMerchant todePiglinMerchant) {
        Vec3 vec3 = LandRandomPos.getPos(todePiglinMerchant, 4, 2);
        return vec3 == null ? todePiglinMerchant.position() : vec3;
    }

    // idleness and navigation
    private static void stopWalking(@NotNull TodePiglinMerchant todePiglinMerchant) {
        BrainUtils.clearMemory(todePiglinMerchant, MemoryModuleType.WALK_TARGET);
        todePiglinMerchant.getNavigation().stop();
    }
    private static boolean stopWalking(PathfinderMob pathfinderMob) {
        return BrainUtils.hasMemory(pathfinderMob, MemoryModuleType.WALK_TARGET);
    }

    public static boolean isIdle(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().isActive(Activity.IDLE);
    }

    // sound events
    protected SoundEvent getAmbientSound() {
        return this.level.isClientSide ? null : TodePiglinMerchant.getSoundForCurrentActivity(this).orElse(null);
    }
    public static Optional<SoundEvent> getSoundForCurrentActivity(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().getActiveNonCoreActivity().map((activity) -> getSoundForCurrentActivity(todePiglinMerchant, activity));
    }
    private static SoundEvent getSoundForCurrentActivity(TodePiglinMerchant todePiglinMerchant, Activity merchantActivity) {
        if (merchantActivity == Activity.FIGHT) {
            return SoundEvents.PIGLIN_ANGRY;
        } else if (merchantActivity == Activity.AVOID && isNearAvoidTarget(todePiglinMerchant)) {
            return SoundEvents.PIGLIN_RETREAT;
        } else if (merchantActivity == Activity.ADMIRE_ITEM) {
            return SoundEvents.PIGLIN_ADMIRING_ITEM;
        } else if (merchantActivity == Activity.CELEBRATE) {
            return SoundEvents.PIGLIN_CELEBRATE;
        } else if (seesPlayerHoldingLovedItem(todePiglinMerchant)) {
            return SoundEvents.PIGLIN_JEALOUS;
        } else if (eat(todePiglinMerchant)) {
            return SoundEvents.GENERIC_EAT;
        } else {
            return isNearRepellent(todePiglinMerchant) ? SoundEvents.PIGLIN_RETREAT : SoundEvents.PIGLIN_AMBIENT;
        }
    }

    /** ANIMATION BLOCK **/
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void registerControllers(@NotNull AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "defaultController",
                0, this::defaultPredicate));
        data.addAnimationController(new AnimationController<>(this, "meleeController",
                0, this::meleePredicate));
        data.addAnimationController(new AnimationController<>(this, "crossbowController",
                0, this::crossbowPredicate));
        data.addAnimationController(new AnimationController<>(this, "admireController",
                0, this::admirePredicate));
        data.addAnimationController(new AnimationController<>(this, "danceController",
                0, this::dancePredicate));
    }
    private PlayState defaultPredicate(@NotNull AnimationEvent<TodePiglinMerchant> event) {
        if (event.isMoving()) {
            if (this.swinging) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("walk_legs_only", LOOP));
            }
            else {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", LOOP));
            }
        }
        else {
            event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", LOOP));
        }
        return PlayState.CONTINUE;
    }
    private PlayState meleePredicate(AnimationEvent<TodePiglinMerchant> event) {
        if (getArmPose() == TodePiglinMerchantArmPose.ATTACKING_WITH_MELEE_WEAPON) {
            if (this.swinging && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
                if (isLeftHanded()) {
                    event.getController().markNeedsReload();
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("melee_left", PLAY_ONCE));
                    this.swinging = false;
                }
                else if (!isLeftHanded()) {
                    event.getController().markNeedsReload();
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("melee_right", PLAY_ONCE));
                    this.swinging = false;
                }
            }
            else if (!this.swinging && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
                if (isLeftHanded()) {
                    event.getController().markNeedsReload();
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("melee_left", HOLD_ON_LAST_FRAME));
                    this.swinging = false;
                }
                else if (!isLeftHanded()) {
                    event.getController().markNeedsReload();
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("melee_right", HOLD_ON_LAST_FRAME));
                    this.swinging = false;
                }
            }
        }
        return PlayState.CONTINUE;
    }
    private PlayState crossbowPredicate(AnimationEvent<TodePiglinMerchant> event) {
        if (getArmPose() == TodePiglinMerchantArmPose.CROSSBOW_CHARGE) {
            if (this.swinging && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
                if (isLeftHanded()) {
                    event.getController().markNeedsReload();
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("crossbow_left", HOLD_ON_LAST_FRAME));
                    this.swinging = false;
                } else if (!isLeftHanded()) {
                    event.getController().markNeedsReload();
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("crossbow_right", HOLD_ON_LAST_FRAME));
                    this.swinging = false;
                }
            } else if (getArmPose() == TodePiglinMerchantArmPose.CROSSBOW_HOLD) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", LOOP));
            }
        }
        return PlayState.CONTINUE;
    }
    private PlayState admirePredicate(AnimationEvent<TodePiglinMerchant> event) {
        if (this.swinging && isHoldingItemInOffHand(event.getAnimatable()) && getArmPose() == TodePiglinMerchantArmPose.ADMIRING_ITEM &&
                event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            if (isLeftHanded()) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("admire_left", HOLD_ON_LAST_FRAME));
                this.swinging = false;
            }
            else if (!isLeftHanded()) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("admire_right", HOLD_ON_LAST_FRAME));
                this.swinging = false;
            }
        }
        return PlayState.CONTINUE;
    }
    private PlayState dancePredicate(AnimationEvent<TodePiglinMerchant> event) {
        if (this.swinging && getArmPose() == TodePiglinMerchantArmPose.DANCING &&
                event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("dance", LOOP));
            this.swinging = false;
        }
        return PlayState.CONTINUE;
    }


    public TodePiglinMerchantArmPose getArmPose() {
        if (this.isDancing()) {
            return TodePiglinMerchantArmPose.DANCING;
        } else if (TodePiglinMerchant.isLovedItem(this.getOffhandItem())) {
            return TodePiglinMerchantArmPose.ADMIRING_ITEM;
        } else if (this.isAggressive() && this.isHoldingMeleeWeapon()) {
            return TodePiglinMerchantArmPose.ATTACKING_WITH_MELEE_WEAPON;
        } else if (this.isChargingCrossbow()) {
            return TodePiglinMerchantArmPose.CROSSBOW_CHARGE;
        } else {
            return this.isAggressive() && this.isHolding(is -> is.getItem() instanceof CrossbowItem) ?
                    TodePiglinMerchantArmPose.CROSSBOW_HOLD : TodePiglinMerchantArmPose.DEFAULT;
        }
    }

    // avoidance tactics
    public static boolean isZombified(EntityType<?> pEntityType) {
        return pEntityType == EntityType.ZOMBIFIED_PIGLIN
                || pEntityType == EntityType.ZOGLIN
                || pEntityType == EntityType.ZOMBIE
                || pEntityType == EntityType.ZOMBIE_VILLAGER
                || pEntityType == EntityType.ZOMBIE_HORSE;
    }
    private static boolean isNearRepellent(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_REPELLENT);
    }
    private static boolean isNearAvoidTarget(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return Objects.requireNonNull(BrainUtils.getMemory(todePiglinMerchant, MemoryModuleType.AVOID_TARGET))
                .closerThan(todePiglinMerchant, AVOID_DISTANCE);
    }
    private static boolean isNearZombified(LivingEntity livingEntity, LivingEntity livingEntity1) {
        if (BrainUtils.hasMemory(livingEntity, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED)) {
            LivingEntity livingentity = BrainUtils.getMemory(livingEntity,MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED);
            assert livingentity != null;
            return livingentity.closerThan(livingEntity1, DESIRED_DISTANCE_FROM_ZOMBIFIED);
        } else {
            return false;
        }
    }
    public static Optional<LivingEntity> getAvoidTarget(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET) ?
                todePiglinMerchant.getBrain().getMemory(MemoryModuleType.AVOID_TARGET) : Optional.empty();
    }
    private static void retreatFromNearestTarget(@NotNull TodePiglinMerchant todePiglinMerchant, LivingEntity retreatTarget) {
        BrainUtils.getMemory(todePiglinMerchant, MemoryModuleType.ATTACK_TARGET);
        LivingEntity nearestTarget;
        nearestTarget = retreatTarget;
        BrainUtils.setMemory(todePiglinMerchant, MemoryModuleType.AVOID_TARGET, retreatTarget);
        setAvoidTargetAndDontHuntForAWhile(todePiglinMerchant, nearestTarget);
    }
    private static void broadcastRetreat(TodePiglinMerchant todePiglinMerchant, LivingEntity retreatTarget) {
        getVisibleAdultTodePiglins(todePiglinMerchant).stream().filter(Objects::nonNull).forEach((todePiglinMerchant1) ->
                retreatFromNearestTarget(todePiglinMerchant1, retreatTarget));
    }
    private static void wantsToStopFleeing(PathfinderMob pathfinderMob) {
        TodePiglinMerchant todePiglinMerchant = (TodePiglinMerchant) pathfinderMob;
        if (!BrainUtils.hasMemory(pathfinderMob, MemoryModuleType.AVOID_TARGET)) {
            isIdle(todePiglinMerchant);
        } else {
            LivingEntity livingentity = BrainUtils.getMemory(pathfinderMob, MemoryModuleType.AVOID_TARGET);
            assert livingentity != null;
            EntityType<?> entitytype = livingentity.getType();
            if (EntityType.HOGLIN == entitytype) {
                piglinsEqualOrOutnumberHoglins(todePiglinMerchant);
            } else if (isZombified(entitytype)) {
                BrainUtils.setMemory(pathfinderMob, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, livingentity);
            }
        }
    }

    // targeting
    public static boolean isAlly(LivingEntity livingEntity, LivingEntity checkedEntity) {
        return checkedEntity instanceof AbstractPiglin;
    }

    private static boolean isNearestValidAttackTarget(LivingEntity livingEntity) {
        TodePiglinMerchant todePiglinMerchant = (TodePiglinMerchant) livingEntity;
        return isNearestValidAttackTarget(todePiglinMerchant);
    }

    private static boolean isNearestValidAttackTarget(TodePiglinMerchant todePiglinMerchant) {
        return findNearestValidAttackTarget(todePiglinMerchant).filter((livingEntity1) ->
                livingEntity1 == todePiglinMerchant).isPresent();
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(@NotNull TodePiglinMerchant livingentity) {
        @SuppressWarnings("unchecked")
        SmartBrain<TodePiglinMerchant> brain = (SmartBrain<TodePiglinMerchant>) livingentity.getBrain();
        // ignore zombie types
        if (isNearZombified(livingentity, livingentity)) {
            return Optional.empty();
        }
        // ignore allies
        else if (isAlly(livingentity, livingentity)) {
            return Optional.empty();
        }
        else {
            Optional<LivingEntity> angerTarget = BehaviorUtils.getLivingEntityFromUUIDMemory(livingentity, MemoryModuleType.ANGRY_AT);
            if (angerTarget.isPresent() && Sensor.isEntityAttackableIgnoringLineOfSight(livingentity, angerTarget.get())) {
                return angerTarget;
            }
            else {
                if (brain.hasMemoryValue(MemoryModuleType.UNIVERSAL_ANGER)) {
                    Optional<Player> playerAngerTarget = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
                    if (playerAngerTarget.isPresent()) {
                        return playerAngerTarget;
                    }
                }

                Optional<Mob> nemesis = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
                if (nemesis.isPresent()) {
                    return nemesis;
                }
                else {
                    Optional<Player> playerNoGold = brain.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
                    return playerNoGold.isPresent() && Sensor.isEntityAttackable(livingentity, playerNoGold.get()) ? playerNoGold : Optional.empty();
                }
            }
        }
    }
    public static Optional<Player> getNearestVisibleTargetablePlayer(@NotNull TodePiglinMerchant todePiglinMerchant) {
        System.out.println("targeting NEAREST_VISIBLE_ATTACKABLE_PLAYER");
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) ?
                todePiglinMerchant.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) : Optional.empty();
    }
    public static boolean isWearingGold(@NotNull Player player) {
        for(ItemStack itemstack : player.getArmorSlots()) {
            itemstack.getItem();
            if (itemstack.makesPiglinsNeutral(player)) {
                return true;
            }
        }
        return false;
    }

    /** this is based on player block interaction that is written within the block files - I may need to write an event interception
     * or some other type of behavior like a sensor to activate this method in order to replicate this vanilla piglin behavior with this entity
     * otherwise I will need it to trigger on some other interesting thing that still feels piglin appropriate in response to the player
     * need to figure out event handling for this most likely **/
    public static void angerNearbyPiglins(@NotNull Player pPlayer, boolean pAngerOnlyIfCanSee) {
        List<TodePiglinMerchant> list = pPlayer.level.getEntitiesOfClass(TodePiglinMerchant.class, pPlayer.getBoundingBox().inflate(PLAYER_ANGER_RANGE));
        list.stream().filter(TodePiglinMerchant::isIdle).filter((nearestTarget) -> !pAngerOnlyIfCanSee || BrainUtils.canSee(nearestTarget, pPlayer)).forEach((angerNearby) -> {
            if (angerNearby.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                setAngerTargetToNearestTargetablePlayerIfFound(angerNearby, pPlayer);
            } else {
                setAngerTarget(angerNearby, pPlayer);
            }
        });
    }

    // combat and animation logistics
    protected boolean isHoldingMeleeWeapon() {
        return this.getMainHandItem().getItem() instanceof TieredItem;
    }
    private static boolean isHoldingCrossbow(@NotNull LivingEntity livingEntity) {
        return livingEntity.isHolding(stack -> stack.getItem() instanceof CrossbowItem);
    }
    private static boolean isHoldingItemInOffHand(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return !todePiglinMerchant.getOffhandItem().isEmpty();
    }

    // community awareness
    private static List<TodePiglinMerchant> getAdultTodePiglins(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return BrainUtils.getMemory(todePiglinMerchant, ModMemoryTypes.NEARBY_ADULT_TODEPIGLINS.get());
    }
    private static List<TodePiglinMerchant> getVisibleAdultTodePiglins(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return BrainUtils.getMemory(todePiglinMerchant, ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get());
    }

    // crossbow specific logistics - honestly not sure if all this is needed
    /** setCharging and isCharging has to be here if I want to implement Crossbow Mob which is annoying
     * since with GeckoLib I don't need it to flag anything for me
     * idk, maybe I will just connect it to canFire and test it - can always undo it later if I need to**/
    @Override
    public void setChargingCrossbow(boolean isCharging) {
        this.entityData.set(DATA_IS_CHARGING_CROSSBOW, isCharging);
    }
    private boolean isChargingCrossbow() {
        return this.entityData.get(DATA_IS_CHARGING_CROSSBOW);
    }
    @Override
    public boolean canFireProjectileWeapon(@NotNull ProjectileWeaponItem pProjectileWeapon) {
        return pProjectileWeapon == Items.CROSSBOW;
    }
    @Override
    public void shootCrossbowProjectile(@NotNull LivingEntity target, @NotNull ItemStack crossbowStack, @NotNull Projectile bolt, float boltAngle) {
        this.shootCrossbowProjectile(this, target, bolt, boltAngle, CROSSBOW_POWER);
    }
    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }
    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float pVelocity) {
        this.performCrossbowAttack(this, CROSSBOW_POWER);
    }

    // hunting related
    private static void piglinsEqualOrOutnumberHoglins(TodePiglinMerchant todePiglinMerchant) {
        hoglinsOutnumberPiglins(todePiglinMerchant);
    }
    private static boolean hoglinsOutnumberPiglins(@NotNull TodePiglinMerchant todePiglinMerchant) {
        int i = todePiglinMerchant.getBrain().getMemory(ModMemoryTypes.VISIBLE_ADULT_TODEPIGLIN_COUNT.get()).orElse(0) + 1;
        int j = todePiglinMerchant.getBrain().getMemory(ModMemoryTypes.VISIBLE_ADULT_TODEPIGLIN_COUNT.get()).orElse(0);
        return j > i;
    }
    public static boolean hasAnyoneNearbyHuntedRecently(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY) ||
                getVisibleAdultTodePiglins(todePiglinMerchant).stream().anyMatch((brain) ->
                        todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY));
    }
    public static void dontKillAnyMoreHoglinsForAWhile(@NotNull TodePiglinMerchant todePiglinMerchant) {
        todePiglinMerchant.getBrain().setMemoryWithExpiry(
                MemoryModuleType.HUNTED_RECENTLY, true, TIME_BETWEEN_HUNTS.sample(todePiglinMerchant.level.random));
    }
    private static void setAvoidTargetAndDontHuntForAWhile(@NotNull TodePiglinMerchant todePiglinMerchant, LivingEntity target) {
        todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
        todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, target, RETREAT_DURATION.sample(todePiglinMerchant.level.random));
        dontKillAnyMoreHoglinsForAWhile(todePiglinMerchant);
    }
    public static void broadcastDontKillAnyMoreHoglinsForAWhile(TodePiglinMerchant todePiglinMerchant) {
        getVisibleAdultTodePiglins(todePiglinMerchant).forEach(TodePiglinMerchant::dontKillAnyMoreHoglinsForAWhile);
    }

    public static void broadcastAngerTarget(TodePiglinMerchant todePiglinMerchant, LivingEntity angerTarget) {
        getAdultTodePiglins(todePiglinMerchant).forEach((TodePiglinMerchant) -> {
            if (angerTarget.getType() != EntityType.HOGLIN || todePiglinMerchant.canHunt() && ((Hoglin)angerTarget).canBeHunted()) {
                setAngerTargetIfCloserThanCurrent(todePiglinMerchant, angerTarget);
            }
        });
    }
    public static boolean wantsToDance(LivingEntity livingEntity, @NotNull LivingEntity deadTarget) {
        if (deadTarget.getType() != EntityType.HOGLIN) {
            return false;
        }
        else {
            return RandomSource.create(livingEntity.level.getGameTime()).nextFloat() < PROBABILITY_OF_CELEBRATION_DANCE;
        }
    }

    // keeping score of pain
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        boolean flag = super.hurt(pSource, pAmount);
        if (this.level.isClientSide) {
            return false;
        } else {
            if (flag && pSource.getEntity() instanceof LivingEntity) {
                TodePiglinMerchant.wasHurtBy(this, (LivingEntity)pSource.getEntity());
            }
            return flag;
        }
    }
    protected static void wasHurtBy(TodePiglinMerchant todePiglinMerchant, LivingEntity hurtBy) {
        if (!(hurtBy instanceof TodePiglinMerchant) && !(hurtBy instanceof AbstractPiglin)) {
            if (isHoldingItemInOffHand(todePiglinMerchant)) {
                stopHoldingOffHandItem(todePiglinMerchant, false);
            }
            BrainUtils.clearMemory(todePiglinMerchant, MemoryModuleType.CELEBRATE_LOCATION);
            BrainUtils.clearMemory(todePiglinMerchant, MemoryModuleType.DANCING);
            BrainUtils.clearMemory(todePiglinMerchant, MemoryModuleType.ADMIRING_ITEM);
            if (hurtBy instanceof Player) {
                BrainUtils.setForgettableMemory(todePiglinMerchant, MemoryModuleType.ADMIRING_DISABLED, true, HIT_BY_PLAYER_MEMORY_TIMEOUT);
            }
            getAvoidTarget(todePiglinMerchant).ifPresent((livingEntity) -> {
                if (livingEntity.getType() != hurtBy.getType()) {
                    BrainUtils.clearMemory(todePiglinMerchant, MemoryModuleType.AVOID_TARGET);
                }
            });
            if (hurtBy.getType() == EntityType.HOGLIN && hoglinsOutnumberPiglins(todePiglinMerchant)) {
                setAvoidTargetAndDontHuntForAWhile(todePiglinMerchant, hurtBy);
                broadcastRetreat(todePiglinMerchant, hurtBy);
            } else {
                maybeRetaliate(todePiglinMerchant, hurtBy);
            }
        }
    }
    protected static void maybeRetaliate(@NotNull TodePiglinMerchant todePiglinMerchant, LivingEntity angerTarget) {
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
    protected static void broadcastUniversalAnger(TodePiglinMerchant todePiglinMerchant) {
        getAdultTodePiglins(todePiglinMerchant).forEach((todePiglinMerchant1) ->
                getNearestVisibleTargetablePlayer(todePiglinMerchant1).ifPresent((player) ->
                        setAngerTarget(todePiglinMerchant1, player)));
    }
    private static @NotNull Optional<LivingEntity> getAngerTarget(TodePiglinMerchant todePiglinMerchant) {
        return BehaviorUtils.getLivingEntityFromUUIDMemory(todePiglinMerchant, MemoryModuleType.ANGRY_AT);
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
}