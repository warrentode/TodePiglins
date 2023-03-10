package net.warrentode.todepiglins.entity.custom.brain;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface BrainLoader {

    static <T> Supplier<MemoryModuleType<T>> registerMemoryType(String id) {
        return BrainBoot.registerMemoryType(id, null);
    }
    static <T> Supplier<MemoryModuleType<T>> registerMemoryType(String id, @Nullable Codec<T> codec) {
        return BrainBoot.registerMemoryType(id, codec);
    }

    static <T extends ExtendedSensor<?>> Supplier<SensorType<T>> registerSensorType(String id, Supplier<T> sensor) {
        return BrainBoot.registerSensorType(id, sensor);
    }
}