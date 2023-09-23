package com.github.warrentode.todepiglins.datagen.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class BlockTagsGen extends BlockTagsProvider {
    public BlockTagsGen(DataGenerator generator, String modid, @Nullable ExistingFileHelper fileHelper) {
        super(generator, modid, fileHelper);
    }

    @Override
    protected void addTags() {
        this.registerTags();
    }

    private void registerTags() {

    }
}