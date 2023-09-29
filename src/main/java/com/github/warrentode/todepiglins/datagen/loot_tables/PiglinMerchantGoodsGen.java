package com.github.warrentode.todepiglins.datagen.loot_tables;

import com.github.warrentode.todepiglins.loot.ModBuiltInLootTables;
import com.github.warrentode.todepiglins.util.ModTags;
import net.minecraft.data.loot.PiglinBarterLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class PiglinMerchantGoodsGen extends PiglinBarterLoot {
    @Override
    public void accept(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
        consumer.accept(ModBuiltInLootTables.COMMON_BARTER_CURRENCY_GOODS,
                LootTable.lootTable().withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1)).setBonusRolls(ConstantValue.exactly(0))
                                .add(LootTableReference.lootTableReference(ModBuiltInLootTables.COMMON_BARTER_GOODS).setWeight(30))
                                .add(LootTableReference.lootTableReference(ModBuiltInLootTables.UNCOMMON_BARTER_GOODS).setWeight(5))
                                .add(LootTableReference.lootTableReference(ModBuiltInLootTables.RARE_BARTER_CURRENCY_GOODS).setWeight(1))
                        ));
        consumer.accept(ModBuiltInLootTables.UNCOMMON_BARTER_CURRENCY_GOODS,
                LootTable.lootTable().withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1)).setBonusRolls(ConstantValue.exactly(0))
                        .add(LootTableReference.lootTableReference(ModBuiltInLootTables.UNCOMMON_BARTER_GOODS).setWeight(30))
                        .add(LootTableReference.lootTableReference(ModBuiltInLootTables.RARE_BARTER_CURRENCY_GOODS).setWeight(1))
                ));
        consumer.accept(ModBuiltInLootTables.COMMON_BARTER_GOODS,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .setBonusRolls(ConstantValue.exactly(0))
                                .add(LootItem.lootTableItem(Items.SPECTRAL_ARROW)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0F, 12.0F))))
                                .add(TagEntry.expandTag(ModTags.Items.PIGLIN_MERCHANT_COMMON_GOODS))
                                // common equipment
                                .add(LootItem.lootTableItem(Items.IRON_SWORD).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.SMITE)
                                ))
                                .add(LootItem.lootTableItem(Items.IRON_AXE).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.SMITE)
                                ))
                                .add(LootItem.lootTableItem(Items.IRON_PICKAXE).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.UNBREAKING)
                                ))
                                .add(LootItem.lootTableItem(Items.CROSSBOW).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.QUICK_CHARGE)
                                ))
                                .add(LootItem.lootTableItem(Items.IRON_HELMET).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.THORNS)
                                        .withEnchantment(Enchantments.FIRE_PROTECTION)
                                        .withEnchantment(Enchantments.BLAST_PROTECTION)
                                ))
                                .add(LootItem.lootTableItem(Items.IRON_CHESTPLATE).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.THORNS)
                                        .withEnchantment(Enchantments.FIRE_PROTECTION)
                                        .withEnchantment(Enchantments.BLAST_PROTECTION)
                                ))
                                .add(LootItem.lootTableItem(Items.IRON_LEGGINGS).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.THORNS)
                                        .withEnchantment(Enchantments.FIRE_PROTECTION)
                                        .withEnchantment(Enchantments.BLAST_PROTECTION)
                                ))
                                .add(LootItem.lootTableItem(Items.IRON_BOOTS).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.THORNS)
                                        .withEnchantment(Enchantments.FIRE_PROTECTION)
                                        .withEnchantment(Enchantments.BLAST_PROTECTION)
                                ))
                        ));
        consumer.accept(ModBuiltInLootTables.UNCOMMON_BARTER_GOODS,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .setBonusRolls(ConstantValue.exactly(0))
                                .add(TagEntry.expandTag(ModTags.Items.PIGLIN_MERCHANT_UNCOMMON_GOODS))
                                // uncommon equipment
                                .add(LootItem.lootTableItem(Items.DIAMOND_SWORD).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.SMITE)
                                        .withEnchantment(Enchantments.MOB_LOOTING)
                                ))
                                .add(LootItem.lootTableItem(Items.DIAMOND_AXE).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.SMITE)
                                        .withEnchantment(Enchantments.MOB_LOOTING)
                                        .withEnchantment(Enchantments.BLOCK_FORTUNE)
                                ))
                                .add(LootItem.lootTableItem(Items.DIAMOND_PICKAXE).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.BLOCK_FORTUNE)
                                ))
                                .add(LootItem.lootTableItem(Items.CROSSBOW).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.QUICK_CHARGE)
                                ))
                                .add(LootItem.lootTableItem(Items.DIAMOND_HELMET).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.THORNS)
                                        .withEnchantment(Enchantments.FIRE_PROTECTION)
                                        .withEnchantment(Enchantments.BLAST_PROTECTION)
                                ))
                                .add(LootItem.lootTableItem(Items.DIAMOND_CHESTPLATE).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.THORNS)
                                        .withEnchantment(Enchantments.FIRE_PROTECTION)
                                        .withEnchantment(Enchantments.BLAST_PROTECTION)
                                ))
                                .add(LootItem.lootTableItem(Items.DIAMOND_LEGGINGS).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.THORNS)
                                        .withEnchantment(Enchantments.FIRE_PROTECTION)
                                        .withEnchantment(Enchantments.BLAST_PROTECTION)
                                ))
                                .add(LootItem.lootTableItem(Items.DIAMOND_BOOTS).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.THORNS)
                                        .withEnchantment(Enchantments.FIRE_PROTECTION)
                                        .withEnchantment(Enchantments.BLAST_PROTECTION)
                                ))
                        ));
        consumer.accept(ModBuiltInLootTables.RARE_BARTER_CURRENCY_GOODS,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .setBonusRolls(ConstantValue.exactly(0))
                                .add(TagEntry.expandTag(ModTags.Items.PIGLIN_MERCHANT_RARE_GOODS))
                                .add(LootItem.lootTableItem(Items.BOOK)
                                        .apply((new EnchantRandomlyFunction.Builder())
                                                .withEnchantment(Enchantments.SMITE)
                                                .withEnchantment(Enchantments.THORNS)
                                                .withEnchantment(Enchantments.FIRE_PROTECTION)
                                                .withEnchantment(Enchantments.BLAST_PROTECTION)
                                                .withEnchantment(Enchantments.MOB_LOOTING)
                                                .withEnchantment(Enchantments.BLOCK_FORTUNE)
                                                .withEnchantment(Enchantments.SOUL_SPEED)
                                                .withEnchantment(Enchantments.MENDING)
                                                .withEnchantment(Enchantments.QUICK_CHARGE)))
                                // rare equipment
                                .add(LootItem.lootTableItem(Items.NETHERITE_SWORD).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.MENDING)
                                        .withEnchantment(Enchantments.SMITE)
                                        .withEnchantment(Enchantments.MOB_LOOTING)
                                ))
                                .add(LootItem.lootTableItem(Items.NETHERITE_AXE).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.MENDING)
                                        .withEnchantment(Enchantments.SMITE)
                                        .withEnchantment(Enchantments.MOB_LOOTING)
                                        .withEnchantment(Enchantments.BLOCK_FORTUNE)
                                ))
                                .add(LootItem.lootTableItem(Items.NETHERITE_PICKAXE).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.MENDING)
                                        .withEnchantment(Enchantments.BLOCK_FORTUNE)
                                ))
                                .add(LootItem.lootTableItem(Items.CROSSBOW).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.MENDING)
                                        .withEnchantment(Enchantments.QUICK_CHARGE)
                                ))
                                .add(LootItem.lootTableItem(Items.NETHERITE_HELMET).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.MENDING)
                                        .withEnchantment(Enchantments.THORNS)
                                        .withEnchantment(Enchantments.FIRE_PROTECTION)
                                        .withEnchantment(Enchantments.BLAST_PROTECTION)
                                ))
                                .add(LootItem.lootTableItem(Items.NETHERITE_CHESTPLATE).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.MENDING)
                                        .withEnchantment(Enchantments.THORNS)
                                        .withEnchantment(Enchantments.FIRE_PROTECTION)
                                        .withEnchantment(Enchantments.BLAST_PROTECTION)
                                ))
                                .add(LootItem.lootTableItem(Items.NETHERITE_LEGGINGS).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.MENDING)
                                        .withEnchantment(Enchantments.THORNS)
                                        .withEnchantment(Enchantments.FIRE_PROTECTION)
                                        .withEnchantment(Enchantments.BLAST_PROTECTION)
                                ))
                                .add(LootItem.lootTableItem(Items.NETHERITE_BOOTS).apply(new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.SOUL_SPEED)
                                        .withEnchantment(Enchantments.MENDING)
                                        .withEnchantment(Enchantments.THORNS)
                                        .withEnchantment(Enchantments.FIRE_PROTECTION)
                                        .withEnchantment(Enchantments.BLAST_PROTECTION)
                                ))
                        ));
    }
}