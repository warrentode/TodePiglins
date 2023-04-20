package net.warrentode.todepiglins.trades;

// AUTHOR: MrCrayfish https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X
public interface IRaritySettings {
    int getMinValue();

    int getMaxValue();

    double includeChance();
}
