package net.warrentode.todepiglins.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> PIGLIN_BARTER_ITEMS = forgeItemTag("piglin_barter_items");
        public static final TagKey<Item> PIGLIN_WANTED_ITEMS = forgeItemTag("piglin_wanted_items");
    }

    @SuppressWarnings("SameParameterValue")
    private static TagKey<Item> forgeItemTag(String path) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", path));
    }
}
