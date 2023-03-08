package net.warrentode.todepiglins;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.warrentode.todepiglins.brain.BrainBoot;
import net.warrentode.todepiglins.client.todepiglinmerchant.TodePiglinMerchantRenderer;
import net.warrentode.todepiglins.entity.ModEntityTypes;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import net.warrentode.todepiglins.item.ModItems;
import net.warrentode.todepiglins.recipe.ModRecipeTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.GeckoLib;

@Mod(TodePiglins.MODID)
public class TodePiglins {
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger(TodePiglins.class);
    public static final String MODID = "todepiglins";

    public TodePiglins() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BrainBoot.init();
        ModEntityTypes.register(modEventBus);

        ModItems.register(modEventBus);
        ModRecipeTypes.register(modEventBus);

        GeckoLib.initialize();

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
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntityTypes.TODEPIGLINMERCHANT.get(), TodePiglinMerchantRenderer::new);
        }
    }
}