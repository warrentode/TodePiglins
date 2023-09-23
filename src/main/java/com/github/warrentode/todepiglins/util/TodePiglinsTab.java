package com.github.warrentode.todepiglins.util;

import com.github.warrentode.todepiglins.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class TodePiglinsTab extends CreativeModeTab {
    public static final TodePiglinsTab TODEPIGLINS_TAB = new TodePiglinsTab(CreativeModeTab.TABS.length, "todepiglins_tab");

    @SuppressWarnings("unused")
    public TodePiglinsTab(int length, String label) {
        super(length, label);
    }

    @Override
    public @NotNull ItemStack makeIcon() {
        return new ItemStack(Items.GOLD_INGOT);
    }

    @Override
    public void fillItemList(final @NotNull NonNullList<ItemStack> items) {
        items.add(ModItems.TODEPIGLINMERCHANT_SPAWN_EGG.get().getDefaultInstance());
    }
}