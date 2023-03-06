package net.warrentode.todepiglins.entity.custom.brain.memory;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.warrentode.todepiglins.TodePiglins;
import net.warrentode.todepiglins.entity.custom.brain.manager.BrainLoader;
import net.warrentode.todepiglins.entity.custom.todepiglin.TodeAbstractPiglin;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModMemoryTypes {

    @SuppressWarnings("EmptyMethod")
    public static void init() {}

    private static Codec<TodeAbstractPiglin> TodeAbstractPiglin;

    // Getter
    public Codec<TodeAbstractPiglin> getTodeAbstractPiglin(Codec<TodeAbstractPiglin> TodeAbstractPiglin) {
        return TodeAbstractPiglin;
    }

    // Setter
    public Codec<TodeAbstractPiglin> setTodeAbstractPiglin(Codec<TodeAbstractPiglin> todeAbstractPiglin) {
        return TodeAbstractPiglin = todeAbstractPiglin;
    }

    @SuppressWarnings("ConstantValue")
    public static final Supplier<MemoryModuleType<List<TodeAbstractPiglin>>> NEARBY_ADULT_TODEPIGLINS = register("nearby_adult_todepiglins", Codec.list(TodeAbstractPiglin));
    public static final Supplier<MemoryModuleType<List<TodeAbstractPiglin>>> NEAREST_VISIBLE_ADULT_TODEPIGLINS = register("nearest_visible_adult_todepiglins", Codec.list(TodeAbstractPiglin));
    public static final Supplier<MemoryModuleType<Integer>> VISIBLE_ADULT_TODEPIGLIN_COUNT = register("visible_adult_todepiglin_count", Codec.INT);

    private static <T> Supplier<MemoryModuleType<T>> register(String id) {
        return BrainLoader.registerMemoryType(id, null);
    }

    private static <T> Supplier<MemoryModuleType<T>> register(String id, @Nullable Codec<T> codec) {
        return BrainLoader.registerMemoryType(id, codec);
    }

    // one for each entity
    public static void registerTodePiglinMerchantMemories() {
        try {
            ImmutableList.Builder<MemoryModuleType<?>> builder = ImmutableList.builder();
            builder.addAll(TodePiglinMerchant.RECOLLECTION);

            builder.add(ModMemoryTypes.NEARBY_ADULT_TODEPIGLINS.get());
            builder.add(ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get());
            builder.add(ModMemoryTypes.VISIBLE_ADULT_TODEPIGLIN_COUNT.get());

            TodePiglinMerchant.RECOLLECTION = (builder.build());
        } catch (Exception e) {
            TodePiglins.LOGGER.warn("failed to register memory types for TodePiglinMerchant: " + e);
        }
    }
}