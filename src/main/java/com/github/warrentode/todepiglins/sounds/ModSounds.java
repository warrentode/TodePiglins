package com.github.warrentode.todepiglins.sounds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.github.warrentode.todepiglins.TodePiglins.MODID;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static final RegistryObject<SoundEvent> TODEPIGLINMERCHANT_EAT = REGISTRY.register("todepiglinmerchant_eat",
            () -> new SoundEvent(new ResourceLocation(MODID, "todepiglinmerchant_eat")));
    public static final RegistryObject<SoundEvent> TODEPIGLINMERCHANT_HURT = REGISTRY.register("todepiglinmerchant_hurt",
            () -> new SoundEvent(new ResourceLocation(MODID, "todepiglinmerchant_hurt")));
    public static final RegistryObject<SoundEvent> TODEPIGLINMERCHANT_DEATH = REGISTRY.register("todepiglinmerchant_death",
            () -> new SoundEvent(new ResourceLocation(MODID, "todepiglinmerchant_death")));
    public static final RegistryObject<SoundEvent> TODEPIGLINMERCHANT_STEP = REGISTRY.register("todepiglinmerchant_step",
            () -> new SoundEvent(new ResourceLocation(MODID, "todepiglinmerchant_step")));
    public static final RegistryObject<SoundEvent> TODEPIGLINMERCHANT_RETREAT = REGISTRY.register("todepiglinmerchant_retreat",
            () -> new SoundEvent(new ResourceLocation(MODID, "todepiglinmerchant_retreat")));
    public static final RegistryObject<SoundEvent> TODEPIGLINMERCHANT_JEALOUS = REGISTRY.register("todepiglinmerchant_jealous",
            () -> new SoundEvent(new ResourceLocation(MODID, "todepiglinmerchant_jealous")));
    public static final RegistryObject<SoundEvent> TODEPIGLINMERCHANT_AMBIENT = REGISTRY.register("todepiglinmerchant_ambient",
            () -> new SoundEvent(new ResourceLocation(MODID, "todepiglinmerchant_ambient")));
    public static final RegistryObject<SoundEvent> TODEPIGLINMERCHANT_CELEBRATE = REGISTRY.register("todepiglinmerchant_celebrate",
            () -> new SoundEvent(new ResourceLocation(MODID, "todepiglinmerchant_celebrate")));
    public static final RegistryObject<SoundEvent> TODEPIGLINMERCHANT_ANGER = REGISTRY.register("todepiglinmerchant_anger",
            () -> new SoundEvent(new ResourceLocation(MODID, "todepiglinmerchant_anger")));
    public static final RegistryObject<SoundEvent> TODEPIGLINMERCHANT_EXAMINE = REGISTRY.register("todepiglinmerchant_examine",
            () -> new SoundEvent(new ResourceLocation(MODID, "todepiglinmerchant_examine")));
}