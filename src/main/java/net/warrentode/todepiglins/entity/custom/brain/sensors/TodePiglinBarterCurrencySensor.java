package net.warrentode.todepiglins.entity.custom.brain.sensors;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.warrentode.todepiglins.entity.custom.brain.ModMemoryTypes;
import net.warrentode.todepiglins.entity.custom.brain.ModSensorTypes;
import net.warrentode.todepiglins.entity.custom.todepiglinmerchant.TodePiglinMerchant;
import net.warrentode.todepiglins.util.ModTags;
import org.jetbrains.annotations.NotNull;

public class TodePiglinBarterCurrencySensor<E extends LivingEntity> extends ExtendedSensor<E> {
    private static final ImmutableList<MemoryModuleType<?>> MEMORIES =
            ImmutableList.of(
                    // vanilla memory types
                    MemoryModuleType.LOOK_TARGET,
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

    public ImmutableList<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ModSensorTypes.TODEPIGLIN_BARTER_CURRENCY.get();
    }

    @SuppressWarnings("unused")
    public final Ingredient barterItems;

    public TodePiglinBarterCurrencySensor(Ingredient pBarterItems) {
        this.barterItems = pBarterItems;
    }

    // checks the held item being admired if it's tagged as barter currency
    public static void isBarterCurrency() {
        ItemStack stack = ItemStack.EMPTY;
        stack.is(ModTags.Items.PIGLIN_BARTER_ITEMS);
    }

    public static boolean isLovedItem(@NotNull ItemStack stack) {
        return stack.is(ItemTags.PIGLIN_LOVED);
    }
    public static boolean isWantedItem(@NotNull ItemStack stack) {
        return stack.is(ItemTags.PIGLIN_LOVED) ||
                stack.is(ItemTags.PIGLIN_FOOD) ||
                stack.is(ModTags.Items.PIGLIN_BARTER_ITEMS);
    }
    public static boolean canAdmire(TodePiglinMerchant todePiglinMerchant, ItemStack stack) {
        return !isAdmiringDisabled(todePiglinMerchant) && !isAdmiringItem(todePiglinMerchant)
                && stack.is(ItemTags.PIGLIN_LOVED);
    }
    public static void admireItem(@NotNull TodePiglinMerchant todePiglinMerchant) {
        BrainUtils.setForgettableMemory(todePiglinMerchant, MemoryModuleType.ADMIRING_ITEM, true, TodePiglinMerchant.ADMIRE_DURATION);
    }
    public static boolean isAdmiringDisabled(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return BrainUtils.hasMemory(todePiglinMerchant, MemoryModuleType.ADMIRING_DISABLED);
    }
    public static boolean isAdmiringItem(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return BrainUtils.hasMemory(todePiglinMerchant, MemoryModuleType.ADMIRING_ITEM);
    }
    public static boolean isNotHoldingWantedItemInOffHand(@NotNull TodePiglinMerchant todePiglinMerchant) {
        return todePiglinMerchant.getOffhandItem().isEmpty() || !isWantedItem(todePiglinMerchant.getOffhandItem());
    }

    @Override
    protected void doTick(ServerLevel serverLevel, @NotNull E pEntity) {
        @SuppressWarnings("unchecked")
        Brain<E> brain = (Brain<E>) pEntity.getBrain();

        //this is the burning fuel check of the furnace basically
        if (BrainUtils.hasMemory(brain, MemoryModuleType.ADMIRING_ITEM)) {
            getEmotiveParticles(serverLevel);
        }
    }

    private void getEmotiveParticles(Level level) {
        //here we select the emotive particles needed based on the entity and the item held
        if (ItemStack.EMPTY.is(Items.GOLD_NUGGET)) {
            this.addParticlesAroundSelf(ParticleTypes.NOTE, level);
        }
        else if (ItemStack.EMPTY.is(ModTags.Items.PIGLIN_BARTER_ITEMS)) {
            this.addParticlesAroundSelf(ParticleTypes.HEART, level);
        }
        else if (ItemStack.EMPTY.is(ItemTags.PIGLIN_LOVED)) {
            this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER, level);
        }
        else if (ItemStack.EMPTY.is(ItemTags.PIGLIN_FOOD)) {
            this.addParticlesAroundSelf(ParticleTypes.COMPOSTER, level);
        }
        this.addParticlesAroundSelf(ParticleTypes.SMOKE, level);
    }

    private void addParticlesAroundSelf(SimpleParticleType pParticleOption, Level level) {
        for(int i = 0; i < 7; ++i) {
            double d0 = RandomSource.create().nextGaussian() * 0.01D;
            double d1 = RandomSource.create().nextGaussian() * 0.01D;
            double d2 = RandomSource.create().nextGaussian() * 0.01D;
            level.addParticle(pParticleOption, RandomSource.create().nextDouble(), RandomSource.create().nextDouble(),
                    RandomSource.create().nextDouble(), d0, d1, d2);
        }
    }
}
