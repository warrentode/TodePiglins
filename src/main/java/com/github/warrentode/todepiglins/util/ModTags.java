package com.github.warrentode.todepiglins.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> PIGLIN_BARTER_ITEMS = itemTag("forge", "piglin_barter_items");
        public static final TagKey<Item> PIGLIN_WANTED_ITEMS = itemTag("forge", "piglin_wanted_items");
        public static final TagKey<Item> PIGLIN_LOVED = itemTag("minecraft", "piglin_loved");
        public static final TagKey<Item> PIGLIN_FOOD = itemTag("minecraft", "piglin_food");
    }

    private static TagKey<Item> itemTag(String modid, String path) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(modid, path));
    }
}