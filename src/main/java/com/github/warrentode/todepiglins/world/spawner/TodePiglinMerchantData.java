package com.github.warrentode.todepiglins.world.spawner;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

// AUTHOR: MrCrayfish https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X
public class TodePiglinMerchantData {
    @SuppressWarnings("CanBeFinal")
    private TodePiglinMerchantSavedData data;
    private int todePiglinMerchantSpawnDelay;
    private int todePiglinMerchantSpawnChance;

    public TodePiglinMerchantData(TodePiglinMerchantSavedData data) {
        this.data = data;
    }

    public void setTodePiglinMerchantSpawnDelay(int todePiglinMerchantSpawnDelay) {
        this.todePiglinMerchantSpawnDelay = todePiglinMerchantSpawnDelay;
        this.data.setDirty(true);
    }

    public void setTodePiglinMerchantSpawnChance(int todePiglinMerchantSpawnChance) {
        this.todePiglinMerchantSpawnChance = todePiglinMerchantSpawnChance;
        this.data.setDirty(true);
    }

    public int getTodePiglinMerchantSpawnDelay() {
        return todePiglinMerchantSpawnDelay;
    }

    public int getTodePiglinMerchantSpawnChance() {
        return todePiglinMerchantSpawnChance;
    }

    public void read(@NotNull CompoundTag compound) {
        if (compound.contains("PiglinMerchantSpawnDelay", Tag.TAG_INT)) {
            this.todePiglinMerchantSpawnDelay = compound.getInt("PiglinMerchantSpawnDelay");
        }
        if (compound.contains("PiglinMerchantSpawnChance", Tag.TAG_INT)) {
            this.todePiglinMerchantSpawnChance = compound.getInt("PiglinMerchantSpawnChance");
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public CompoundTag write(@NotNull CompoundTag compound) {
        compound.putInt("PiglinMerchantSpawnDelay", this.todePiglinMerchantSpawnDelay);
        compound.putInt("PiglinMerchantSpawnChance", this.todePiglinMerchantSpawnChance);
        return compound;
    }
}