package net.warrentode.todepiglins.sounds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.warrentode.todepiglins.TodePiglins.MODID;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static final RegistryObject<SoundEvent> TODEPIGLINMERCHANT_EAT = REGISTRY.register("todepiglinmerchant_eat",
            () -> new SoundEvent(new ResourceLocation(MODID, "todepiglinmerchant_eat")));

}
