package net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearestItemSensor;
import net.warrentode.todepiglins.entity.custom.brain.memory.ModMemoryTypes;
import net.warrentode.todepiglins.entity.custom.brain.sensor.todepiglin.TodePiglinBarterCurrencySensor;
import net.warrentode.todepiglins.entity.custom.brain.sensor.todepiglin.TodePiglinMerchantSpecificSensor;
import net.warrentode.todepiglins.entity.custom.model.todepiglin.pose.TodePiglinMerchantArmPose;
import net.warrentode.todepiglins.entity.custom.model.todepiglin.pose.TodePiglinMerchantHeadPose;
import net.warrentode.todepiglins.entity.custom.todepiglin.TodeAbstractPiglin;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TodePiglinMerchant extends TodeAbstractPiglin implements SmartBrainOwner<TodePiglinMerchant>, IAnimatable, CrossbowAttackMob, InventoryCarrier {
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_DANCING = SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_BARTER_SUCCESS =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_NOD_YES =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_NOD_NO =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> DATA_ANIMATED_HEAD_TIME =
            SynchedEntityData.defineId(TodePiglinMerchant.class, EntityDataSerializers.INT);
    private static final int MAX_HEALTH = 50;
    private static final double MOVEMENT_SPEED = 0.25;
    private static final double KNOCKBACK_RESISTANCE = 1;
    private static final double ATTACK_DAMAGE = 7.0D;
    private static final float CROSSBOW_POWER = 1.6F;
    private static final float CHANCE_OF_WEARING_EACH_ARMOUR_ITEM = 0.1F;
    private static final double PROBABILITY_OF_SPAWNING_WITH_CROSSBOW_INSTEAD_OF_SWORD = 0.5D;
    public final SimpleContainer inventory = new SimpleContainer(8);
    private boolean cannotHunt;
    @SuppressWarnings("removal")
    private final AnimationFactory animationFactory = new AnimationFactory(this);
    public static ImmutableList<MemoryModuleType<?>> RECOLLECTION =
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

    @Override
    public List<ExtendedSensor<TodePiglinMerchant>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<TodePiglinMerchant>()
                        .setPredicate((target, entity)->
                                target instanceof Player ||
                                        target instanceof Hoglin ||
                                        target instanceof ZombifiedPiglin ||
                                        target instanceof TodeAbstractPiglin),
                new NearbyPlayersSensor<>(),
                new NearestItemSensor<>(),
                new HurtBySensor<>(),
                new TodePiglinBarterCurrencySensor<>(TodeAbstractPiglin.getBarterItems()),
                new TodePiglinMerchantSpecificSensor<>()
        );
    }

    public final Level level;

    @SuppressWarnings("unused")
    public TodePiglinMerchant(EntityType<? extends TodeAbstractPiglin> pMonster, Level level) {
        super(pMonster, level);
        this.xpReward = 10;
        this.level = level;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHARGING_CROSSBOW, false);
        this.entityData.define(DATA_IS_DANCING, false);
        this.entityData.define(DATA_BARTER_SUCCESS, false);
        this.entityData.define(DATA_NOD_YES, false);
        this.entityData.define(DATA_NOD_NO, false);
        this.entityData.define(DATA_ANIMATED_HEAD_TIME, 0);
    }

    public void setBarterSuccess(boolean pSuccess) {
        this.getEntityData().set(DATA_BARTER_SUCCESS, pSuccess);
    }
    protected boolean getBarterSuccess() {
        return this.getEntityData().get(DATA_BARTER_SUCCESS);
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

    @VisibleForDebug
    public @NotNull SimpleContainer getInventory() {
        return this.inventory;
    }

    protected void dropCustomDeathLoot(@NotNull DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
    }

    @SuppressWarnings("UnusedReturnValue")
    public ItemStack addToMerchantInventory(ItemStack stack) {
        return this.inventory.addItem(stack);
    }

    protected boolean canAddToMerchantInventory(ItemStack stack) {
        return this.inventory.canAddItem(stack);
    }
    
    public static @NotNull AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE).build();
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

        TodePiglinMerchantAi.initMemories(this, pLevel.getRandom());
        this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(randomsource, pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, nbtTag);
    }

    private @NotNull ItemStack createSpawnWeapon() {
        return (double)this.random.nextFloat() < PROBABILITY_OF_SPAWNING_WITH_CROSSBOW_INSTEAD_OF_SWORD ?
                new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
    }

    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return !this.isPersistenceRequired();
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

    @Override
    protected @NotNull Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected @NotNull SmartBrain<?> makeBrain(@NotNull Dynamic<?> pDynamic) {
        return TodePiglinMerchantAi.makeBrain(this, (SmartBrain<TodePiglinMerchant>) this.brainProvider().makeBrain(pDynamic));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull SmartBrain<TodePiglinMerchant> getBrain() {
        return (SmartBrain<TodePiglinMerchant>)super.getBrain();
    }

    @Override
    public Set<Activity> getAlwaysRunningActivities() {
        return ImmutableSet.of(Activity.CORE);
    }
    @Override
    public Activity getDefaultActivity() {
        return Activity.IDLE;
    }
    @Override
    public Map<Activity, BrainActivityGroup<TodePiglinMerchant>> getAdditionalTasks() {
        return SmartBrainOwner.super.getAdditionalTasks();
    }

    @Override
    public List<Activity> getActivityPriorities() {
        return ObjectArrayList.of(
                Activity.AVOID,
                Activity.FIGHT,
                Activity.CELEBRATE,
                Activity.ADMIRE_ITEM,
                Activity.RIDE,
                Activity.IDLE);
    }

    private void setCannotHunt(boolean pCannotHunt) {
        this.cannotHunt = pCannotHunt;
    }

    protected boolean canHunt() {
        return !this.cannotHunt;
    }

    protected void customServerAiStep() {
        this.level.getProfiler().push("todepiglinmerchantBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        TodePiglinMerchantAi.updateActivity(this);
        super.customServerAiStep();
    }

    //this may also be where right-clicking empty-handed opens up the trade window?
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        InteractionResult interactionresult = super.mobInteract(pPlayer, pHand);
        if (interactionresult.consumesAction()) {
            return interactionresult;
        } else if (!this.level.isClientSide) {
            return TodePiglinMerchantAi.mobInteract(this, pPlayer, pHand);
        } else {
            //checks if the item the player is holding while clicking on this entity is a loved item and if the entity is able to admire it
            //may need to come back to edit this for barter currency specifically?
            boolean flag = TodePiglinMerchantAi.canAdmire(this, pPlayer.getItemInHand(pHand)) &&
                    !TodePiglinMerchantAi.isLovedItem(this.getOffhandItem());
            return flag ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
    }

    public int getExperienceReward() {
        return this.xpReward;
    }

    protected void finishConversion(@NotNull ServerLevel pServerLevel) {
        TodePiglinMerchantAi.cancelAdmiring(this);
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
        super.finishConversion(pServerLevel);
    }

    private <E extends IAnimatable> PlayState defaultController(@NotNull AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("walk",
                    ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle",
                ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    AnimationController<TodePiglinMerchant> danceController;
    AnimationController<TodePiglinMerchant> admireController;
    AnimationController<TodePiglinMerchant> crossbowController;
    AnimationController<TodePiglinMerchant> meleeController;
    AnimationController<TodePiglinMerchant> yesController;
    AnimationController<TodePiglinMerchant> noController;

    @Override
    public void registerControllers(@NotNull AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "defaultController", 40,
                this::defaultController));

        danceController = new AnimationController<>(this, "danceController", 40, this::dancePredicate);
        animationData.addAnimationController(danceController);
        admireController = new AnimationController<>(this, "admireController", 40, this::admirePredicate);
        animationData.addAnimationController(admireController);
        crossbowController = new AnimationController<>(this, "crossbowController", 40, this::crossbowPredicate);
        animationData.addAnimationController(crossbowController);
        meleeController = new AnimationController<>(this, "meleeController", 40, this::meleePredicate);
        animationData.addAnimationController(meleeController);
        yesController = new AnimationController<>(this, "yesController", 40, this::yesPredicate);
        animationData.addAnimationController(yesController);
        noController = new AnimationController<>(this, "noController", 40, this::noPredicate);
        animationData.addAnimationController(noController);
    }

    @Override
    public AnimationFactory getFactory() {
        return animationFactory;
    }
    private PlayState dancePredicate(AnimationEvent<?> event) {
        return isDancing() ? PlayState.CONTINUE : PlayState.STOP;
    }
    private PlayState admirePredicate(AnimationEvent<?> event) {
        return isAdmiring() ? PlayState.CONTINUE : PlayState.STOP;
    }
    private PlayState crossbowPredicate(AnimationEvent<?> event) {
        return this.swinging ? PlayState.CONTINUE : PlayState.STOP;
    }
    private PlayState meleePredicate(AnimationEvent<?> event) {
        return this.swinging ? PlayState.CONTINUE : PlayState.STOP;
    }
    private PlayState yesPredicate(AnimationEvent<?> event) {
        return getNodYes() ? PlayState.CONTINUE : PlayState.STOP;
    }
    private PlayState noPredicate(AnimationEvent<?> event) {
        return getNodNo() ? PlayState.CONTINUE : PlayState.STOP;
    }

    private void startAnimation(TodePiglinMerchant todePiglinMerchant) {
        TodePiglinMerchantArmPose todePiglinMerchantArmPose = todePiglinMerchant.getTodePiglinMerchantArmPose(todePiglinMerchant);
        TodePiglinMerchantHeadPose todePiglinMerchantHeadPose = todePiglinMerchant.getTodePiglinMerchantHeadPose(todePiglinMerchant);
        try {
            if (todePiglinMerchantArmPose == TodePiglinMerchantArmPose.DANCING) {
                danceController.markNeedsReload();
                danceController.setAnimation(new AnimationBuilder().addAnimation("dance", ILoopType.EDefaultLoopTypes.LOOP));
            }
            else if (todePiglinMerchantArmPose == TodePiglinMerchantArmPose.ADMIRING_ITEM) {
                admireController.markNeedsReload();
                if (todePiglinMerchant.isLeftHanded()) {
                    admireController.setAnimation(new AnimationBuilder().addAnimation("admire_left", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
                }
                else {
                    admireController.setAnimation(new AnimationBuilder().addAnimation("admire_right", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
                }
            } else if (todePiglinMerchantArmPose == TodePiglinMerchantArmPose.CROSSBOW_CHARGE || todePiglinMerchantArmPose == TodePiglinMerchantArmPose.CROSSBOW_HOLD) {
                crossbowController.markNeedsReload();
                if (todePiglinMerchant.isLeftHanded()) {
                    crossbowController.setAnimation(new AnimationBuilder().addAnimation("crossbow_left", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
                }
                else {
                    crossbowController.setAnimation(new AnimationBuilder().addAnimation("crossbow_right", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
                }
            } else if (todePiglinMerchantArmPose == TodePiglinMerchantArmPose.ATTACKING_WITH_MELEE_WEAPON) {
                if (todePiglinMerchant.isLeftHanded()) {
                    meleeController.setAnimation(new AnimationBuilder().addAnimation("melee_left", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                } else {
                    meleeController.setAnimation(new AnimationBuilder().addAnimation("melee_right", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                }
            } else if (todePiglinMerchantHeadPose == TodePiglinMerchantHeadPose.NOD_YES) {
                yesController.setAnimation(new AnimationBuilder().addAnimation("nod_yes", ILoopType.EDefaultLoopTypes.LOOP));
            } else if (todePiglinMerchantHeadPose == TodePiglinMerchantHeadPose.NOD_NO) {
                noController.setAnimation(new AnimationBuilder().addAnimation("nod_no", ILoopType.EDefaultLoopTypes.LOOP));
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public TodePiglinMerchantHeadPose getTodePiglinMerchantHeadPose(TodePiglinMerchant todePiglinMerchant) {
        if (getNodYes()) {
            startAnimation(todePiglinMerchant);
            return TodePiglinMerchantHeadPose.NOD_YES;
        }
        else if (getNodNo()) {
            startAnimation(todePiglinMerchant);
            return TodePiglinMerchantHeadPose.NOD_NO;
        }
        else return TodePiglinMerchantHeadPose.DEFAULT;
    }

    public void nodsYes(TodePiglinMerchant todePiglinMerchant) {
        setNodYes(true);
        this.getEntityData().set(DATA_ANIMATED_HEAD_TIME, 200);
        getTodePiglinMerchantHeadPose(todePiglinMerchant);
    }
    public void nodsNo(TodePiglinMerchant todePiglinMerchant) {
        setNodNo(true);
        this.getEntityData().set(DATA_ANIMATED_HEAD_TIME, 200);
        getTodePiglinMerchantHeadPose(todePiglinMerchant);
    }

    public void setNodYes(boolean pNod) {
        this.entityData.set(DATA_NOD_YES, pNod);
    }
    public void setNodNo(boolean pNod) {
        this.entityData.set(DATA_NOD_NO, pNod);
    }
    public boolean getNodYes() {
        return this.getEntityData().get(DATA_NOD_YES);
    }
    public boolean getNodNo() {
        return this.getEntityData().get(DATA_NOD_NO);
    }

    public void tick() {
        if (this.getEntityData().get(DATA_ANIMATED_HEAD_TIME) > 0) {
            int tickTimer = this.getEntityData().get(DATA_ANIMATED_HEAD_TIME);
            while (tickTimer > 0) {
                tickTimer--;
            }
            while (tickTimer < 0) {
                this.getEntityData().set(DATA_ANIMATED_HEAD_TIME, 0);
                setNodYes(false);
                setNodNo(false);
                tickTimer = 0;
            }
        }
    }

    public TodePiglinMerchantArmPose getTodePiglinMerchantArmPose(TodePiglinMerchant todePiglinMerchant) {
        if (this.isDancing()) {
            startAnimation(todePiglinMerchant);
            return TodePiglinMerchantArmPose.DANCING;
        } else if (TodePiglinMerchantAi.isLovedItem(this.getOffhandItem())) {
            startAnimation(todePiglinMerchant);
            return TodePiglinMerchantArmPose.ADMIRING_ITEM;
        } else if (this.isAggressive() && this.isHoldingMeleeWeapon()) {
            startAnimation(todePiglinMerchant);
            return TodePiglinMerchantArmPose.ATTACKING_WITH_MELEE_WEAPON;
        } else if (this.isChargingCrossbow()) {
            startAnimation(todePiglinMerchant);
            return TodePiglinMerchantArmPose.CROSSBOW_CHARGE;
        } else {
            startAnimation(todePiglinMerchant);
            return this.isAggressive() && this.isHolding(is ->
                    is.getItem() instanceof net.minecraft.world.item.CrossbowItem) ?
                    TodePiglinMerchantArmPose.CROSSBOW_HOLD : TodePiglinMerchantArmPose.DEFAULT;
        }
    }

    static boolean piglinBoogie = false;
    public boolean isAdmiring() {
        return this.getBrain().checkMemory(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_PRESENT);
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean isDancing() {
        if (this.entityData.get(DATA_IS_DANCING)) {
            piglinBoogie = true;
        }
        else {
            piglinBoogie = false;
        }
        return this.entityData.get(DATA_IS_DANCING);
    }

    public void setDancing(boolean pDancing) {
        this.entityData.set(DATA_IS_DANCING, pDancing);
    }

    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        boolean flag = super.hurt(pSource, pAmount);
        if (this.level.isClientSide) {
            return false;
        } else {
            if (flag && pSource.getEntity() instanceof LivingEntity) {
                TodePiglinMerchantAi.wasHurtBy(this, (LivingEntity)pSource.getEntity());
            }
            return flag;
        }
    }

    private boolean isChargingCrossbow() {
        return this.entityData.get(DATA_IS_CHARGING_CROSSBOW);
    }

    @Override
    public void setChargingCrossbow(boolean pIsCharging) {
        this.entityData.set(DATA_IS_CHARGING_CROSSBOW, pIsCharging);
    }

    public boolean canFireProjectileWeapon(@NotNull ProjectileWeaponItem pProjectileWeapon) {
        return pProjectileWeapon == Items.CROSSBOW;
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public void shootCrossbowProjectile(@NotNull LivingEntity pTarget, @NotNull ItemStack pCrossbowStack, @NotNull Projectile pProjectile, float pProjectileAngle) {
        this.shootCrossbowProjectile(this, pTarget, pProjectile, pProjectileAngle, CROSSBOW_POWER);
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity pTarget, float pDistanceFactor) {
        this.performCrossbowAttack(this, CROSSBOW_POWER);
    }

    protected void holdInMainHand(ItemStack stack) {
        this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, stack);
    }

    protected void holdInOffHand(@NotNull ItemStack stack) {
        if (stack.isPiglinCurrency()) {
            this.setItemSlot(EquipmentSlot.OFFHAND, stack);
            this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
        } else {
            this.setItemSlotAndDropWhenKilled(EquipmentSlot.OFFHAND, stack);
        }
    }

    public boolean wantsToPickUp(@NotNull ItemStack stack) {
        return ForgeEventFactory.getMobGriefingEvent(this.level, this) &&
                this.getBarterSuccess() &&
                this.canPickUpLoot() && TodePiglinMerchantAi.wantsToPickup(this, stack);
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
            boolean flag = TodePiglinMerchantAi.isLovedItem(pCandidate) || pCandidate.is(Items.CROSSBOW);
            boolean flag1 = TodePiglinMerchantAi.isLovedItem(pExisting) || pExisting.is(Items.CROSSBOW);
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

    protected void pickUpItem(@NotNull ItemEntity pItemEntity) {
        this.onItemPickup(pItemEntity);
        TodePiglinMerchantAi.pickUpItem(this, pItemEntity);
    }

    protected SoundEvent getAmbientSound() {
        return this.level.isClientSide ? null : TodePiglinMerchantAi.getMerchantSoundForCurrentActivity(this).orElse(null);
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return SoundEvents.PIGLIN_BRUTE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PIGLIN_BRUTE_DEATH;
    }

    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pBlock) {
        this.playSound(SoundEvents.PIGLIN_BRUTE_STEP, 0.15F, 1.0F);
    }

    protected void playSoundEvent(SoundEvent pSoundEvent) {
        this.playSound(pSoundEvent, this.getSoundVolume(), this.getVoicePitch());
    }

    protected void playConvertedSound() {
        this.playSound(SoundEvents.PIGLIN_CONVERTED_TO_ZOMBIFIED, this.getSoundVolume(), this.getVoicePitch());
    }
}