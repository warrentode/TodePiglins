package net.warrentode.todepiglins.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static net.warrentode.todepiglins.TodePiglins.MODID;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> PIGLIN_BARTER_ITEMS = modItemTag("piglin_barter_items");
    }

    @SuppressWarnings("SameParameterValue")
    private static TagKey<Item> modItemTag(String path) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MODID, path));
    }
}
