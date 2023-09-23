package com.github.warrentode.todepiglins.trades;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

// AUTHOR: MrCrayfish https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X
public record TodePiglinMerchantTrade(ItemStack offerStack, ItemStack paymentStack, ItemStack secondaryPaymentStack, int maxUses, int experience, float priceMultiplier) implements VillagerTrades.ItemListing {
    @Override
    public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource source) {
        return new TodePiglinMerchantOffer(this.paymentStack, this.secondaryPaymentStack, this.offerStack, this.maxUses, this.experience, this.priceMultiplier);
    }
}