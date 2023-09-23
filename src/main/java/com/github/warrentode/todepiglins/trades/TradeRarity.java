package com.github.warrentode.todepiglins.trades;

// AUTHOR: MrCrayfish https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X
public enum TradeRarity {
    COMMON("common", true),
    UNCOMMON("uncommon", true),
    RARE("rare", true),
    EPIC("epic", true),
    LEGENDARY("legendary", true);

    private final String key;
    private final boolean shuffle;

    @SuppressWarnings("SameParameterValue")
    TradeRarity(String key, boolean shuffle) {
        this.key = key;
        this.shuffle = shuffle;
    }

    public String getKey()
    {
        return this.key;
    }

    public boolean shouldShuffle()
    {
        return this.shuffle;
    }
}