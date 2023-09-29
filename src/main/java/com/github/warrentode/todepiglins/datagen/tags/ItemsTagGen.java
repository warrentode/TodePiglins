package com.github.warrentode.todepiglins.datagen.tags;

import com.github.warrentode.todepiglins.util.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.minecraft.world.item.Items.*;

public class ItemsTagGen extends ItemTagsProvider {
    public ItemsTagGen(DataGenerator generator, BlockTagsProvider provider, String modid, @Nullable ExistingFileHelper fileHelper) {
        super(generator, provider, modid, fileHelper);
    }

    @Override
    protected void addTags() {
        this.registerTags();
    }

    private void registerTags() {
        tag(ModTags.Items.PIGLIN_MERCHANT_RARE_GOODS)
                .add(MUSIC_DISC_PIGSTEP)
                .add(NETHERITE_SCRAP)
                .add(ANCIENT_DEBRIS.asItem());
        tag(ModTags.Items.PIGLIN_MERCHANT_UNCOMMON_GOODS)
                .add(FIRE_CHARGE)
                .add(MAGMA_CREAM)
                .add(ENDER_PEARL);
        tag(ModTags.Items.PIGLIN_MERCHANT_COMMON_GOODS)
                .addTag(ModTags.Items.NETHER_BLOCKS)
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("biomesoplenty:rose_quartz")))
                .add(BOOK)
                .add(IRON_INGOT)
                .add(GLOWSTONE_DUST)
                .add(WARPED_ROOTS)
                .add(TWISTING_VINES)
                .add(CRIMSON_ROOTS)
                .add(WEEPING_VINES)
                .add(STRING)
                .add(QUARTZ)
                .add(LEATHER)
                .add(NETHER_BRICK);

        tag(ModTags.Items.NETHER_BLOCKS)
                .add(WARPED_WART_BLOCK)
                .add(NETHER_WART_BLOCK)
                .add(WARPED_STEM)
                .add(CRIMSON_STEM)
                .add(Block.byItem(BASALT).asItem())
                .add(Block.byItem(WARPED_NYLIUM).asItem())
                .add(Block.byItem(CRIMSON_NYLIUM).asItem())
                .add(Block.byItem(OBSIDIAN).asItem())
                .add(Block.byItem(CRYING_OBSIDIAN).asItem())
                .add(Block.byItem(SOUL_SAND).asItem())
                .add(Block.byItem(RED_NETHER_BRICKS).asItem())
                .add(Block.byItem(NETHERRACK).asItem())
                .add(Block.byItem(GRAVEL).asItem())
                .add(Block.byItem(BLACKSTONE).asItem());
        tag(ModTags.Items.FUNGUS)
                .add(Block.byItem(SHROOMLIGHT).asItem())
                .add(Items.WARPED_FUNGUS)
                .add(Items.CRIMSON_FUNGUS)
                .add(Items.BROWN_MUSHROOM)
                .add(Items.RED_MUSHROOM);

        tag(ModTags.Items.PIGLIN_FOOD)
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("fastfooddelight:pork_sandwich")))
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("farmersdelight:honey_glazed_ham")))
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("farmersdelight:bacon_sandwich")))
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("farmersdelight:bacon")))
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("farmersdelight:smoked_ham")))
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("farmersdelight:ham")))
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("nethersdelight:hoglin_ear")))
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("nethersdelight:hoglin_sirloin")))
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("nethersdelight:hoglin_loin")))
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("nethersdelight:plate_of_stuffed_hoglin_roast")))
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("nethersdelight:plate_of_stuffed_hoglin_ham")))
                .addOptionalTag(Objects.requireNonNull(ResourceLocation.tryParse("nethersdelight:plate_of_stuffed_hoglin_snout")));

        tag(ModTags.Items.PIGLIN_LOVED)
                .addTag(ModTags.Items.PIGLIN_BARTER_ITEMS);
        tag(ModTags.Items.PIGLIN_WANTED_ITEMS)
                .addTag(ModTags.Items.PIGLIN_LOVED)
                .addTag(ModTags.Items.PIGLIN_FOOD)
                .addTag(ModTags.Items.PIGLIN_BARTER_ITEMS);
        tag(ModTags.Items.PIGLIN_BARTER_ITEMS)
                .addTag(ModTags.Items.COMMON_BARTER_CURRENCY)
                .addTag(ModTags.Items.UNCOMMON_BARTER_CURRENCY)
                .addTag(ModTags.Items.RARE_BARTER_CURRENCY)
                .addOptional(Objects.requireNonNull(ResourceLocation.tryParse("todecoins:nether_gold_coin")));
        tag(ModTags.Items.COMMON_BARTER_CURRENCY)
                .add(Items.GOLD_INGOT);
        tag(ModTags.Items.UNCOMMON_BARTER_CURRENCY)
                .add(Blocks.GOLD_BLOCK.asItem());
        tag(ModTags.Items.RARE_BARTER_CURRENCY)
                .add(Items.ENCHANTED_GOLDEN_APPLE);
    }
}