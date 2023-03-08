package net.warrentode.todepiglins.brain.behaviors.bartering;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class StopAdmiringTooFar extends ExtendedBehaviour<TodePiglinMerchant> {
    private final int maxDistanceToItem;
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.REGISTERED)
            );

    public StopAdmiringTooFar(int pMaxDistanceToItem) {
        this.maxDistanceToItem = pMaxDistanceToItem;
    }

    protected boolean checkExtraStartConditions(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant) {
        if (!todePiglinMerchant.getOffhandItem().isEmpty()) {
            return false;
        } else {
            Optional<ItemEntity> optional = todePiglinMerchant.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
            return optional.map(itemEntity -> !itemEntity.closerThan(todePiglinMerchant, this.maxDistanceToItem)).orElse(true);
        }
    }

    protected void start(@NotNull ServerLevel pLevel, @NotNull TodePiglinMerchant todePiglinMerchant, long pGameTime) {
        todePiglinMerchant.getBrain().eraseMemory(MemoryModuleType.ADMIRING_ITEM);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
