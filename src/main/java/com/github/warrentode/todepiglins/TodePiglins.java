package com.github.warrentode.todepiglins;

import com.github.warrentode.todepiglins.client.todepiglinmerchant.TodePiglinMerchantRenderer;
import com.github.warrentode.todepiglins.entity.ModEntityTypes;
import com.github.warrentode.todepiglins.entity.custom.brain.BrainBoot;
import com.github.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import com.github.warrentode.todepiglins.item.ModItems;
import com.github.warrentode.todepiglins.sounds.ModSounds;
import com.github.warrentode.todepiglins.trades.TradeManager;
import com.github.warrentode.todepiglins.trades.type.BasicType;
import com.github.warrentode.todepiglins.util.Config;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(TodePiglins.MODID)
public class TodePiglins {
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger(TodePiglins.class);
    public static final String MODID = "todepiglins";

    public TodePiglins() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);

        BrainBoot.init();

        ModSounds.REGISTRY.register(modEventBus);

        ModEntityTypes.register(modEventBus);
        ModItems.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    public void commonSetup(final @NotNull FMLCommonSetupEvent event) {
        event.enqueueWork(()-> {
            //noinspection deprecation
            SpawnPlacements.register(ModEntityTypes.TODEPIGLINMERCHANT.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    TodePiglinMerchant::checkTodePiglinMerchantSpawnRules);
        });
        // AUTHOR: MrCrayfish https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X
        TradeManager manager = TradeManager.instance();
        manager.registerTrader(ModEntityTypes.TODEPIGLINMERCHANT.get());
        manager.registerTypeSerializer(BasicType.SERIALIZER);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntityTypes.TODEPIGLINMERCHANT.get(), TodePiglinMerchantRenderer::new);
        }
    }
}