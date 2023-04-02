package net.warrentode.todepiglins.entity.custom.todepiglinmerchant;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
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
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.AvoidEntity;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StayWithinDistanceOfAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.UnreachableTargetSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.warrentode.todepiglins.client.todepiglinmerchant.TodePiglinMerchantArmPose;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.StartDancing;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.bartering.StartAdmiring;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.bartering.StopAdmireTired;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.bartering.StopAdmiringTooFar;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.bartering.StopHoldingAndBarter;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.combat.SetAngerTarget;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.combat.StopAngerUponDeadTarget;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.food.EatFood;
import net.warrentode.todepiglins.entity.custom.brain.behaviors.hunting.RememberDeadHoglin;
import net.warrentode.todepiglins.entity.custom.brain.sensors.NearestWantedItemSensor;
import net.warrentode.todepiglins.entity.custom.brain.sensors.TodePiglinBarterCurrencySensor;
import net.warrentode.todepiglins.entity.custom.brain.sensors.TodePiglinSpecificSensor;
import net.warrentode.todepiglins.sounds.ModSounds;
import net.warrentode.todepiglins.util.ModTags;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.controller.AnimationController.IParticleListener;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.warrentode.todepiglins.entity.custom.brain.sensors.TodePiglinBarterCurrencySensor.isLovedItem;
import static net.warrentode.todepiglins.entity.custom.brain.sensors.TodePiglinBarterCurrencySensor.isWantedItem;
import static software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes.*;

public class TodePiglinMerchant extends Monster implements SmartBrainOwner<TodePiglinMerchant>, IAnimatable, IParticleListener<TodePiglinMerchant>, InventoryCarrier {
    private static final EntityDataAccessor<Boolean> DATA_IS_DANCING =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CANNOT_HUNT =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_WILLING_TO_BARTER =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HOLDING_ITEM =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CAN_WALK =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_EATING =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_EATING_TIME =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.INT);
    public static final UniformInt TIME_BETWEEN_HUNTS = TimeUtil.rangeOfSeconds(30, 120);
    public static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);
    private static final int MAX_HEALTH = 50;
    private static final double MOVEMENT_SPEED = 0.25;
    private static final double KNOCKBACK_RESISTANCE = 1;
    private static final int MELEE_ATTACK_COOLDOWN = 20;
    private static final double ATTACK_DAMAGE = 7.0D;
    private static final float CHANCE_OF_WEARING_EACH_ARMOUR_ITEM = 0.1F;
    private static final double PROBABILITY_OF_SPAWNING_WITH_AXE_INSTEAD_OF_SWORD = 0.5D;
    public static final int DESIRED_DISTANCE_FROM_REPELLENT = 8;
    public static final int REPELLENT_DETECTION_RANGE_HORIZONTAL = 8;
    public static final int REPELLENT_DETECTION_RANGE_VERTICAL = 4;
    public static final double PLAYER_ANGER_RANGE = 16.0D;
    public static final int ANGER_DURATION = 600;
    public static final int ADMIRE_DURATION = 120;
    private static final int MAX_ADMIRE_DISTANCE = 9;
    private static final int MAX_ADMIRE_TIME_TO_REACH = 200;
    private static final int ADMIRE_DISABLE_TIME = 200;
    public static final int CELEBRATION_TIME = 300;
    private static final float PROBABILITY_OF_CELEBRATION_DANCE = 0.1F;
    private static final int HIT_BY_PLAYER_MEMORY_TIMEOUT = 400;
    public static final int EAT_COOLDOWN = 200;
    private static final float SPEED_MULTIPLIER_WHEN_AVOIDING = 1.0F;
    private static final float SPEED_TO_WANTED_ITEM = 1.0F;
    public static final float MIN_AVOID_DISTANCE = 6.0F;
    public static final double DESIRED_AVOID_DISTANCE = 12.0D;
    private static final float SPEED_IDLE = 0.6F;
    public int foodLevel;
    public static @NotNull Ingredient getBarterItems() {
        return Ingredient.of(ModTags.Items.PIGLIN_BARTER_ITEMS);
    }
    final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public TodePiglinMerchant(EntityType<? extends TodePiglinMerchant> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setCanPickUpLoot(true);
        this.applyOpenDoorsAbility();
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA,-1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_CACTUS, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_CACTUS,-1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_OTHER,8.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_OTHER,-1.0F);
        this.setPathfindingMalus(BlockPathTypes.BLOCKED,-1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER,8.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES,-1.0F);
        this.setPathfindingMalus(BlockPathTypes.BREACH,4.0F);
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
        return false;
    }

    // Basic Abilities and Stats
    public static @NotNull AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE).build();
    }
    private void applyOpenDoorsAbility() {
        if (GoalUtils.hasGroundPathNavigation(this)) {
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        }
    }
    public boolean isAdult() {
        return !this.isBaby();
    }
    public boolean isPreventingPlayerRest(@NotNull Player pPlayer) {
        return false;
    }

    public int getExperienceReward() {
        return this.xpReward;
    }

    protected void dropCustomDeathLoot(@NotNull DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
    }
    private @NotNull ItemStack createSpawnWeapon() {
        return (double)this.random.nextFloat() < PROBABILITY_OF_SPAWNING_WITH_AXE_INSTEAD_OF_SWORD ?
                new ItemStack(Items.GOLDEN_AXE) : new ItemStack(Items.GOLDEN_SWORD);
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

    // hunting  switch
    public void setCannotHunt(boolean pCannotHunt) {
        this.entityData.set(DATA_CANNOT_HUNT, pCannotHunt);
    }
    public boolean canHunt() {
        return !this.entityData.get(DATA_CANNOT_HUNT);
    }
    // dance switch
    public void setDancing(boolean pDancing) {
        this.entityData.set(DATA_IS_DANCING, pDancing);
    }
    public boolean isDancing() {
        return this.entityData.get(DATA_IS_DANCING);
    }
    // barter  switch
    public void setWillingToBarter(boolean pWillingToBarter) {
        this.entityData.set(DATA_WILLING_TO_BARTER, pWillingToBarter);
    }
    public boolean isWillingToBarter() {
        return !this.entityData.get(DATA_WILLING_TO_BARTER);
    }
    // holding  switch
    public void setHoldingItem(boolean pHoldingItem) {
        this.entityData.set(DATA_HOLDING_ITEM, pHoldingItem);
    }
    public boolean isHoldingItem() {
        return this.entityData.get(DATA_HOLDING_ITEM);
    }
    // walk  switch
    public void setCanWalk(boolean pCanWalk) {
        this.entityData.set(DATA_CAN_WALK, pCanWalk);
    }
    public boolean canWalk() {
        return this.entityData.get(DATA_CAN_WALK);
    }
    // eating  switches
    public void setIsEating(boolean pIsEating) {
        this.entityData.set(DATA_IS_EATING, pIsEating);
    }
    public boolean isEating() {
        return this.entityData.get(DATA_IS_EATING);
    }
    public void setEatingTime(int pEatingTime) {
        this.entityData.set(DATA_EATING_TIME, pEatingTime);
    }
    public int getEatingTime() {
        return this.entityData.get(DATA_EATING_TIME);
    }

    // synced data management
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CANNOT_HUNT, false);
        this.entityData.define(DATA_IS_DANCING, false);
        this.entityData.define(DATA_WILLING_TO_BARTER, true);
        this.entityData.define(DATA_HOLDING_ITEM, false);
        this.entityData.define(DATA_CAN_WALK, true);
        this.entityData.define(DATA_IS_EATING, false);
        this.entityData.define(DATA_EATING_TIME, 0);
    }
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
    }

    // inventory management
    // TODO set up item handler capability for better inventory management? It's kind of a fucking mess...
    public final SimpleContainer inventory = new SimpleContainer(8);
    @Override
    @VisibleForDebug
    public @NotNull SimpleContainer getInventory() {
        return this.inventory;
    }
    public @NotNull SlotAccess getSlot(int pSlot) {
        int i = pSlot - 300;
        return i >= 0 && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(pSlot);
    }
    public void addToInventory(ItemStack stack) {
        this.inventory.addItem(stack);
    }
    public boolean canAddToInventory(ItemStack stack) {
        return this.inventory.canAddItem(stack);
    }
    private static void putInInventory(@NotNull TodePiglinMerchant todePiglinMerchant, ItemStack stack) {
        if (todePiglinMerchant.canAddToInventory(stack)) {
            todePiglinMerchant.addToInventory(stack);
        }
        else {
            int i;
            for (i = 0; i < todePiglinMerchant.getInventory().getContainerSize(); ++i) {
                ItemStack stack1 = todePiglinMerchant.getInventory().getItem(i);
                if (!stack1.isEmpty()) {
                    int j = stack1.getCount();

                    for (int k = j; k > stack1.getMaxStackSize(); --k) {
                        todePiglinMerchant.getInventory().removeItem(i, 1);
                        throwItemsTowardRandomPos(todePiglinMerchant, Collections.singletonList(stack));
                    }
                }
            }
        }
    }

    // read and save data
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.inventory.fromTag(pCompound.getList("Inventory", 10));

        if (pCompound.contains("FoodLevel", 1)) {
            this.foodLevel = pCompound.getByte("FoodLevel");
        }
    }
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("Inventory", this.inventory.createTag());
        pCompound.putByte("FoodLevel", (byte)this.foodLevel);
    }

    // TODO consider bringing in Hoglin riding as your son requested
    //  - at the very least make them useful in some way to the piglin merchant like the lamas are to the wandering trader
    /** BRAIN BLOCK START **/
    @Override
    protected @NotNull Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this, true, false);
    }
    @Override
    public List<ExtendedSensor<TodePiglinMerchant>> getSensors() {
        //noinspection ConstantValue
        return ObjectArrayList.of(
                new NearbyPlayersSensor<>(),
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
                                        target instanceof AbstractIllager ||
                                        target instanceof AbstractPiglin ||
                                        target instanceof TodePiglinMerchant),
                new NearbyBlocksSensor<>(),
                new NearestWantedItemSensor<>(),
                new HurtBySensor<>(),
                new UnreachableTargetSensor<>(),
                new TodePiglinBarterCurrencySensor<>(TodePiglinMerchant.getBarterItems()),
                new TodePiglinSpecificSensor<>()
        );
    }
    @Override
    public BrainActivityGroup<TodePiglinMerchant> getCoreTasks() {
        //noinspection unchecked,ConstantValue
        return BrainActivityGroup.coreTasks(
                new RememberDeadHoglin(),
                new StopAngerUponDeadTarget(),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new GoToWantedItem<>(TodePiglinMerchant::isNotHoldingWantedItemInOffHand, SPEED_TO_WANTED_ITEM, true, MAX_ADMIRE_DISTANCE),
                new InteractWithDoor(),
                new AvoidEntity<>()
                        .noCloserThan(MIN_AVOID_DISTANCE)
                        .stopCaringAfter((float) DESIRED_AVOID_DISTANCE)
                        .speedModifier(SPEED_MULTIPLIER_WHEN_AVOIDING)
                        .avoiding((entity)->
                                entity instanceof Zombie ||
                                        entity instanceof ZombieVillager ||
                                        entity instanceof ZombieHorse ||
                                        entity instanceof ZombifiedPiglin ||
                                        entity instanceof Zoglin)
                        .whenStarting((TodePiglinMerchant) -> playRetreatSound(this.getOnPos(), this.getBlockStateOn())),
                new SetWalkTargetAwayFrom<>(MemoryModuleType.NEAREST_REPELLENT,
                        SPEED_MULTIPLIER_WHEN_AVOIDING, DESIRED_DISTANCE_FROM_REPELLENT, false, Vec3::atBottomCenterOf),
                new FirstApplicableBehaviour<>(
                        new StartAdmiring(ADMIRE_DURATION),
                        new StopAdmiringTooFar(MAX_ADMIRE_DISTANCE),
                        new StopAdmireTired(MAX_ADMIRE_TIME_TO_REACH, ADMIRE_DISABLE_TIME),
                        new StopHoldingAndBarter()
                )
        );
    }
    // TODO mix it up a bit with the Dance task
    //  - maybe a few different animations to randomize?
    //  or specific ones in response to certain events?
    //  maybe even randomize the timer a bit?
    @Override
    public BrainActivityGroup<TodePiglinMerchant> getIdleTasks() {
        // noinspection unchecked
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new SetAngerTarget(),
                        new TargetOrRetaliate<>()
                                .attackablePredicate((target)->
                                        !(target instanceof TodePiglinMerchant) &&
                                                !(target instanceof AbstractPiglin) &&
                                                !(target instanceof Zombie) &&
                                                !(target instanceof ZombieHorse) &&
                                                !(target instanceof Zoglin) &&
                                                !(target.isBaby())
                                )
                                .useMemory(MemoryModuleType.HURT_BY_ENTITY)
                                .useMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD)
                                .useMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN)
                                .useMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS)
                                .isAllyIf(TodePiglinSpecificSensor::isAlliedTo)
                                .alertAlliesWhen((mob, entity) ->
                                        entity instanceof Hoglin ||
                                                entity instanceof Player ||
                                                entity instanceof AbstractIllager ||
                                                entity instanceof WitherSkeleton ||
                                                entity instanceof WitherBoss),
                        new StartDancing(CELEBRATION_TIME)
                                .startCondition(TodePiglinMerchant::wantsToDance)
                                .whenStarting((TodePiglinMerchant) -> new MoveToWalkTarget<>()),
                        new SetPlayerLookTarget<>()
                                .predicate(TodePiglinSpecificSensor::isPlayerHoldingLovedItem)
                                .whenStarting(mob -> playJealousSound(this.getOnPos(), this.getBlockStateOn()))
                ),
                new OneRandomBehaviour<>(
                        new SetRandomLookTarget<>(),
                        new SetRandomWalkTarget<>()
                                .speedModifier(SPEED_IDLE).stopIf(TodePiglinMerchant::isHoldingItemInOffHand),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
                ).startCondition(pathfinderMob -> canWalk())
                        .whenStarting((TodePiglinMerchant) -> playAmbientSound(this.getOnPos(), this.getBlockStateOn()))
        );
    }
    @Override
    public BrainActivityGroup<TodePiglinMerchant> getFightTasks() {
        //noinspection unchecked
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new FirstApplicableBehaviour<>(
                        new AnimatableMeleeAttack<>(MELEE_ATTACK_COOLDOWN)
                                .whenStarting(entity -> setAggressive(true))
                                .whenStopping(entity -> setAggressive(false)),
                        new StayWithinDistanceOfAttackTarget<>().minDistance(0.5f).maxDistance(5f)
                ).whenStarting((TodePiglinMerchant) -> playAngrySound(this.getOnPos(), this.getBlockStateOn()))
        );
    }
    @Override
    public void handleAdditionalBrainSetup(SmartBrain<TodePiglinMerchant> brain) {
        int i = TIME_BETWEEN_HUNTS.sample(RandomSource.create());
        BrainUtils.setForgettableMemory(brain, MemoryModuleType.HUNTED_RECENTLY, true, i);
        SmartBrainOwner.super.handleAdditionalBrainSetup(brain);
    }

    @Override
    protected void customServerAiStep() {
        if (!BrainUtils.hasMemory(this.brain, MemoryModuleType.CELEBRATE_LOCATION)) {
            BrainUtils.setMemory(this.brain, MemoryModuleType.DANCING, false);
            setDancing(false);
        }
        else {
            BrainUtils.setMemory(this.brain, MemoryModuleType.DANCING, true);
            setDancing(true);
        }

        if (this.entityData.get(DATA_CANNOT_HUNT) && !BrainUtils.hasMemory(this.brain, MemoryModuleType.HUNTED_RECENTLY)) {
            BrainUtils.setForgettableMemory(this,
                    MemoryModuleType.HUNTED_RECENTLY, true, TIME_BETWEEN_HUNTS.sample(this.level.random));
        }
        else {
            setCannotHunt(true);
            BrainUtils.clearMemory(this.brain, MemoryModuleType.HUNTED_RECENTLY);
        }

        if (isEating()) {
            ItemStack stack = this.getOffhandItem();
            int usingTime = this.getOffhandItem().getUseDuration();
            setEatingTime(usingTime);
            int tickCount = getEatingTime();

            while ((getEatingTime() > 0) && (tickCount > 0)) {
                tickCount--;
                setEatingTime(tickCount--);
            }
            if ((getEatingTime() <= 0) && (tickCount <= 0)) {
                getEatingTime();
                setIsEating(false);
                stack.finishUsingItem(this.level, this);
            }
        }

        setWillingToBarter(!BrainUtils.hasMemory(this, MemoryModuleType.DANCING));

        setHoldingItem(isHoldingItemInOffHand(this));

        setCanWalk(!isHoldingItem());

        updateSwingTime();
        // must be here to tick the brain server side //
        tickBrain(this);
    }

    /** SOUND EVENTS **/
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return ModSounds.TODEPIGLINMERCHANT_HURT.get();
    }
    protected SoundEvent getDeathSound() {
        return ModSounds.TODEPIGLINMERCHANT_DEATH.get();
    }
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pBlock) {
        this.playSound(ModSounds.TODEPIGLINMERCHANT_STEP.get(), 0.15F, 1.0F);
    }
    protected void playAngrySound(@NotNull BlockPos pPos, @NotNull BlockState pBlock) {
        this.playSound(ModSounds.TODEPIGLINMERCHANT_ANGER.get(), 0.15F, 1.0F);
    }
    protected void playRetreatSound(@NotNull BlockPos pPos, @NotNull BlockState pBlock) {
        this.playSound(ModSounds.TODEPIGLINMERCHANT_RETREAT.get(), 0.15F, 1.0F);
    }
    protected void playJealousSound(@NotNull BlockPos pPos, @NotNull BlockState pBlock) {
        this.playSound(ModSounds.TODEPIGLINMERCHANT_JEALOUS.get(), 0.15F, 1.0F);
    }
    protected void playAmbientSound(@NotNull BlockPos pPos, @NotNull BlockState pBlock) {
        this.playSound(ModSounds.TODEPIGLINMERCHANT_AMBIENT.get(), 0.15F, 1.0F);
    }

    /** ANIMATION BLOCK START **/
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    @Override
    public void registerControllers(@NotNull AnimationData data) {
        AnimationController<TodePiglinMerchant> defaultController = new AnimationController<>(
                this, "defaultController", 0, this::defaultPredicate);
        AnimationController<TodePiglinMerchant> admireController = new AnimationController<>(
                this, "admireController", 0, this::admirePredicate);
        AnimationController<TodePiglinMerchant> danceController = new AnimationController<>(
                this, "danceController", 0, this::dancePredicate);
        AnimationController<TodePiglinMerchant> meleeController = new AnimationController<>(
                this, "meleeController", 0, this::meleePredicate);

        admireController.registerParticleListener(this);
        danceController.registerParticleListener(this);

        admireController.registerSoundListener(this::soundListener);
        danceController.registerSoundListener(this::soundListener);

        data.addAnimationController(defaultController);
        data.addAnimationController(admireController);
        data.addAnimationController(danceController);
        data.addAnimationController(meleeController);
    }

    private void soundListener(@NotNull SoundKeyframeEvent<TodePiglinMerchant> keyframeEvent) {
        TodePiglinMerchant todePiglinMerchant = keyframeEvent.getEntity();
        LocalPlayer player = Minecraft.getInstance().player;
        if (todePiglinMerchant.level.isClientSide && player != null) {
            switch (keyframeEvent.sound) {
                case "eat" -> player.playSound(ModSounds.TODEPIGLINMERCHANT_EAT.get(), 1, 1);
                case "examine" -> player.playSound(ModSounds.TODEPIGLINMERCHANT_EXAMINE.get(), 1, 1);
                case "celebrate" -> player.playSound(ModSounds.TODEPIGLINMERCHANT_CELEBRATE.get(), 1, 1);
            }
        }
    }

    @Override
    public void summonParticle(ParticleKeyFrameEvent keyFrameEvent) {
        ItemStack stack = this.getOffhandItem();
        if (getArmPose() == TodePiglinMerchantArmPose.ACCEPT_ITEM) {
            for (int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.01D;
                double d1 = this.random.nextGaussian() * 0.01D;
                double d2 = this.random.nextGaussian() * 0.01D;
                this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.2D, this.getRandomZ(1.0D), d0, d1, d2);
            }
        }
        else if (getArmPose() == TodePiglinMerchantArmPose.REJECT_ITEM) {
            for (int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.01D;
                double d1 = this.random.nextGaussian() * 0.01D;
                double d2 = this.random.nextGaussian() * 0.01D;
                this.level.addParticle(ParticleTypes.ANGRY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.2D, this.getRandomZ(1.0D), d0, d1, d2);
            }
        }
        else if (getArmPose() == TodePiglinMerchantArmPose.EAT) {
            for (int i = 0; i < 7; ++i) {
                Vec3 vec3 = new Vec3(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, ((double) this.random.nextFloat() - 0.5D) * 0.1D);
                vec3 = vec3.xRot(-this.getXRot() * ((float) Math.PI / 180F));
                vec3 = vec3.yRot(-this.getYRot() * ((float) Math.PI / 180F));
                double d0 = (double) (-this.random.nextFloat()) * 0.6D - 0.3D;
                Vec3 vec31 = new Vec3(((double) this.random.nextFloat() - 0.5D) * 0.8D, d0, 1.0D + ((double) this.random.nextFloat() - 0.5D) * 0.4D);
                vec31 = vec31.yRot(-this.yBodyRot * ((float) Math.PI / 180F));
                vec31 = vec31.add(this.getX(), this.getEyeY() - 0.2D, this.getZ());
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);
            }
        }
        else if (getArmPose() == TodePiglinMerchantArmPose.DANCING) {
            for (int i = 0; i < 360; i++) {
                Vec3 vec3 = new Vec3(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, ((double) this.random.nextFloat() - 0.5D) * 0.1D);
                vec3 = vec3.xRot(-this.getXRot() * ((float) Math.PI / 180F));
                vec3 = vec3.yRot(-this.getYRot() * ((float) Math.PI / 180F));
                double d0 = (double) (-this.random.nextFloat()) * 0.6D - 0.3D;
                Vec3 vec31 = new Vec3(((double) this.random.nextFloat() - 0.5D) * 0.8D, d0, 1.0D + ((double) this.random.nextFloat() - 0.5D) * 0.4D);
                vec31 = vec31.yRot(-this.yBodyRot * ((float) Math.PI / 180F));
                vec31 = vec31.add(this.getX(), this.getEyeY() - 0.2D, this.getZ());
                this.level.addParticle(ParticleTypes.NOTE, vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);
            }
        }
    }

    private PlayState defaultPredicate(@NotNull AnimationEvent<TodePiglinMerchant> event) {
        if (event.isMoving()) {
            if (this.swinging) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("walk_legs_only", LOOP));
            }
            else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", LOOP));
            }
        }
        else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", LOOP));
        }
        return PlayState.CONTINUE;
    }
    private PlayState admirePredicate(AnimationEvent<TodePiglinMerchant> event) {
        // it's the opposite hand moving here since the main hand goes to the handedness of the entity
        if (this.swinging && getArmPose() == TodePiglinMerchantArmPose.ACCEPT_ITEM
                && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            if (isLeftHanded()) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("admire_right", HOLD_ON_LAST_FRAME)
                        .addRepeatingAnimation("nod_yes", 4));
            }
            else if (!isLeftHanded()) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("accept_left", HOLD_ON_LAST_FRAME)
                        .addRepeatingAnimation("nod_yes", 4));
            }
        }
        else if (this.swinging && getArmPose() == TodePiglinMerchantArmPose.REJECT_ITEM
                && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            if (isLeftHanded()) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("admire_right", HOLD_ON_LAST_FRAME)
                        .addRepeatingAnimation("nod_no", 4));
            }
            else if (!isLeftHanded()) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("reject_left", HOLD_ON_LAST_FRAME)
                        .addRepeatingAnimation("nod_no", 4));
            }
        }
        else if (this.swinging && getArmPose() == TodePiglinMerchantArmPose.EAT
                && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            if (isLeftHanded()) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("eat_right", HOLD_ON_LAST_FRAME)
                        .addAnimation("look_left", PLAY_ONCE).addAnimation("look_right", PLAY_ONCE));
            }
            else if (!isLeftHanded()) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("eat_left", HOLD_ON_LAST_FRAME)
                        .addAnimation("look_right", PLAY_ONCE).addAnimation("look_left", PLAY_ONCE));
            }
        }
        return PlayState.CONTINUE;
    }
    private PlayState dancePredicate(AnimationEvent<TodePiglinMerchant> event) {
        if (this.swinging && getArmPose() == TodePiglinMerchantArmPose.DANCING &&
                event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("dance", LOOP));
        }
        return PlayState.CONTINUE;
    }
    private PlayState meleePredicate(AnimationEvent<TodePiglinMerchant> event) {
        if (getArmPose() == TodePiglinMerchantArmPose.ATTACKING_WITH_MELEE_WEAPON) {
            if (this.swinging && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
                if (isLeftHanded()) {
                    event.getController().markNeedsReload();
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("melee_left", HOLD_ON_LAST_FRAME));
                }
                else if (!isLeftHanded()) {
                    event.getController().markNeedsReload();
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("melee_right", HOLD_ON_LAST_FRAME));
                }
            }
        }
        return PlayState.CONTINUE;
    }

    public TodePiglinMerchantArmPose getArmPose() {
        ItemStack stack = this.getItemInHand(InteractionHand.OFF_HAND);
        if (this.isDancing()) {
            return TodePiglinMerchantArmPose.DANCING;
        }
        else if (isHoldingItem() && stack.is(ItemTags.PIGLIN_FOOD)) {
            return TodePiglinMerchantArmPose.EAT;
        }
        else if (isHoldingItem() && stack.is(ModTags.Items.PIGLIN_BARTER_ITEMS)) {
            return TodePiglinMerchantArmPose.ACCEPT_ITEM;
        }
        else if (isHoldingItem() && !stack.is(ModTags.Items.PIGLIN_BARTER_ITEMS) && !stack.is(ItemTags.PIGLIN_FOOD)) {
            return TodePiglinMerchantArmPose.REJECT_ITEM;
        }
        else if (this.isAggressive() && this.isHoldingMeleeWeapon()) {
            return TodePiglinMerchantArmPose.ATTACKING_WITH_MELEE_WEAPON;
        }
        else {
            return TodePiglinMerchantArmPose.DEFAULT;
        }
    }

    // combat & other animation logistics
    protected boolean isHoldingMeleeWeapon() {
        return this.getMainHandItem().getItem() instanceof TieredItem;
    }
    private static boolean isHoldingItemInOffHand(@NotNull LivingEntity livingEntity) {
        return !livingEntity.getOffhandItem().isEmpty();
    }

    /** INSTRUCTIONS FOR THE BRAIN (AI bits) **/
    private static void stopWalking(PathfinderMob pathfinderMob) {
        if (isHoldingItemInOffHand(pathfinderMob)) {
            BrainUtils.clearMemory(pathfinderMob, MemoryModuleType.WALK_TARGET);
            pathfinderMob.getNavigation().stop();
        }
    }
    public static boolean isIdle(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getBrain().isActive(Activity.IDLE);
    }
    public static boolean wantsToDance(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return RandomSource.create(todePiglinMerchant.level.getGameTime()).nextFloat() < PROBABILITY_OF_CELEBRATION_DANCE;
    }

    // TODO setup custom trades
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
        }
        else if (!this.level.isClientSide) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (TodePiglinBarterCurrencySensor.canAdmire(this, itemStack)) {
                ItemStack itemStack1 = itemStack.split(1);
                holdInOffhand(this, itemStack1);
                TodePiglinBarterCurrencySensor.admireItem(this);
                stopWalking(this);
                setHoldingItem(true);
                return InteractionResult.CONSUME;
            }
            else {
                return InteractionResult.PASS;
            }
        }
        else {
            //checks if the item the player is holding while clicking on this entity is a loved item and if the entity is able to admire it
            //may need to come back to edit this for barter currency specifically?
            boolean flag = TodePiglinBarterCurrencySensor.canAdmire(this, player.getItemInHand(hand)) &&
                    !isWantedItem(this.getOffhandItem()) && !BrainUtils.hasMemory(this, MemoryModuleType.DANCING) &&
                    isWillingToBarter();
            return flag ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
    }

    protected void holdInMainHand(ItemStack pStack) {
        this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, pStack);
    }
    private boolean isNotHoldingWantedItemInOffHand() {
        return TodePiglinBarterCurrencySensor.isNotHoldingWantedItemInOffHand(this);
    }
    public static void holdInOffhand(@NotNull TodePiglinMerchant todePiglinMerchant, @NotNull ItemStack stack) {
        todePiglinMerchant.spawnAtLocation(todePiglinMerchant.getItemInHand(InteractionHand.OFF_HAND));
        if (stack.is(ModTags.Items.PIGLIN_WANTED_ITEMS)) {
            todePiglinMerchant.setItemInHand(InteractionHand.OFF_HAND, stack);
            todePiglinMerchant.setItemSlot(EquipmentSlot.OFFHAND, stack);
            todePiglinMerchant.setGuaranteedDrop(EquipmentSlot.OFFHAND);
            todePiglinMerchant.swing(InteractionHand.OFF_HAND);
        }
        else {
            todePiglinMerchant.setItemSlotAndDropWhenKilled(EquipmentSlot.OFFHAND, stack);
            todePiglinMerchant.swing(InteractionHand.OFF_HAND);
        }
    }
    private static @NotNull ItemStack removeOneItemFromItemEntity(@NotNull ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        ItemStack stack1 = stack.split(1);
        if (stack.isEmpty()) {
            itemEntity.discard();
        }
        else {
            itemEntity.setItem(stack);
        }
        return stack1;
    }
    public boolean wantsToPickUp(@NotNull ItemStack stack) {
        return ForgeEventFactory.getMobGriefingEvent(this.level, this) && this.canPickUpLoot() && wantsToPickUp(stack, this);
    }
    public static boolean wantsToPickUp(@NotNull ItemStack stack, @NotNull TodePiglinMerchant todePiglinMerchant) {
        if (stack.is(ItemTags.PIGLIN_REPELLENTS)) {
            return false;
        }
        else if (TodePiglinBarterCurrencySensor.isAdmiringDisabled(todePiglinMerchant)
                && todePiglinMerchant.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
            return false;
        }
        else if (isWantedItem(stack) && todePiglinMerchant.isWillingToBarter()) {
            return TodePiglinBarterCurrencySensor.isNotHoldingWantedItemInOffHand(todePiglinMerchant);
        }
        else {
            boolean flag = todePiglinMerchant.canAddToInventory(stack);
            if (stack.is(Items.GOLD_NUGGET)) {
                return flag;
            }
            else if (EatFood.isFood(stack)) {
                return EatFood.hasNotEatenRecently(todePiglinMerchant) && flag;
            }
            else if (!isLovedItem(stack)) {
                return todePiglinMerchant.canReplaceCurrentItem(stack);
            }
            else {
                return TodePiglinBarterCurrencySensor.isNotHoldingWantedItemInOffHand(todePiglinMerchant) && flag;
            }
        }
    }

    /** "item shuffle" as the entity examines what it has in hand and decides what to do with it **/
    protected boolean canReplaceCurrentItem(ItemStack pCandidate) {
        EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(pCandidate);
        ItemStack itemstack = this.getItemBySlot(equipmentslot);
        return this.canReplaceCurrentItem(pCandidate, itemstack);
    }
    protected boolean canReplaceCurrentItem(@NotNull ItemStack pCandidate, @NotNull ItemStack pExisting) {
        if (EnchantmentHelper.hasBindingCurse(pExisting)) {
            return false;
        } else {
            boolean flag = isLovedItem(pCandidate) || pCandidate.is(Items.GOLDEN_AXE);
            boolean flag1 = isLovedItem(pExisting) || pExisting.is(Items.GOLDEN_AXE);
            if (flag && !flag1) {
                return true;
            } else if (!flag && flag1) {
                return false;
            } else {
                return (pCandidate.is(Items.GOLDEN_AXE) || !pExisting.is(Items.GOLDEN_AXE)) &&
                        super.canReplaceCurrentItem(pCandidate, pExisting);
            }
        }
    }

    /** this is where the "barter recipe" check begins **/
    protected void pickUpItem(@NotNull ItemEntity itemEntity) {
        this.onItemPickup(itemEntity);
        setHoldingItem(true);
        stopWalking(this);
        ItemStack stack = this.getItemInHand(InteractionHand.OFF_HAND);
        this.setItemSlot(EquipmentSlot.OFFHAND, stack);
        this.getItemInHand(InteractionHand.OFF_HAND);
        this.setItemInHand(InteractionHand.OFF_HAND, stack);

        if (itemEntity.getItem().is(Items.GOLD_NUGGET)) {
            this.take(itemEntity, itemEntity.getItem().getCount());
            stack = itemEntity.getItem();
            itemEntity.discard();
        }
        else {
            this.take(itemEntity, 1);
            stack = removeOneItemFromItemEntity(itemEntity);
        }
        if (isWantedItem(stack) && !EatFood.isFood(stack)) {
            BrainUtils.clearMemory(this, MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
            holdInOffhand(this, stack);
            TodePiglinBarterCurrencySensor.admireItem(this);
            stopWalking(this);
            setHoldingItem(true);
        }
        else if (EatFood.isFood(stack) && EatFood.hasNotEatenRecently(this)) {
            holdInOffhand(this, itemEntity.getItem());
            EatFood.eat( this);
        }
        else if (EatFood.isFood(stack) && !EatFood.hasNotEatenRecently(this)) {
            itemEntity.discard();
        }
        else {
            boolean flag = this.equipItemIfPossible(stack);
            if (!flag) {
                putInInventory(this, stack);
            }
        }
    }
    /** this is where the bartering item check event actually happens **/
    public static void stopHoldingOffHandItem(@NotNull TodePiglinMerchant todePiglinMerchant, boolean pShouldBarter) {
        ItemStack offHandItem = todePiglinMerchant.getItemInHand(InteractionHand.OFF_HAND);
        todePiglinMerchant.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        boolean flag = offHandItem.is(ModTags.Items.PIGLIN_BARTER_ITEMS);
        if (pShouldBarter && flag) {
            //maybe dance?
            wantsToDance(todePiglinMerchant);
            throwItems(todePiglinMerchant, getBarterResponseItems(todePiglinMerchant));
        }
        else if (!flag) {
            boolean flag1 = todePiglinMerchant.equipItemIfPossible(offHandItem);
            if (!flag1) {
                putInInventory(todePiglinMerchant, offHandItem);
            }
        }
        else {
            boolean flag2 = todePiglinMerchant.equipItemIfPossible(offHandItem);
            if (!flag2) {
                ItemStack mainHandItem = todePiglinMerchant.getMainHandItem();
                if (isLovedItem(mainHandItem)) {
                    putInInventory(todePiglinMerchant, mainHandItem);
                }
                else {
                    throwItems(todePiglinMerchant, Collections.singletonList(mainHandItem));
                }

                todePiglinMerchant.holdInMainHand(offHandItem);
            }
        }
    }
    // TODO setup a custom bartering loot tier  system
    /** if I can get the custom currency working then maybe I can then get custom loot tables running too
     * it's very likely that I can get this running with the sensor **/
    private static @NotNull List<ItemStack> getBarterResponseItems(@NotNull TodePiglinMerchant todePiglinMerchant) {
        LootTable lootTable = Objects.requireNonNull(todePiglinMerchant.level.getServer()).getLootTables().get(BuiltInLootTables.PIGLIN_BARTERING);
        return lootTable.getRandomItems((
                new LootContext.Builder((ServerLevel)todePiglinMerchant.level))
                .withParameter(LootContextParams.THIS_ENTITY, todePiglinMerchant)
                .withRandom(todePiglinMerchant.level.random).create(LootContextParamSets.PIGLIN_BARTER));
    }
    // items thrown in response to item held
    private static void throwItems(@NotNull TodePiglinMerchant todePiglinMerchant, List<ItemStack> stacks) {
        Optional<Player> optional = todePiglinMerchant.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
        if (optional.isPresent()) {
            throwItemsTowardPlayer(todePiglinMerchant, optional.get(), stacks);
        }
        else {
            throwItemsTowardRandomPos(todePiglinMerchant, stacks);
        }
    }
    private static void throwItemsTowardRandomPos(@NotNull TodePiglinMerchant todePiglinMerchant, List<ItemStack> stacks) {
        throwItemsTowardPos(todePiglinMerchant, stacks, getRandomNearbyPos(todePiglinMerchant));
    }
    private static void throwItemsTowardPlayer(@NotNull TodePiglinMerchant todePiglinMerchant, @NotNull Player pPlayer, List<ItemStack> stacks) {
        throwItemsTowardPos(todePiglinMerchant, stacks, pPlayer.position());
    }
    private static void throwItemsTowardPos(@NotNull TodePiglinMerchant todePiglinMerchant, @NotNull List<ItemStack> stacks, Vec3 pPos) {
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
    public boolean hurt(@NotNull DamageSource dmgSrc, float dmgAmt) {
        boolean flag = super.hurt(dmgSrc, dmgAmt);
        if (this.level.isClientSide) {
            return false;
        }
        else {
            if (flag && dmgSrc.getEntity() instanceof LivingEntity) {
                wasHurtBy(this, (LivingEntity)dmgSrc.getEntity());
            }
            return flag;
        }
    }
    public void wasHurtBy(TodePiglinMerchant todePiglinMerchant, LivingEntity hurtBy) {
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
            TodePiglinSpecificSensor.getAvoidTarget(todePiglinMerchant).ifPresent((livingEntity) -> {
                if (livingEntity.getType() != hurtBy.getType()) {
                    BrainUtils.clearMemory(todePiglinMerchant, MemoryModuleType.AVOID_TARGET);
                }
            });
            if (hurtBy.getType() == EntityType.HOGLIN && TodePiglinSpecificSensor.hoglinsOutnumberPiglins(todePiglinMerchant)) {
                TodePiglinSpecificSensor.setAvoidTargetAndDontHuntForAWhile(todePiglinMerchant, hurtBy);
                TodePiglinSpecificSensor.broadcastRetreat(todePiglinMerchant, hurtBy);
            }
            else {
                SetAngerTarget.maybeRetaliate(todePiglinMerchant, hurtBy);
            }
        }
    }
}