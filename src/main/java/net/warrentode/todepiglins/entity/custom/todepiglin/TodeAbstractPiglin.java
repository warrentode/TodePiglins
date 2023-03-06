package net.warrentode.todepiglins.entity.custom.todepiglin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.event.ForgeEventFactory;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchantAi;
import net.warrentode.todepiglins.util.ModTags;

import javax.annotation.Nullable;

public abstract class TodeAbstractPiglin extends Monster {
   protected static final EntityDataAccessor<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION =
           SynchedEntityData.defineId(TodeAbstractPiglin.class, EntityDataSerializers.BOOLEAN);
   protected static final int CONVERSION_TIME = 300;
   protected int timeInOverworld;

   protected TodeAbstractPiglin(EntityType<? extends Monster> pMonster, Level level) {
      super(pMonster, level);
      this.setCanPickUpLoot(true);
      this.applyOpenDoorsAbility();
      this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
      this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
   }

   public static Ingredient getBarterItems() {
      return Ingredient.of(ModTags.Items.PIGLIN_BARTER_ITEMS);
   }

   public void playAmbientSound() {
      if (TodePiglinMerchantAi.isIdle(this)) {
         super.playAmbientSound();
      }
   }

   private void applyOpenDoorsAbility() {
      if (GoalUtils.hasGroundPathNavigation(this)) {
         ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
      }
   }

   public boolean isAdult() {
      return !this.isBaby();
   }

   protected abstract boolean canHunt();

   public double getMyRidingOffset() {
      return this.isBaby() ? -0.05D : -0.45D;
   }

   public void setImmuneToZombification(boolean pImmuneToZombification) {
      this.getEntityData().set(DATA_IMMUNE_TO_ZOMBIFICATION, pImmuneToZombification);
   }

   protected boolean isImmuneToZombification() {
      return this.getEntityData().get(DATA_IMMUNE_TO_ZOMBIFICATION);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_IMMUNE_TO_ZOMBIFICATION, false);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      if (this.isImmuneToZombification()) {
         pCompound.putBoolean("IsImmuneToZombification", true);
      }
      pCompound.putInt("TimeInOverworld", this.timeInOverworld);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setImmuneToZombification(pCompound.getBoolean("IsImmuneToZombification"));
      this.timeInOverworld = pCompound.getInt("TimeInOverworld");
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if (this.isConverting()) {
         ++this.timeInOverworld;
      } else {
         this.timeInOverworld = 0;
      }
      if (this.timeInOverworld > CONVERSION_TIME &&
              ForgeEventFactory.canLivingConvert(this, EntityType.ZOMBIFIED_PIGLIN, (timer) -> this.timeInOverworld = timer)) {
         this.playConvertedSound();
         this.finishConversion((ServerLevel)this.level);
      }
   }

   public boolean isConverting() {
      return !this.level.dimensionType().piglinSafe() && !this.isImmuneToZombification() && !this.isNoAi();
   }

   protected void finishConversion(ServerLevel pServerLevel) {
      ZombifiedPiglin zombifiedpiglin = this.convertTo(EntityType.ZOMBIFIED_PIGLIN, true);
      if (zombifiedpiglin != null) {
         zombifiedpiglin.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
         net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, zombifiedpiglin);
      }
   }

   @SuppressWarnings({"unused", "SameReturnValue"})
   public static boolean isZombified(EntityType<?> type) {
      return true;
   }

   /**
    * Gets the active target the Goal system uses for tracking
    */
   @Nullable
   public LivingEntity getTarget() {
      return this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
   }

   protected boolean isHoldingMeleeWeapon() {
      return this.getMainHandItem().getItem() instanceof TieredItem;
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   protected abstract void playConvertedSound();
}
