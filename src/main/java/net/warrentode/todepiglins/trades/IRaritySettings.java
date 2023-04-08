package net.warrentode.todepiglins.trades;

/**
 * AUTHOR: MrCrayfish
 * <a href="https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X">https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X</a>
 * **/
public interface IRaritySettings {
    int getMinValue();

    int getMaxValue();

    double includeChance();
}
