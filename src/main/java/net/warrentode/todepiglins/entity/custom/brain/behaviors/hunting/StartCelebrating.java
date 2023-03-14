package net.warrentode.todepiglins.entity.custom.brain.behaviors.hunting;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StartCelebrating extends ExtendedBehaviour<TodePiglinMerchant> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.CELEBRATE_LOCATION, MemoryStatus.VALUE_ABSENT),
                    Pair.of(MemoryModuleType.DANCING, MemoryStatus.REGISTERED)
            );

    private final int celebrateDuration;

    public StartCelebrating(int celebrateDuration) {
        this.celebrateDuration = celebrateDuration;
    }

    protected boolean checkExtraStartConditions(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant) {
        return true;
    }

    protected void start(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
        todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.DANCING, true, this.celebrateDuration);
        todePiglinMerchant.getBrain().setMemoryWithExpiry(MemoryModuleType.CELEBRATE_LOCATION, todePiglinMerchant.blockPosition(), this.celebrateDuration);
        todePiglinMerchant.setDancing(true);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
