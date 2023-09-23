package com.github.warrentode.todepiglins.event;

import com.github.warrentode.todepiglins.entity.ModEntityTypes;
import com.github.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import static com.github.warrentode.todepiglins.TodePiglins.MODID;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        @SubscribeEvent
        public static void onEntityAttributeEvent(@NotNull EntityAttributeCreationEvent event) {
            event.put(ModEntityTypes.TODEPIGLINMERCHANT.get(), TodePiglinMerchant.setAttributes());
        }
    }
}