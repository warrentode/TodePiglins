package net.warrentode.todepiglins.entity.custom.brain.sensors;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.warrentode.todepiglins.entity.custom.brain.ModMemoryTypes;
import net.warrentode.todepiglins.entity.custom.brain.ModSensorTypes;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class TodePiglinSpecificSensor<E extends LivingEntity> extends ExtendedSensor<E> {
    private static final List<MemoryModuleType<?>> MEMORIES =
            ObjectArrayList.of(
                    // vanilla memory types
                    MemoryModuleType.LOOK_TARGET,
                    MemoryModuleType.DOORS_TO_CLOSE,
                    MemoryModuleType.NEAREST_LIVING_ENTITIES,
                    MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                    MemoryModuleType.NEAREST_VISIBLE_PLAYER,
                    MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
                    MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
                    MemoryModuleType.HURT_BY,
                    MemoryModuleType.HURT_BY_ENTITY,
                    MemoryModuleType.WALK_TARGET,
                    MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                    MemoryModuleType.ATTACK_TARGET,
                    MemoryModuleType.ATTACK_COOLING_DOWN,
                    MemoryModuleType.INTERACTION_TARGET,
                    MemoryModuleType.PATH,
                    MemoryModuleType.ANGRY_AT,
                    MemoryModuleType.UNIVERSAL_ANGER,
                    MemoryModuleType.AVOID_TARGET,
                    MemoryModuleType.ADMIRING_ITEM,
                    MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM,
                    MemoryModuleType.ADMIRING_DISABLED,
                    MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM,
                    MemoryModuleType.CELEBRATE_LOCATION,
                    MemoryModuleType.DANCING,
                    MemoryModuleType.HUNTED_RECENTLY,
                    MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN,
                    MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
                    MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED,
                    MemoryModuleType.RIDE_TARGET,
                    MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
                    MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN,
                    MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD,
                    MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM,
                    MemoryModuleType.ATE_RECENTLY,
                    MemoryModuleType.NEAREST_REPELLENT,
                    MemoryModuleType.NEARBY_ADULT_PIGLINS,
                    MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS,
                    MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
                    // custom memory types
                    ModMemoryTypes.NEARBY_ADULT_TODEPIGLINS.get(),
                    ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get(),
                    ModMemoryTypes.VISIBLE_ADULT_TODEPIGLIN_COUNT.get()
            );

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ModSensorTypes.TODEPIGLIN_SPECIFIC.get();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    protected void doTick(ServerLevel pLevel, E pEntity)  {
        Brain<?> brain = pEntity.getBrain();
        brain.setMemory(MemoryModuleType.NEAREST_REPELLENT, findNearestRepellent(pLevel, pEntity));
        Optional<Mob> weirdos = Optional.empty();
        Optional<Hoglin> huntableHoglin = Optional.empty();
        Optional<Hoglin> babyHoglin = Optional.empty();
        Optional<TodePiglinMerchant> babyTodePiglins = Optional.empty();
        Optional<AbstractPiglin> babyPiglins = Optional.empty();
        Optional<LivingEntity> zombie = Optional.empty();
        Optional<Player> playerNoGold = Optional.empty();
        Optional<Player> playerGoodie = Optional.empty();
        int adultHoglins = 0;
        List<TodePiglinMerchant> visibleAdultTodePiglins = new ObjectArrayList<>();
        List<TodePiglinMerchant> adultTodePiglins = new ObjectArrayList<>();
        List<AbstractPiglin> adultPiglins = new ObjectArrayList<>();
        List<AbstractPiglin> visibleAdultPiglins = new ObjectArrayList<>();
        NearestVisibleLivingEntities nearestvisiblelivingentities =
                brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());

        for(LivingEntity livingEntity : nearestvisiblelivingentities.findAll((livingEntity) -> true)) {
            // DETECT HOGLIN POPULATION
            if (livingEntity instanceof Hoglin hoglin) {
                // check for baby hoglins
                if (hoglin.isBaby() && babyHoglin.isEmpty()) {
                    babyHoglin = Optional.of(hoglin);
                }
                // check for adult hoglins
                else if (hoglin.isAdult()) {
                    ++adultHoglins;
                    if (huntableHoglin.isEmpty() && hoglin.canBeHunted()) {
                        huntableHoglin = Optional.of(hoglin);
                    }
                }
            }
            // DETECT COMMUNITY & ALLIES
            // detect vanilla piglin brutes
            else if (livingEntity instanceof PiglinBrute brute) {
                visibleAdultPiglins.add(brute);
            }
            // detect vanilla piglins
            else if (livingEntity instanceof Piglin piglin) {
                // detect baby vanilla piglins
                if (piglin.isBaby() && babyPiglins.isEmpty()) {
                    babyPiglins = Optional.of(piglin);
                }
                // detect adult vanilla piglins
                else if (piglin.isAdult()) {
                    visibleAdultPiglins.add(piglin);
                }
            }
            // detect todepiglins
            else if (livingEntity instanceof TodePiglinMerchant todePiglinMerchant) {
                //  detect baby todepiglins
                if (todePiglinMerchant.isBaby() && babyTodePiglins.isEmpty()) {
                    babyTodePiglins = Optional.of(todePiglinMerchant);
                }
                // detect adult todepiglins
                else if (todePiglinMerchant.isAdult()) {
                    visibleAdultTodePiglins.add(todePiglinMerchant);
                }
            }
            // PLAYER DETECTION
            else if (livingEntity instanceof Player player && !(livingEntity instanceof TodePiglinMerchant)) {
                // detect players not wearing gold
                if (playerNoGold.isEmpty() && !TodePiglinMerchant.isWearingGold(player)
                        && pEntity.canAttack(player)) {
                    playerNoGold = Optional.of(player);
                }
                // detect players holding loved items
                if (playerGoodie.isEmpty() && !player.isSpectator() && TodePiglinMerchant.isPlayerHoldingLovedItem(player)) {
                    playerGoodie = Optional.of(player);
                }
                // detect players holding wanted items
                if (playerGoodie.isEmpty() && !player.isSpectator() && TodePiglinMerchant.seesPlayerHoldingWantedItem(player)) {
                    playerGoodie = Optional.of(player);
                }
            }
            // DETECT THE WEIRDOS
            else if (weirdos.isPresent() || !(livingEntity instanceof WitherSkeleton) && !(livingEntity instanceof WitherBoss)) {
                // detect zombie piglins and zoglins
                if (zombie.isEmpty() && TodePiglinMerchant.isZombified(livingEntity.getType())) {
                    zombie = Optional.of(livingEntity);
                }
            }
            // make note of whatever else
            else {
                weirdos = Optional.of((Mob)livingEntity);
            }
        }

        for(LivingEntity livingEntity1 : brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(ImmutableList.of())) {
            if (livingEntity1 instanceof TodePiglinMerchant todePiglinMerchant) {
                // add to the list the nearest adult todepiglins
                if (todePiglinMerchant.isAdult()) {
                    adultTodePiglins.add(todePiglinMerchant);
                }
            } else if (livingEntity1 instanceof AbstractPiglin abstractPiglin) {
                // add to the list the nearest adult piglins
                if (abstractPiglin.isAdult()) {
                    adultPiglins.add(abstractPiglin);
                }
            }
        }

        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, weirdos);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, huntableHoglin);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, babyHoglin);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, zombie);
        brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, playerNoGold);
        brain.setMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, playerGoodie);
        brain.setMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, adultHoglins);
        // vanilla community
        brain.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, adultPiglins);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, visibleAdultPiglins);
        brain.setMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, visibleAdultPiglins.size());
        // custom community
        brain.setMemory(ModMemoryTypes.NEARBY_ADULT_TODEPIGLINS.get(), adultTodePiglins);
        brain.setMemory(ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get(), visibleAdultTodePiglins);
        brain.setMemory(ModMemoryTypes.VISIBLE_ADULT_TODEPIGLIN_COUNT.get(), visibleAdultTodePiglins.size());
    }

    private static @NotNull Optional<BlockPos> findNearestRepellent(ServerLevel pLevel, @NotNull LivingEntity livingEntity) {
        return BlockPos.findClosestMatch(livingEntity.blockPosition(),
                TodePiglinMerchant.REPELLENT_DETECTION_RANGE_HORIZONTAL,
                TodePiglinMerchant.REPELLENT_DETECTION_RANGE_VERTICAL,
                (blockPos) -> isValidRepellent(pLevel, blockPos));
    }

    private static boolean isValidRepellent(@NotNull ServerLevel pLevel, BlockPos pPos) {
        BlockState blockstate = pLevel.getBlockState(pPos);
        boolean flag = blockstate.is(BlockTags.PIGLIN_REPELLENTS);
        return flag && blockstate.is(Blocks.SOUL_CAMPFIRE) ? CampfireBlock.isLitCampfire(blockstate) : flag;
    }
}