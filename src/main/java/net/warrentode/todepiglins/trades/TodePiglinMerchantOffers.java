package net.warrentode.todepiglins.trades;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.trading.MerchantOffers;
import org.jetbrains.annotations.NotNull;

/**
 * AUTHOR: MrCrayfish
 * <a href="https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X">https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X</a>
 * modified by me to fit my mod
 * **/
public class TodePiglinMerchantOffers extends MerchantOffers {
    public TodePiglinMerchantOffers() {}
    public TodePiglinMerchantOffers(@NotNull CompoundTag compoundTag) {
        ListTag recipes = compoundTag.getList("Recipes", 10);
        for (int i = 0; i < recipes.size(); ++i) {
            this.add(new TodePiglinMerchantOffer(recipes.getCompound(i)));
        }
    }
}
