package net.warrentode.todepiglins.brain;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModMemoryTypes {

    @SuppressWarnings("EmptyMethod")
    public static void init() {}

    private static Codec<TodePiglinMerchant> TodePiglinMerchant;

    // Getter
    public Codec<TodePiglinMerchant> getTodeAbstractPiglin(Codec<TodePiglinMerchant> TodePiglinMerchant) {
        return TodePiglinMerchant;
    }

    // Setter
    public Codec<TodePiglinMerchant> setTodeAbstractPiglin(Codec<TodePiglinMerchant> todePiglinMerchantCodec) {
        return TodePiglinMerchant = todePiglinMerchantCodec;
    }

    @SuppressWarnings("ConstantValue")
    public static final Supplier<MemoryModuleType<List<TodePiglinMerchant>>> NEARBY_ADULT_TODEPIGLINS = register("nearby_adult_todepiglins", Codec.list(TodePiglinMerchant));
    public static final Supplier<MemoryModuleType<List<TodePiglinMerchant>>> NEAREST_VISIBLE_ADULT_TODEPIGLINS = register("nearest_visible_adult_todepiglins", Codec.list(TodePiglinMerchant));
    public static final Supplier<MemoryModuleType<Integer>> VISIBLE_ADULT_TODEPIGLIN_COUNT = register("visible_adult_todepiglin_count", Codec.INT);

    private static <T> Supplier<MemoryModuleType<T>> register(String id) {
        return BrainLoader.registerMemoryType(id, null);
    }

    private static <T> Supplier<MemoryModuleType<T>> register(String id, @Nullable Codec<T> codec) {
        return BrainLoader.registerMemoryType(id, codec);
    }
}