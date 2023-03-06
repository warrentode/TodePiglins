package net.warrentode.todepiglins.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.warrentode.todepiglins.entity.ModEntityTypes;

import static net.warrentode.todepiglins.TodePiglins.MODID;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    @SuppressWarnings("unused")
    public static final RegistryObject<ForgeSpawnEggItem> TODEPIGLINMERCHANT_SPAWN_EGG = ITEMS.register("todepiglinmerchant_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.TODEPIGLINMERCHANT, 0x330303, 0xF9F3A4,
                    new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}