package com.github.warrentode.todepiglins.trades;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.*;

// AUTHOR: MrCrayfish https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X
public record EntityTrades(Map<TradeRarity, List<VillagerTrades.ItemListing>> tradeMap) {
    public EntityTrades(Map<TradeRarity, List<VillagerTrades.ItemListing>> tradeMap) {
        this.tradeMap = ImmutableMap.copyOf(tradeMap);
    }

    public static class Builder {
        private final Map<TradeRarity, List<VillagerTrades.ItemListing>> tradeMap = Util.make(() -> {
            Map<TradeRarity, List<VillagerTrades.ItemListing>> map = new EnumMap<>(TradeRarity.class);
            Arrays.stream(TradeRarity.values()).forEach(rarity -> map.put(rarity, new ArrayList<>()));
            return map;
        });

        private Builder() {
        }

        public EntityTrades build() {
            return new EntityTrades(this.tradeMap);
        }

        void deserialize(TradeRarity rarity, JsonObject object) {
            List<VillagerTrades.ItemListing> trades = this.tradeMap.get(rarity);

            if (GsonHelper.getAsBoolean(object, "replace", false)) {
                trades.clear();
            }

            JsonArray tradeArray = GsonHelper.getAsJsonArray(object, "trades");
            for (JsonElement tradeElement : tradeArray) {
                JsonObject tradeObject = tradeElement.getAsJsonObject();
                String rawType = GsonHelper.getAsString(tradeObject, "type");
                ResourceLocation typeKey = ResourceLocation.tryParse(rawType);
                if (typeKey == null) {
                    throw new JsonParseException("");
                }
                TradeSerializer<?> serializer = TradeManager.instance().getTypeSerializer(typeKey);
                if (serializer == null) {
                    throw new JsonParseException(String.format("Invalid trade type: %s", typeKey));
                }
                trades.add(serializer.deserialize(tradeObject).createVillagerTrade());
            }
        }

        static Builder create() {
            return new Builder();
        }
    }
}