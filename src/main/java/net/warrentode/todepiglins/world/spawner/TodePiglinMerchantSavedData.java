package net.warrentode.todepiglins.world.spawner;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static net.warrentode.todepiglins.TodePiglins.MODID;

/**
 * AUTHOR: MrCrayfish
 * <a href="https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X">https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X</a>
 * modified by me to fit my mod
 * **/
public class TodePiglinMerchantSavedData extends SavedData {
    private static final String DATA_NAME = MODID + "todepiglinmerchant";

    private final Map<String, TodePiglinMerchantData> data = new HashMap<>();

    public TodePiglinMerchantSavedData() {}

    public TodePiglinMerchantData getTodePiglinMerchantData(String key) {
        return this.data.computeIfAbsent(key, s -> new TodePiglinMerchantData(this));
    }

    public TodePiglinMerchantSavedData read(@NotNull CompoundTag tag) {
        if (tag.contains("GoblinTraderSpawnDelay", Tag.TAG_INT)) {
            this.getTodePiglinMerchantData("GoblinTrader").setTodePiglinMerchantSpawnDelay(tag.getInt("GoblinTraderSpawnDelay"));
        }
        if (tag.contains("GoblinTraderSpawnChance", Tag.TAG_INT)) {
            this.getTodePiglinMerchantData("GoblinTrader").setTodePiglinMerchantSpawnChance(tag.getInt("GoblinTraderSpawnChance"));
        }
        if (tag.contains("Data", Tag.TAG_LIST)) {
            this.data.clear();
            ListTag list = tag.getList("Data", Tag.TAG_COMPOUND);
            list.forEach(nbt -> {
                CompoundTag goblinTag = (CompoundTag) nbt;
                String key = goblinTag.getString("Key");
                TodePiglinMerchantData data = new TodePiglinMerchantData(this);
                data.read(goblinTag);
                this.data.put(key, data);
            });
        }
        return this;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compound) {
        ListTag list = new ListTag();
        this.data.forEach((s, goblinData) -> {
            CompoundTag goblinTag = new CompoundTag();
            goblinData.write(goblinTag);
            goblinTag.putString("Key", s);
            list.add(goblinTag);
        });
        compound.put("Data", list);
        return compound;
    }

    public static TodePiglinMerchantSavedData get(@NotNull MinecraftServer server) {
        ServerLevel serverLevelOverworld = server.getLevel(Level.OVERWORLD);
        ServerLevel serverLevelNether = server.getLevel(Level.NETHER);
        if (serverLevelOverworld != null) {
            return serverLevelOverworld.getDataStorage().computeIfAbsent(tag -> new TodePiglinMerchantSavedData().read(tag), TodePiglinMerchantSavedData::new, DATA_NAME);
        }
        else if (serverLevelNether != null) {
            return serverLevelNether.getDataStorage().computeIfAbsent(tag -> new TodePiglinMerchantSavedData().read(tag), TodePiglinMerchantSavedData::new, DATA_NAME);
        }
        return null;
    }
}
