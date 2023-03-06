package net.warrentode.todepiglins.entity.custom.brain.behavior.todepiglinmerchant.bartering;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchant;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchantAi;

public class StartAdmiringItemIfSeen extends Behavior<TodePiglinMerchant> {
   private final int admireDuration;

   public StartAdmiringItemIfSeen(int pAdmireDuration) {
      super(ImmutableMap.of(
              MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT,
              MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_ABSENT,
              MemoryModuleType.ADMIRING_DISABLED, MemoryStatus.VALUE_ABSENT,
              MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryStatus.VALUE_ABSENT
              )
      );
      this.admireDuration = pAdmireDuration;
   }

   @SuppressWarnings("OptionalGetWithoutIsPresent")
   protected boolean checkExtraStartConditions(ServerLevel pLevel, TodePiglinMerchant todePiglinMerchant) {
      ItemEntity itementity = todePiglinMerchant.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
      return TodePiglinMerchantAi.isLovedItem(itementity.getItem());
   }

   protected void start(ServerLevel pLevel, TodePiglinMerchant todePiglinMerchant, long pGameTime) {
      todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.ADMIRING_ITEM, true, this.admireDuration);
   }
}