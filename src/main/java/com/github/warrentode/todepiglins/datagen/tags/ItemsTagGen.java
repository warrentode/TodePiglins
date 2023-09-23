package com.github.warrentode.todepiglins.datagen.tags;

import com.github.warrentode.todepiglins.util.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ItemsTagGen extends ItemTagsProvider {
    public ItemsTagGen(DataGenerator generator, BlockTagsProvider provider, String modid, @Nullable ExistingFileHelper fileHelper) {
        super(generator, provider, modid, fileHelper);
    }

    @Override
    protected void addTags() {
        this.registerTags();
    }

    private void registerTags() {
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
                .add(Items.GOLD_INGOT)
                .addOptional(Objects.requireNonNull(ResourceLocation.tryParse("todecoins:nether_gold_coin")));
    }
}