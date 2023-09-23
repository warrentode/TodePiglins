package com.github.warrentode.todepiglins.trades;

import com.github.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.github.warrentode.todepiglins.TodePiglins.MODID;

// AUTHOR: MrCrayfish https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X
@Mod.EventBusSubscriber(modid = MODID)
public class TradeManager implements PreparableReloadListener {
    private static final int FILE_TYPE_LENGTH_VALUE = ".json".length();
    private static final Gson GSON = new GsonBuilder().create();
    private static TradeManager instance;

    public static TradeManager instance() {
        if (instance == null) {
            instance = new TradeManager();
        }
        return instance;
    }

    private List<EntityType<?>> traders = new ArrayList<>();
    private Map<EntityType<?>, EntityTrades> tradeMap = new HashMap<>();
    private Map<ResourceLocation, TradeSerializer<?>> tradeSerializer = new HashMap<>();

    @SubscribeEvent
    public static void addReloadListener(@NotNull AddReloadListenerEvent event) {
        event.addListener(instance());
    }

    public void registerTrader(@NotNull EntityType<TodePiglinMerchant> type) {
        if(!this.traders.contains(type)) {
            this.traders.add(type);
        }
    }

    @Nullable
    public EntityTrades getTrades(@NotNull EntityType<TodePiglinMerchant> type) {
        return this.tradeMap.get(type);
    }

    public void registerTypeSerializer(TradeSerializer<?> serializer) {
        this.tradeSerializer.putIfAbsent(serializer.getId(), serializer);
    }

    @Nullable
    public TradeSerializer<?> getTypeSerializer(ResourceLocation id) {
        return this.tradeSerializer.get(id);
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier stage, @NotNull ResourceManager manager, @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler, @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
        Map<EntityType<?>, EntityTrades> entityToResourceList = new HashMap<>();
        return CompletableFuture.allOf(CompletableFuture.runAsync(() ->
                this.traders.forEach(entityType -> {
                    String folder = String.format("trades/%s", EntityType.getKey(entityType).getPath());
                    List<ResourceLocation> resources = new ArrayList<>(manager.listResources(folder, (fileName) -> fileName.getPath().endsWith(".json")).keySet());
                    resources.sort((r1, r2) -> {
                        if(r1.getNamespace().equals(r2.getNamespace())) return 0;
                        return r2.getNamespace().equals(MODID) ? 1 : -1;
                    });

                    Map<TradeRarity, LinkedHashSet<ResourceLocation>> tradeResources = new EnumMap<>(TradeRarity.class);
                    Arrays.stream(TradeRarity.values()).forEach(rarity -> tradeResources.put(rarity, new LinkedHashSet<>()));
                    resources.forEach(resource -> {
                        String path = resource.getPath().substring(0, resource.getPath().length() - FILE_TYPE_LENGTH_VALUE);
                        String[] splitPath = path.split("/");
                        if(splitPath.length != 3)
                            return;
                        Arrays.stream(TradeRarity.values()).forEach(rarity -> {
                            if(rarity.getKey().equals(splitPath[2]))
                            {
                                tradeResources.get(rarity).add(resource);
                            }
                        });
                    });

                    EntityTrades.Builder builder = EntityTrades.Builder.create();
                    Arrays.stream(TradeRarity.values()).forEach(rarity -> this.deserializeTrades(manager, builder, rarity, tradeResources.get(rarity)));
                    entityToResourceList.put(entityType, builder.build());
                    this.tradeMap = ImmutableMap.copyOf(entityToResourceList);
                }),
                backgroundExecutor)).thenCompose(stage::wait);
    }

    private void deserializeTrades(ResourceManager manager, EntityTrades.Builder builder, TradeRarity rarity, @NotNull LinkedHashSet<ResourceLocation> resources) {
        for (ResourceLocation resourceLocation : resources) {
            manager.getResource(resourceLocation).ifPresent(resource -> {
                try (Reader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                    JsonObject object = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                    builder.deserialize(rarity, object);
                }
                catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
        }
    }
}