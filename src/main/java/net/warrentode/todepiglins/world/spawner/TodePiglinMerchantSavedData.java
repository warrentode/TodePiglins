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

// AUTHOR: MrCrayfish https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X
public class TodePiglinMerchantSavedData extends SavedData {
    private static final String DATA_NAME = MODID + "todepiglinmerchant";

    private final Map<String, TodePiglinMerchantData> data = new HashMap<>();

    public TodePiglinMerchantSavedData() {}

    public TodePiglinMerchantData getTodePiglinMerchantData(String key) {
        return this.data.computeIfAbsent(key, s -> new TodePiglinMerchantData(this));
    }

    public TodePiglinMerchantSavedData read(@NotNull CompoundTag tag) {
        if (tag.contains("PiglinMerchantSpawnDelay", Tag.TAG_INT)) {
            this.getTodePiglinMerchantData("PiglinMerchant").setTodePiglinMerchantSpawnDelay(tag.getInt("PiglinMerchantSpawnDelay"));
        }
        if (tag.contains("PiglinMerchantSpawnChance", Tag.TAG_INT)) {
            this.getTodePiglinMerchantData("PiglinMerchant").setTodePiglinMerchantSpawnChance(tag.getInt("PiglinMerchantSpawnChance"));
        }
        if (tag.contains("Data", Tag.TAG_LIST)) {
            this.data.clear();
            ListTag list = tag.getList("Data", Tag.TAG_COMPOUND);
            list.forEach(nbt -> {
                CompoundTag todePiglinMerchantTag = (CompoundTag) nbt;
                String key = todePiglinMerchantTag.getString("Key");
                TodePiglinMerchantData data = new TodePiglinMerchantData(this);
                data.read(todePiglinMerchantTag);
                this.data.put(key, data);
            });
        }
        return this;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compound) {
        ListTag list = new ListTag();
        this.data.forEach((s, todePiglinMerchantData) -> {
            CompoundTag todePiglinMerchantTag = new CompoundTag();
            todePiglinMerchantData.write(todePiglinMerchantTag);
            todePiglinMerchantTag.putString("Key", s);
            list.add(todePiglinMerchantTag);
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
