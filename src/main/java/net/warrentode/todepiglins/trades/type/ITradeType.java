package net.warrentode.todepiglins.trades.type;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.npc.VillagerTrades;

// AUTHOR: MrCrayfish https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X
public interface ITradeType<T extends VillagerTrades.ItemListing> {
    JsonObject serialize();

    T createVillagerTrade();
}
