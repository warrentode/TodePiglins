package net.warrentode.todepiglins.util;

import net.minecraftforge.common.ForgeConfigSpec;
import net.warrentode.todepiglins.trades.IRaritySettings;
import net.warrentode.todepiglins.trades.TradeRarity;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

/**
 * AUTHOR: MrCrayfish
 * <a href="https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X">https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X</a>
 * modified by me to fit my mod
 * **/
public final class Config {
    public static class Common {
        public final TodePiglinMerchant todePiglinMerchant;
        public final ForgeConfigSpec.BooleanValue preventDespawnIfNamed;

        Common(@NotNull ForgeConfigSpec.Builder builder) {
            builder.comment("Common configuration settings").push("common");
            this.todePiglinMerchant = new TodePiglinMerchant(builder, "Piglin Merchant", "todepiglinmerchant", 25, 24000, -64, 50);
            this.preventDespawnIfNamed = builder.comment("If true, prevents the trader from despawning if named").define("preventDespawnIfNamed", true);
            builder.pop();
        }

        public static class TodePiglinMerchant {
            public final ForgeConfigSpec.IntValue traderSpawnChance;
            public final ForgeConfigSpec.IntValue traderSpawnDelay;
            public final ForgeConfigSpec.IntValue traderMinSpawnLevel;
            public final ForgeConfigSpec.IntValue traderMaxSpawnLevel;
            public final ForgeConfigSpec.IntValue restockDelay;
            public final Trades trades;

            public TodePiglinMerchant(@NotNull ForgeConfigSpec.Builder builder, String name, String key, int spawnChance, int spawnDelay, int minLevel, int maxLevel) {
                builder.comment(name + " settings").push(key);
                this.traderSpawnChance = builder
                        .comment("The chance out of one hundred that the trader will spawn in the over world")
                        .defineInRange("traderSpawnChance", spawnChance, 1, 100);
                this.traderSpawnDelay = builder
                        .comment("The amount of ticks before the trader will spawn again")
                        .defineInRange("traderSpawnDelay", spawnDelay, 0, Integer.MAX_VALUE);
                this.traderMinSpawnLevel = builder
                        .comment("The minimum level the trader can spawn")
                        .defineInRange("traderMinSpawnLevel", minLevel, -64, 320);
                this.traderMaxSpawnLevel = builder
                        .comment("The maximum level the trader can spawn")
                        .defineInRange("traderMaxSpawnLevel", maxLevel, -64, 320);
                this.restockDelay = builder
                        .comment("The amount of ticks before the trader will replenish it's trades. Set to -1 to disable restocking")
                        .defineInRange("restockDelay", 48000, -1, Integer.MAX_VALUE);
                this.trades = new Trades(builder);
                builder.pop();
            }

            public static class Trades {
                public final Trade common;
                public final Trade uncommon;
                public final Trade rare;
                public final Trade epic;
                public final Trade legendary;

                public Trades(@NotNull ForgeConfigSpec.Builder builder) {
                    builder.push("trades");
                    this.common = new Trade(builder, "common", 5, 8, 1.0);
                    this.uncommon = new Trade(builder, "uncommon", 3, 6, 0.5);
                    this.rare = new Trade(builder, "rare", 1, 4, 0.25);
                    this.epic = new Trade(builder, "epic", 0, 2, 0.125);
                    this.legendary = new Trade(builder, "legendary", 0, 1, 0.0625);
                    builder.pop();
                }

                public IRaritySettings getSettings(@NotNull TradeRarity rarity) {
                    return switch(rarity) {
                        case COMMON -> this.common;
                        case UNCOMMON -> this.uncommon;
                        case RARE -> this.rare;
                        case EPIC -> this.epic;
                        case LEGENDARY -> this.legendary;
                    };
                }

                public static class Trade implements IRaritySettings {
                    public final ForgeConfigSpec.IntValue minAmount;
                    public final ForgeConfigSpec.IntValue maxAmount;
                    public final ForgeConfigSpec.DoubleValue includeChance;

                    public Trade(@NotNull ForgeConfigSpec.Builder builder, String name, int min, int max, double includeChance) {
                        builder.push(name);
                        this.minAmount = builder
                                .comment("The minimum amount of " + name + " trades that a Piglin Merchant will have.")
                                .defineInRange("minAmount", min, 0, Integer.MAX_VALUE);
                        this.maxAmount = builder
                                .comment("The maximum amount of " + name + " trades that a Piglin Merchant can have.")
                                .defineInRange("maxAmount", max, 0, Integer.MAX_VALUE);
                        this.includeChance = builder
                                .comment("The chance this trade rarity will be included in the Piglin Merchant's trades")
                                .defineInRange("includeChance", includeChance, 0.0, 1.0);
                        builder.pop();
                    }

                    @Override
                    public int getMinValue() {
                        return this.minAmount.get();
                    }

                    @Override
                    public int getMaxValue() {
                        return this.maxAmount.get();
                    }

                    @Override
                    public double includeChance() {
                        return this.includeChance.get();
                    }
                }
            }
        }
    }

    public static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = commonPair.getRight();
        COMMON = commonPair.getLeft();
    }
}
