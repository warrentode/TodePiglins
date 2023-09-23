package com.github.warrentode.todepiglins.entity.custom.brain.sensors;

import com.github.warrentode.todepiglins.entity.custom.brain.ModSensorTypes;
import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.ItemEntity;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NearestWantedItemSensor<E extends Mob> extends ExtendedSensor<E> {
   private static final ImmutableList<MemoryModuleType<?>> MEMORIES = ImmutableList.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
   private static final double XZ_RANGE = 32.0D;
   private static final double Y_RANGE = 16.0D;
   public static final double MAX_DISTANCE_TO_WANTED_ITEM = 32.0D;

   @Override
   public ImmutableList<MemoryModuleType<?>> memoriesUsed() {
      return MEMORIES;
   }

   @Override
   public SensorType<? extends ExtendedSensor<?>> type() {
      return ModSensorTypes.NEAREST_WANTED_ITEM_SENSOR.get();
   }

   protected void doTick(@NotNull ServerLevel serverLevel, @NotNull Mob mob) {
      Brain<?> brain = mob.getBrain();
      List<ItemEntity> list = serverLevel.getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(XZ_RANGE, Y_RANGE, XZ_RANGE), (itemEntity) -> true);
      list.sort(Comparator.comparingDouble(mob::distanceToSqr));
      Optional<ItemEntity> optional = list.stream().filter((itemEntity) ->
              mob.wantsToPickUp(itemEntity.getItem())).filter((itemEntity1) ->
              itemEntity1.closerThan(mob, MAX_DISTANCE_TO_WANTED_ITEM)).filter(mob::hasLineOfSight).findFirst();
      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, optional);
   }
}