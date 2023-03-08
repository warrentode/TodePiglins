package net.warrentode.todepiglins.brain.behaviors.hunting;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RememberDeadHoglin extends ExtendedBehaviour<TodePiglinMerchant> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.HUNTED_RECENTLY, MemoryStatus.REGISTERED)
            );

    protected void start(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
        if (this.isAttackTargetDeadHoglin(todePiglinMerchant)) {
            TodePiglinMerchant.dontKillAnyMoreHoglinsForAWhile(todePiglinMerchant);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private boolean isAttackTargetDeadHoglin(@NotNull TodePiglinMerchant todePiglinMerchant) {
        LivingEntity livingentity = todePiglinMerchant.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        return livingentity.getType() == EntityType.HOGLIN && livingentity.isDeadOrDying();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
