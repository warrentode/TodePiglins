package net.warrentode.todepiglins.world.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biomes;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import net.warrentode.todepiglins.util.Config;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * AUTHOR: MrCrayfish
 * <a href="https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X">https://github.com/MrCrayfish/GoblinTraders/tree/1.19.X</a>
 * modified by me to fit my mod
 * **/
public class TodePiglinMerchantSpawner {
    private final TodePiglinMerchantData data;
    private final EntityType<TodePiglinMerchant> entityType;
    private int delayBeforeSpawnLogic;
    private final int traderSpawnDelay;
    private final int traderSpawnChance;
    private int currentTraderSpawnDelay;
    private int currentTraderSpawnChance;
    private int minLevel;
    private int maxLevel;

    public TodePiglinMerchantSpawner(MinecraftServer server, String key, EntityType<TodePiglinMerchant> entityType, @NotNull Config.Common.TodePiglinMerchant todePiglinMerchant){
        //noinspection DataFlowIssue
        this.data = TodePiglinMerchantSavedData.get(server).getTodePiglinMerchantData(key);
        this.entityType = entityType;
        this.delayBeforeSpawnLogic = 600;
        this.currentTraderSpawnDelay = this.data.getTodePiglinMerchantSpawnDelay();
        this.currentTraderSpawnChance = this.data.getTodePiglinMerchantSpawnChance();
        this.traderSpawnDelay = todePiglinMerchant.traderSpawnDelay.get();
        this.traderSpawnChance = todePiglinMerchant.traderSpawnChance.get();
        this.minLevel = Math.min(todePiglinMerchant.traderMinSpawnLevel.get(), todePiglinMerchant.traderMaxSpawnLevel.get());
        this.maxLevel = Math.max(todePiglinMerchant.traderMinSpawnLevel.get(), todePiglinMerchant.traderMaxSpawnLevel.get());
        if (this.currentTraderSpawnDelay == 0 && this.currentTraderSpawnChance == 0) {
            this.currentTraderSpawnDelay = this.traderSpawnDelay;
            this.currentTraderSpawnChance = this.traderSpawnChance;
            this.data.setTodePiglinMerchantSpawnDelay(this.currentTraderSpawnDelay);
            this.data.setTodePiglinMerchantSpawnChance(this.currentTraderSpawnChance);
        }
    }

    public void tick(@NotNull Level level) {
        if (level.getGameRules().getBoolean(GameRules.RULE_DO_TRADER_SPAWNING)) {
            if (--this.delayBeforeSpawnLogic <= 0) {
                int delay = Math.max(this.traderSpawnDelay / 20, 1);
                this.delayBeforeSpawnLogic = delay;
                this.currentTraderSpawnDelay -= delay;
                this.data.setTodePiglinMerchantSpawnDelay(this.currentTraderSpawnDelay);
                if (this.currentTraderSpawnDelay <= 0) {
                    this.currentTraderSpawnDelay = this.traderSpawnDelay;
                    if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                        int spawnChance = this.currentTraderSpawnChance;
                        this.currentTraderSpawnChance = Mth.clamp(this.currentTraderSpawnChance + this.traderSpawnChance, this.traderSpawnChance, 100);
                        this.data.setTodePiglinMerchantSpawnChance(this.currentTraderSpawnChance);

                        if (level.getRandom().nextInt(100) <= spawnChance) {
                            if (this.spawnTrader(level)) {
                                this.currentTraderSpawnChance = this.traderSpawnChance;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean spawnTrader(@NotNull Level level) {
        List<? extends Player> players = level.players();
        if (players.isEmpty()) {
            return false;
        }
        Player randomPlayer = players.get(level.getRandom().nextInt(players.size()));
        if (randomPlayer == null) {
            return true;
        }
        else {
            BlockPos blockpos = randomPlayer.getOnPos();
            BlockPos safestPos = this.getSafePositionAroundPlayer(randomPlayer.level, blockpos, 10);
            if (safestPos != null && this.isEmptyCollision(randomPlayer.level, safestPos)) {
                if (level.getBiome(safestPos).is(Biomes.THE_VOID)) {
                    return false;
                }
                if (safestPos.getY() < this.minLevel || safestPos.getY() >= this.maxLevel) {
                    return false;
                }
                TodePiglinMerchant todePiglinMerchant = (TodePiglinMerchant) this.entityType.spawn((ServerLevel) randomPlayer.level, null, null, safestPos, MobSpawnType.EVENT, false, false);
                if (todePiglinMerchant != null) {
                    todePiglinMerchant.setDespawnDelay(this.traderSpawnDelay);
                    todePiglinMerchant.restrictTo(safestPos, 16);
                    return true;
                }
            }
            return false;
        }
    }

    @Nullable
    private BlockPos getSafePositionAroundPlayer(Level level, BlockPos pos, int range) {
        if (range == 0) {
            return null;
        }
        BlockPos safestPos = null;
        for (int attempts = 0; attempts < 50; attempts++) {
            int posX = pos.getX() + level.getRandom().nextInt(range * 2) - range;
            int posY = pos.getY() + level.getRandom().nextInt(range) - range / 2;
            int posZ = pos.getZ() + level.getRandom().nextInt(range * 2) - range;
            BlockPos testPos = this.findGround(level, new BlockPos(posX, posY, posZ), range);
            if (testPos != null && NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, level, testPos, this.entityType)) {
                safestPos = testPos;
                break;
            }
        }
        return safestPos != null ? safestPos : this.getSafePositionAroundPlayer(level, pos, range / 2);
    }

    @Nullable
    private BlockPos findGround(@NotNull Level level, BlockPos pos, int maxDistance) {
        if (level.getBlockState(pos).isAir()) {
            BlockPos downPos = pos;
            while(Level.isInSpawnableBounds(downPos.below()) && level.getBlockState(downPos.below()).isAir() && downPos.below().closerThan(pos, maxDistance)) {
                downPos = downPos.below();
            }
            if (!level.getBlockState(downPos.below()).isAir()) {
                return downPos;
            }
        }
        else {
            BlockPos upPos = pos;
            while (Level.isInSpawnableBounds(upPos.above()) && !level.getBlockState(upPos.above()).isAir() && upPos.above().closerThan(pos, maxDistance)) {
                upPos = upPos.above();
            }
            if (!level.getBlockState(upPos.below()).isAir()) {
                return upPos;
            }
        }
        return null;
    }

    private boolean isEmptyCollision(@NotNull Level level, BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }
}
