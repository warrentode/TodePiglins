package com.github.warrentode.todepiglins.entity.custom.brain.behaviors;

import com.github.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LookAndFollowTradingPlayerSink extends ExtendedBehaviour<TodePiglinMerchant> {
   private final float speedModifier;
   private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
           ObjectArrayList.of(
                   Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED),
                   Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED)
           );

   public LookAndFollowTradingPlayerSink(float pSpeedModifier) {
      this.speedModifier = pSpeedModifier;
   }

   protected boolean checkExtraStartConditions(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant pOwner) {
      Player player = pOwner.getTradingPlayer();
      //noinspection ConstantValue
      return pOwner.isAlive() && player != null && !pOwner.isInWater() && !pOwner.hurtMarked
              && pOwner.distanceToSqr(player) <= 16.0D && player.containerMenu != null;
   }

   protected boolean canStillUse(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
      return this.checkExtraStartConditions(pLevel, todePiglinMerchant);
   }

   protected void start(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
      this.followPlayer(todePiglinMerchant);
   }

   protected void stop(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
      Brain<?> brain = todePiglinMerchant.getBrain();
      brain.eraseMemory(MemoryModuleType.WALK_TARGET);
      brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void tick(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant pOwner, long pGameTime) {
      this.followPlayer(pOwner);
   }

   protected boolean timedOut(long pGameTime) {
      return false;
   }

   private void followPlayer(@NotNull TodePiglinMerchant pOwner) {
      if (pOwner.getTradingPlayer() != null) {
         BrainUtils.setMemory(pOwner, MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(pOwner.getTradingPlayer(), false), this.speedModifier, 2));
         BrainUtils.setMemory(pOwner, MemoryModuleType.LOOK_TARGET, new EntityTracker(pOwner.getTradingPlayer(), true));
      }
   }

   @Override
   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }
}