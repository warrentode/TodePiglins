package net.warrentode.todepiglins.entity.custom.brain.sensor;

import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.warrentode.todepiglins.entity.custom.brain.manager.BrainLoader;
import net.warrentode.todepiglins.entity.custom.brain.sensor.todepiglin.TodePiglinBarterCurrencySensor;
import net.warrentode.todepiglins.entity.custom.brain.sensor.todepiglin.TodePiglinMerchantSpecificSensor;
import net.warrentode.todepiglins.entity.custom.todepiglin.TodeAbstractPiglin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModSensorTypes {
    @SuppressWarnings("EmptyMethod")
    public static void init() {}
    public static final Supplier<SensorType<TodePiglinMerchantSpecificSensor<?>>> TODEPIGLIN_MERCHANT_SPECIFIC
            = register("todepiglin_merchant_specific_sensor", TodePiglinMerchantSpecificSensor::new);

    public static final Supplier<SensorType<TodePiglinBarterCurrencySensor<?>>> TODEPIGLIN_BARTER_CURRENCY
            = register("todepiglin_barter_currency_sensor", ()-> new TodePiglinBarterCurrencySensor<>(TodeAbstractPiglin.getBarterItems()));

    private static <T extends ExtendedSensor<?>> @NotNull Supplier<SensorType<T>> register(String id, Supplier<T> sensor) {
        return BrainLoader.registerSensorType(id, sensor);
    }
}