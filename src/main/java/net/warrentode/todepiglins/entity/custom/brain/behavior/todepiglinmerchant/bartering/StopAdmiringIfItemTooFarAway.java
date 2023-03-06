package net.warrentode.todepiglins.entity.custom.brain.behavior.todepiglinmerchant.bartering;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchant;

import java.util.Optional;

public class StopAdmiringIfItemTooFarAway extends Behavior<TodePiglinMerchant> {
   private final int maxDistanceToItem;

   public StopAdmiringIfItemTooFarAway(int pMaxDistanceToItem) {
      super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.REGISTERED));
      this.maxDistanceToItem = pMaxDistanceToItem;
   }

   protected boolean checkExtraStartConditions(ServerLevel pLevel, TodePiglinMerchant todePiglinMerchant) {
      if (!todePiglinMerchant.getOffhandItem().isEmpty()) {
         return false;
      } else {
         Optional<ItemEntity> optional = todePiglinMerchant.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
         return optional.map(itemEntity -> !itemEntity.closerThan(todePiglinMerchant, this.maxDistanceToItem)).orElse(true);
      }
   }

   protected void start(ServerLevel pLevel, TodePiglinMerchant todePiglinMerchant, long pGameTime) {
      todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.ADMIRING_ITEM);
   }
}