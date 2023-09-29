package com.github.warrentode.todepiglins.datagen;

import com.github.warrentode.todepiglins.datagen.providers.ModLootTableGenProvider;
import com.github.warrentode.todepiglins.datagen.tags.BlockTagsGen;
import com.github.warrentode.todepiglins.datagen.tags.ItemsTagGen;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import static com.github.warrentode.todepiglins.TodePiglins.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModGenerators {
    @SubscribeEvent
    public static void gatherData(@NotNull GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        BlockTagsGen blockTagsGen = new BlockTagsGen(generator, MODID, helper);
        generator.addProvider(event.includeServer(), blockTagsGen);

        ItemsTagGen itemTagsGen = new ItemsTagGen(generator, blockTagsGen, MODID, helper);
        generator.addProvider(event.includeServer(), itemTagsGen);

        generator.addProvider(event.includeServer(), new ModLootTableGenProvider(generator));
    }
}