package net.warrentode.todepiglins.entity.custom.brain.behavior.todepiglinmerchant.hoglin_hunting;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.warrentode.todepiglins.entity.custom.brain.memory.ModMemoryTypes;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchant;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchantAi;

public class StartHuntingHoglin extends Behavior<TodePiglinMerchant> {
   public StartHuntingHoglin() {
      super(ImmutableMap.of(
              MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryStatus.VALUE_PRESENT,
              MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_ABSENT,
              MemoryModuleType.HUNTED_RECENTLY, MemoryStatus.VALUE_ABSENT,
              ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get(), MemoryStatus.REGISTERED
              )
      );
   }

   protected boolean checkExtraStartConditions(ServerLevel pLevel, TodePiglinMerchant todePiglinMerchant) {
      return !todePiglinMerchant.isBaby() && !TodePiglinMerchantAi.hasAnyoneNearbyHuntedRecently(todePiglinMerchant);
   }

   @SuppressWarnings("OptionalGetWithoutIsPresent")
   protected void start(ServerLevel pLevel, TodePiglinMerchant todePiglinMerchant, long pGameTime) {
      Hoglin hoglin = todePiglinMerchant.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN).get();
      TodePiglinMerchantAi.setAngerTarget(todePiglinMerchant, hoglin);
      TodePiglinMerchantAi.dontKillAnyMoreHoglinsForAWhile(todePiglinMerchant);
      TodePiglinMerchantAi.broadcastAngerTarget(todePiglinMerchant, hoglin);
      TodePiglinMerchantAi.broadcastDontKillAnyMoreHoglinsForAWhile(todePiglinMerchant);
   }
}