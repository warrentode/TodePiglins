package net.warrentode.todepiglins.entity.custom.brain.behavior.todepiglinmerchant.bartering;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchant;

import java.util.Optional;

public class StopAdmiringIfTiredOfTryingToReachItem extends Behavior<TodePiglinMerchant> {
   private final int maxTimeToReachItem;
   private final int disableTime;

   public StopAdmiringIfTiredOfTryingToReachItem(int pMaxTimeToReachItem, int pDisableTime) {
      super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryStatus.REGISTERED, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryStatus.REGISTERED));
      this.maxTimeToReachItem = pMaxTimeToReachItem;
      this.disableTime = pDisableTime;
   }

   protected boolean checkExtraStartConditions(ServerLevel pLevel, TodePiglinMerchant todePiglinMerchant) {
      return todePiglinMerchant.getOffhandItem().isEmpty();
   }

   protected void start(ServerLevel pLevel, TodePiglinMerchant todePiglinMerchant, long pGameTime) {
      Brain<TodePiglinMerchant> todePiglinMerchantBrain = todePiglinMerchant.getBrain();
      Optional<Integer> optional = todePiglinMerchantBrain.getMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
      if (optional.isEmpty()) {
         todePiglinMerchantBrain.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, 0);
      } else {
         int i = optional.get();
         if (i > this.maxTimeToReachItem) {
            todePiglinMerchantBrain.eraseMemory(MemoryModuleType.ADMIRING_ITEM);
            todePiglinMerchantBrain.eraseMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
            todePiglinMerchantBrain.setMemoryWithExpiry(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, true, this.disableTime);
         } else {
            todePiglinMerchantBrain.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, i + 1);
         }
      }
   }
}