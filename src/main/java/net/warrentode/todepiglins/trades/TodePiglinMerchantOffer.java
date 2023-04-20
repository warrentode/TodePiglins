package net.warrentode.todepiglins.trades;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// AUTHOR: MrCrayfish https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X
public class TodePiglinMerchantOffer extends MerchantOffer {
    public TodePiglinMerchantOffer(CompoundTag pCompoundTag) {
        super(pCompoundTag);
    }
    public TodePiglinMerchantOffer(ItemStack payStack, ItemStack secondPayStack, ItemStack offerStack, int maxUses, int xp, float priceMult) {
        super(payStack, secondPayStack, offerStack, maxUses, xp, priceMult);
    }
    @Override
    public boolean satisfiedBy(@NotNull ItemStack offerA, @NotNull ItemStack offerB) {
        return this.isMatching(offerA, this.getCostA()) && offerA.getCount() >= this.getCostA().getCount()
                && this.isMatching(offerB, this.getCostB()) && offerB.getCount() >= this.getCostB().getCount();
    }
    private boolean isMatching(@NotNull ItemStack given, @NotNull ItemStack payment) {
        ItemStack givenCopy = given.copy();
        if (payment.isEmpty() && given.isEmpty()) {
            return true;
        }
        if (givenCopy.getItem().canBeDepleted()) {
            givenCopy.setDamageValue(givenCopy.getDamageValue());
        }
        if (!ItemStack.isSame(givenCopy, payment)) {
            return false;
        }
        if (!payment.hasTag()) {
            return true;
        }
        if (!givenCopy.hasTag()) {
            return false;
        }

        // Special case for enchanted books.
        if (givenCopy.getItem() == Items.ENCHANTED_BOOK && payment.getItem() == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> givenEnchantments = EnchantmentHelper.getEnchantments(givenCopy);
            Map<Enchantment, Integer> paymentEnchantments = EnchantmentHelper.getEnchantments(payment);
            paymentEnchantments.entrySet().removeIf(entry -> {
                Integer level = givenEnchantments.get(entry.getKey());
                return level != null && level >= entry.getValue();
            });
            if(paymentEnchantments.isEmpty()) {
                return true;
            }
        }

        return NbtUtils.compareNbt(payment.getTag(), givenCopy.getTag(), false);
    }
}
