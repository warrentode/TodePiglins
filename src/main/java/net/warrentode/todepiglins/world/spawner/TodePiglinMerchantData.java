package net.warrentode.todepiglins.world.spawner;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

/**
 * AUTHOR: MrCrayfish
 * <a href="https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X">https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X</a>
 * modified by me to fit my mod
 * **/
public class TodePiglinMerchantData {
    private TodePiglinMerchantSavedData data;
    private int todePiglinMerchantSpawnDelay;
    private int todePiglinMerchantSpawnChance;

    public TodePiglinMerchantData(TodePiglinMerchantSavedData data) {
        this.data = data;
    }

    public void setTodePiglinMerchantSpawnDelay(int goblinTraderSpawnDelay) {
        this.todePiglinMerchantSpawnDelay = goblinTraderSpawnDelay;
        this.data.setDirty(true);
    }

    public void setTodePiglinMerchantSpawnChance(int goblinTraderSpawnChance) {
        this.todePiglinMerchantSpawnChance = goblinTraderSpawnChance;
        this.data.setDirty(true);
    }

    public int getTodePiglinMerchantSpawnDelay() {
        return todePiglinMerchantSpawnDelay;
    }

    public int getTodePiglinMerchantSpawnChance() {
        return todePiglinMerchantSpawnChance;
    }

    public void read(@NotNull CompoundTag compound) {
        if (compound.contains("GoblinTraderSpawnDelay", Tag.TAG_INT)) {
            this.todePiglinMerchantSpawnDelay = compound.getInt("GoblinTraderSpawnDelay");
        }
        if (compound.contains("GoblinTraderSpawnChance", Tag.TAG_INT)) {
            this.todePiglinMerchantSpawnChance = compound.getInt("GoblinTraderSpawnChance");
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public CompoundTag write(@NotNull CompoundTag compound) {
        compound.putInt("GoblinTraderSpawnDelay", this.todePiglinMerchantSpawnDelay);
        compound.putInt("GoblinTraderSpawnChance", this.todePiglinMerchantSpawnChance);
        return compound;
    }
}
