package net.warrentode.todepiglins.entity.custom.brain.behavior.todepiglinmerchant.hoglin_hunting;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchant;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchantAi;

public class RememberIfHoglinWasKilled extends Behavior<TodePiglinMerchant> {
   public RememberIfHoglinWasKilled() {
      super(ImmutableMap.of(
              MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
              MemoryModuleType.HUNTED_RECENTLY, MemoryStatus.REGISTERED
              )
      );
   }

   protected void start(ServerLevel pLevel, TodePiglinMerchant todePiglinMerchant, long pGameTime) {
      if (this.isAttackTargetDeadHoglin(todePiglinMerchant)) {
         TodePiglinMerchantAi.dontKillAnyMoreHoglinsForAWhile(todePiglinMerchant);
      }

   }

   @SuppressWarnings("OptionalGetWithoutIsPresent")
   private boolean isAttackTargetDeadHoglin(TodePiglinMerchant todePiglinMerchant) {
      LivingEntity livingentity = todePiglinMerchant.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
      return livingentity.getType() == EntityType.HOGLIN && livingentity.isDeadOrDying();
   }
}