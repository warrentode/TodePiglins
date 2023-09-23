package com.github.warrentode.todepiglins.entity.custom.brain;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

import static com.github.warrentode.todepiglins.TodePiglins.MODID;

@SuppressWarnings("unused")
public class BrainBoot implements BrainLoader {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_TYPES = DeferredRegister.create(ForgeRegistries.Keys.MEMORY_MODULE_TYPES, MODID);
    public static final DeferredRegister<SensorType<?>> SENSORS = DeferredRegister.create(ForgeRegistries.Keys.SENSOR_TYPES, MODID);

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MEMORY_TYPES.register(modEventBus);
        SENSORS.register(modEventBus);

        ModMemoryTypes.init();
        ModSensorTypes.init();
    }

    @SuppressWarnings("unused")
    public <T> Supplier<MemoryModuleType<T>> registerMemoryType(String id) {
        return registerMemoryType(id, null);
    }

    public static <T> Supplier<MemoryModuleType<T>> registerMemoryType(String id, @Nullable Codec<T> codec) {
        //noinspection Convert2Diamond
        return MEMORY_TYPES.register(id, () -> new MemoryModuleType<T>(Optional.ofNullable(codec)));
    }

    public static <T extends ExtendedSensor<?>> Supplier<SensorType<T>> registerSensorType(String id, Supplier<T> sensor) {
        return SENSORS.register(id, () -> new SensorType<>(sensor));
    }
}