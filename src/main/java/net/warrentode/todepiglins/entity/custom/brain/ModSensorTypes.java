package net.warrentode.todepiglins.entity.custom.brain;

import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.warrentode.todepiglins.entity.custom.brain.sensors.TodePiglinBarterCurrencySensor;
import net.warrentode.todepiglins.entity.custom.brain.sensors.TodePiglinSpecificSensor;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModSensorTypes {
    @SuppressWarnings("EmptyMethod")
    public static void init() {}
    public static final Supplier<SensorType<TodePiglinSpecificSensor<?>>> TODEPIGLIN_SPECIFIC
            = register("todepiglin_specific_sensor", TodePiglinSpecificSensor::new);

    public static final Supplier<SensorType<TodePiglinBarterCurrencySensor<?>>> TODEPIGLIN_BARTER_CURRENCY
            = register("todepiglin_barter_currency_sensor", ()-> new TodePiglinBarterCurrencySensor<>(TodePiglinMerchant.getBarterItems()));

    private static <T extends ExtendedSensor<?>> @NotNull Supplier<SensorType<T>> register(String id, Supplier<T> sensor) {
        return BrainLoader.registerSensorType(id, sensor);
    }
}