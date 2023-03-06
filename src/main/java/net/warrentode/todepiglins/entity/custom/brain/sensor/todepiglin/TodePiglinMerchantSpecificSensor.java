package net.warrentode.todepiglins.entity.custom.brain.sensor.todepiglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.warrentode.todepiglins.entity.custom.brain.memory.ModMemoryTypes;
import net.warrentode.todepiglins.entity.custom.brain.sensor.ModSensorTypes;
import net.warrentode.todepiglins.entity.custom.todepiglin.TodeAbstractPiglin;
import net.warrentode.todepiglins.entity.custom.todepiglin.todepiglinmerchant.TodePiglinMerchantAi;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class TodePiglinMerchantSpecificSensor<E extends LivingEntity> extends ExtendedSensor<E> {
    private static final ImmutableList<MemoryModuleType<?>> RECOLLECTIONS =
            ImmutableList.of(
                    // vanilla memory types
                    MemoryModuleType.LOOK_TARGET,
                    MemoryModuleType.DOORS_TO_CLOSE,
                    MemoryModuleType.NEAREST_LIVING_ENTITIES,
                    MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                    MemoryModuleType.NEAREST_VISIBLE_PLAYER,
                    MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
                    MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS,
                    MemoryModuleType.NEARBY_ADULT_PIGLINS,
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
                    MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
                    MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
                    MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN,
                    MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD,
                    MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM,
                    MemoryModuleType.ATE_RECENTLY,
                    MemoryModuleType.NEAREST_REPELLENT,
                    // custom memory types
                    ModMemoryTypes.NEARBY_ADULT_TODEPIGLINS.get(),
                    ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get(),
                    ModMemoryTypes.VISIBLE_ADULT_TODEPIGLIN_COUNT.get()
            );

    @Override
    public ImmutableList<MemoryModuleType<?>> memoriesUsed() {
        return RECOLLECTIONS;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ModSensorTypes.TODEPIGLIN_MERCHANT_SPECIFIC.get();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    protected void doTick(ServerLevel pLevel, E pEntity)  {
        Brain<?> brain = pEntity.getBrain();
        brain.setMemory(MemoryModuleType.NEAREST_REPELLENT, findNearestRepellent(pLevel, pEntity));
        Optional<Mob> optional = Optional.empty();
        Optional<Hoglin> optional1 = Optional.empty();
        Optional<Hoglin> optional2 = Optional.empty();
        Optional<TodeAbstractPiglin> optional3 = Optional.empty();
        Optional<LivingEntity> optional4 = Optional.empty();
        Optional<Player> optional5 = Optional.empty();
        Optional<Player> optional6 = Optional.empty();
        int i = 0;
        List<TodeAbstractPiglin> list = Lists.newArrayList();
        List<TodeAbstractPiglin> list1 = Lists.newArrayList();
        NearestVisibleLivingEntities nearestvisiblelivingentities =
                brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());

        for(LivingEntity livingEntity : nearestvisiblelivingentities.findAll((livingEntity) -> true)) {
            // detect hoglin population
            if (livingEntity instanceof Hoglin hoglin) {
                // check for baby hoglins
                if (hoglin.isBaby() && optional2.isEmpty()) {
                    optional2 = Optional.of(hoglin);
                }
                // check for adult hoglins
                else if (hoglin.isAdult()) {
                    ++i;
                    if (optional1.isEmpty() && hoglin.canBeHunted()) {
                        optional1 = Optional.of(hoglin);
                    }
                }
            }
            // detect fellow piglins
            else if (livingEntity instanceof TodeAbstractPiglin todeAbstractPiglin) {
                list.add(todeAbstractPiglin);
            }
            else if (livingEntity instanceof TodeAbstractPiglin todeAbstractPiglin) {
                //  detect baby piglins
                if (todeAbstractPiglin.isBaby() && optional3.isEmpty()) {
                    optional3 = Optional.of(todeAbstractPiglin);
                }
                // detect adult piglins
                else if (todeAbstractPiglin.isAdult()) {
                    list.add(todeAbstractPiglin);
                }
            }
            else if (livingEntity instanceof Player player) {
                // detect players not wearing gold
                if (optional5.isEmpty() && !TodePiglinMerchantAi.isWearingGold(player) && pEntity.canAttack(livingEntity)) {
                    optional5 = Optional.of(player);
                }
                // detect players holding loved items
                if (optional6.isEmpty() && !player.isSpectator() && TodePiglinMerchantAi.isPlayerHoldingLovedItem(player)) {
                    optional6 = Optional.of(player);
                }
                // detect players holding wanted items
                if (optional6.isEmpty() && !player.isSpectator() && TodePiglinMerchantAi.seesPlayerHoldingWantedItem(player)) {
                    optional6 = Optional.of(player);
                }
            }
            // detect the weirdos
            else if (optional.isPresent() || !(livingEntity instanceof WitherSkeleton) && !(livingEntity instanceof WitherBoss)) {
                // detect zombie piglins and zoglins
                if (optional4.isEmpty() && TodeAbstractPiglin.isZombified(livingEntity.getType())) {
                    optional4 = Optional.of(livingEntity);
                }
            }
            // make note of whatever else
            else {
                optional = Optional.of((Mob)livingEntity);
            }
        }

        for(LivingEntity livingEntity1 : brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(ImmutableList.of())) {
            if (livingEntity1 instanceof TodeAbstractPiglin todeAbstractPiglin) {
                // add to the list the nearest adult piglins
                if (todeAbstractPiglin.isAdult()) {
                    list1.add(todeAbstractPiglin);
                }
            }
        }

        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, optional1);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, optional2);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, optional4);
        brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, optional5);
        brain.setMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, optional6);
        brain.setMemory(ModMemoryTypes.NEARBY_ADULT_TODEPIGLINS.get(), list1);
        brain.setMemory(ModMemoryTypes.NEAREST_VISIBLE_ADULT_TODEPIGLINS.get(), list);
        brain.setMemory(ModMemoryTypes.VISIBLE_ADULT_TODEPIGLIN_COUNT.get(), list.size());
        brain.setMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, i);
    }

    private static @NotNull Optional<BlockPos> findNearestRepellent(ServerLevel pLevel, @NotNull LivingEntity livingEntity) {
        return BlockPos.findClosestMatch(livingEntity.blockPosition(),
                TodePiglinMerchantAi.REPELLENT_DETECTION_RANGE_HORIZONTAL,
                TodePiglinMerchantAi.REPELLENT_DETECTION_RANGE_VERTICAL,
                (blockPos) -> isValidRepellent(pLevel, blockPos));
    }

    private static boolean isValidRepellent(@NotNull ServerLevel pLevel, BlockPos pPos) {
        BlockState blockstate = pLevel.getBlockState(pPos);
        boolean flag = blockstate.is(BlockTags.PIGLIN_REPELLENTS);
        return flag && blockstate.is(Blocks.SOUL_CAMPFIRE) ? CampfireBlock.isLitCampfire(blockstate) : flag;
    }
}