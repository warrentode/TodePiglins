package net.warrentode.todepiglins.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;

import static net.warrentode.todepiglins.TodePiglins.MODID;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.ENTITY_TYPES, MODID);

    public static final RegistryObject<EntityType<TodePiglinMerchant>> TODEPIGLINMERCHANT =
            ENTITY_TYPES.register("todepiglinmerchant",
                    () -> EntityType.Builder.of(TodePiglinMerchant::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .build(new ResourceLocation(MODID, "todepiglins").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}