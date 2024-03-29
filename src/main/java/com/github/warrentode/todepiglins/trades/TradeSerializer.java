package com.github.warrentode.todepiglins.trades;

import com.github.warrentode.todepiglins.trades.type.ITradeType;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerTrades;

// AUTHOR: MrCrayfish https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X
public abstract class TradeSerializer<T extends ITradeType<? extends VillagerTrades.ItemListing>> {
    @SuppressWarnings("CanBeFinal")
    private ResourceLocation id;

    public TradeSerializer(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public abstract T deserialize(JsonObject object);

    public JsonObject serialize(T trade) {
        JsonObject object = new JsonObject();
        object.addProperty("type", this.id.toString());
        return object;
    }
}