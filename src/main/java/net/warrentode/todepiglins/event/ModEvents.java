package net.warrentode.todepiglins.event;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.warrentode.todepiglins.entity.ModEntityTypes;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import static net.warrentode.todepiglins.TodePiglins.MODID;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        @SubscribeEvent
        public static void onEntityAttributeEvent(@NotNull EntityAttributeCreationEvent event) {
            event.put(ModEntityTypes.TODEPIGLINMERCHANT.get(), TodePiglinMerchant.setAttributes());
        }
    }
}
