package com.github.warrentode.todepiglins.loot;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Set;

import static com.github.warrentode.todepiglins.TodePiglins.MODID;

public class ModBuiltInLootTables {
    private static final Set<ResourceLocation> LOCATIONS = Sets.newHashSet();

    public static final ResourceLocation COMMON_BARTER_CURRENCY_GOODS = register(MODID + ":" + "gameplay/bartering/common_barter_currency_goods");
    public static final ResourceLocation UNCOMMON_BARTER_CURRENCY_GOODS = register(MODID + ":" + "gameplay/bartering/uncommon_barter_currency_goods");
    public static final ResourceLocation RARE_BARTER_CURRENCY_GOODS = register(MODID + ":" + "gameplay/bartering/rare_barter_currency_goods");

    public static final ResourceLocation UNCOMMON_BARTER_GOODS = register(MODID + ":" + "gameplay/bartering/uncommon_barter_goods");
    public static final ResourceLocation COMMON_BARTER_GOODS = register(MODID + ":" + "gameplay/bartering/common_barter_goods");

    private static final Set<ResourceLocation> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);

    private static ResourceLocation register(String path) {
        return register(new ResourceLocation(path));
    }

    private static ResourceLocation register(ResourceLocation path) {
        if (LOCATIONS.add(path)) {
            return path;
        }
        else {
            throw new IllegalArgumentException(path + " is already a registered built-in loot table");
        }
    }

    public static Set<ResourceLocation> all() {
        return IMMUTABLE_LOCATIONS;
    }
}