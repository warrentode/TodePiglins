package net.warrentode.todepiglins.world.spawner;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.warrentode.todepiglins.entity.ModEntityTypes;
import net.warrentode.todepiglins.util.Config;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static net.warrentode.todepiglins.TodePiglins.MODID;

/**
 * AUTHOR: MrCrayfish
 * <a href="https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X">https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X</a>
 * modified by me to fit my mod
 * **/
@Mod.EventBusSubscriber(modid = MODID)
public class SpawnHandler {
    private static Map<ResourceLocation, TodePiglinMerchantSpawner> spawners = new HashMap<>();

    @SubscribeEvent
    public static void onWorldLoad(@NotNull ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        spawners.put(BuiltinDimensionTypes.OVERWORLD.location(), new TodePiglinMerchantSpawner(server, "TodePiglinMerchant", ModEntityTypes.TODEPIGLINMERCHANT.get(), Config.COMMON.todePiglinMerchant));
        spawners.put(BuiltinDimensionTypes.NETHER.location(), new TodePiglinMerchantSpawner(server, "TodePiglinMerchant", ModEntityTypes.TODEPIGLINMERCHANT.get(), Config.COMMON.todePiglinMerchant));
    }

    @SubscribeEvent
    public static void onServerStart(ServerStoppedEvent event) {
        spawners.clear();
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.@NotNull LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        if (event.side != LogicalSide.SERVER) {
            return;
        }

        TodePiglinMerchantSpawner spawner = spawners.get(event.level.dimension().location());
        if (spawner != null) {
            spawner.tick(event.level);
        }
    }
}
