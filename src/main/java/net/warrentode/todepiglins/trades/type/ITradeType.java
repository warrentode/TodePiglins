package net.warrentode.todepiglins.trades.type;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.npc.VillagerTrades;

/**
 * AUTHOR: MrCrayfish
 * <a href="https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X">https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X</a>
 * **/
public interface ITradeType<T extends VillagerTrades.ItemListing> {
    JsonObject serialize();

    T createVillagerTrade();
}
