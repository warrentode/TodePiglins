package net.warrentode.todepiglins.entity.custom.brain.behavior.todepiglinmerchant.bartering;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraftforge.common.ToolActions;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchant;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchantAi;

public class StopHoldingItemIfNoLongerAdmiring extends Behavior<TodePiglinMerchant> {
   public StopHoldingItemIfNoLongerAdmiring() {
      super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel pLevel, TodePiglinMerchant todePiglinMerchant) {
      TodePiglinMerchantAi.isBarterCurrency();
      return !todePiglinMerchant.getOffhandItem().isEmpty() && !todePiglinMerchant.getOffhandItem().canPerformAction(ToolActions.SHIELD_BLOCK);
   }

   protected void start(ServerLevel pLevel, TodePiglinMerchant todePiglinMerchant, long pGameTime) {
      todePiglinMerchant.nodsYes(todePiglinMerchant);
      TodePiglinMerchantAi.stopHoldingOffHandItem(todePiglinMerchant, true);
   }
}