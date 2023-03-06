package net.warrentode.todepiglins.entity.custom.brain.manager.todepiglinmerchant;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class BrainActivityGroup<T extends LivingEntity & SmartBrainOwner<T>> {
	private final Activity activity;
	private int priorityStart = 0;
	private final List<Behavior<? super T>> behaviours = new ObjectArrayList<>();
	private final Set<Pair<MemoryModuleType<?>, MemoryStatus>> activityStartMemoryConditions = new ObjectOpenHashSet<>();
	private Set<MemoryModuleType<?>> wipedMemoriesOnFinish = null;

	public BrainActivityGroup(Activity activity) {
		this.activity = activity;
	}

	public BrainActivityGroup<T> priority(int priorityStart) {
		this.priorityStart = priorityStart;

		return this;
	}

	@SafeVarargs
	public final BrainActivityGroup<T> behaviours(Behavior<? super T>... behaviours) {
		this.behaviours.addAll(new ObjectArrayList<>(behaviours));

		return this;
	}

	public void onlyStartWithMemoryStatus(MemoryModuleType<?> memory, MemoryStatus status) {
		this.activityStartMemoryConditions.add(Pair.of(memory, status));

	}

	public void wipeMemoriesWhenFinished(MemoryModuleType<?>... memories) {
		if (this.wipedMemoriesOnFinish == null) {
			this.wipedMemoriesOnFinish = new ObjectOpenHashSet<>(memories);
		}
		else {
			this.wipedMemoriesOnFinish.addAll(new ObjectOpenHashSet<>(memories));
		}

	}

	@SuppressWarnings("UnusedReturnValue")
	public BrainActivityGroup<T> requireAndWipeMemoriesOnUse(MemoryModuleType<?> @NotNull ... memories) {
		for (MemoryModuleType<?> memory : memories) {
			onlyStartWithMemoryStatus(memory, MemoryStatus.VALUE_PRESENT);
		}

		wipeMemoriesWhenFinished(memories);

		return this;
	}

	@SuppressWarnings("unused")
	public Activity getActivity() {
		return this.activity;
	}

	@SuppressWarnings("unused")
	public List<Behavior<? super T>> getBehaviours() {
		return this.behaviours;
	}

	@SuppressWarnings("unused")
	public int getPriorityStart() {
		return this.priorityStart;
	}

	@SuppressWarnings("unused")
	public Set<Pair<MemoryModuleType<?>, MemoryStatus>> getActivityStartMemoryConditions() {
		return this.activityStartMemoryConditions;
	}

	@SuppressWarnings({"unused"})
	public Set<MemoryModuleType<?>> getWipedMemoriesOnFinish() {
		return this.wipedMemoriesOnFinish != null ? this.wipedMemoriesOnFinish : Set.of();
	}

	@SuppressWarnings("unused")
	public ImmutableList<Pair<Integer, Behavior<? super T>>> pairBehaviourPriorities() {
		int priority = this.priorityStart;
		ImmutableList.Builder<Pair<Integer, Behavior<? super T>>> pairedBehaviours = ImmutableList.builder();

		for (Behavior<? super T> behaviour : this.behaviours) {
			pairedBehaviours.add(Pair.of(priority++, behaviour));
		}

		return pairedBehaviours.build();
	}

	@SuppressWarnings("unused")
	public static <T extends LivingEntity & SmartBrainOwner<T>> BrainActivityGroup<T> empty() {
		return new BrainActivityGroup<>(Activity.REST);
	}

	@SafeVarargs
	public static <T extends LivingEntity & SmartBrainOwner<T>> void coreTasks(Behavior<? super T>... behaviours) {
		new BrainActivityGroup<T>(Activity.CORE).priority(0).behaviours(behaviours);
	}

	@SafeVarargs
	public static <T extends LivingEntity & SmartBrainOwner<T>> void idleTasks(Behavior<? super T>... behaviours) {
		new BrainActivityGroup<T>(Activity.IDLE).priority(10).behaviours(behaviours);
	}

	@SafeVarargs
	public static <T extends LivingEntity & SmartBrainOwner<T>> void fightTasks(Behavior<? super T>... behaviours) {
		new BrainActivityGroup<T>(Activity.FIGHT).priority(10).behaviours(behaviours).requireAndWipeMemoriesOnUse(MemoryModuleType.ATTACK_TARGET);
	}

	@SafeVarargs
	public static <T extends LivingEntity & SmartBrainOwner<T>> void celebrateTasks(Behavior<? super T>... behaviours) {
		new BrainActivityGroup<T>(Activity.CELEBRATE).priority(10).behaviours(behaviours).requireAndWipeMemoriesOnUse(MemoryModuleType.CELEBRATE_LOCATION);
	}

	@SafeVarargs
	public static <T extends LivingEntity & SmartBrainOwner<T>> void admireTasks(Behavior<? super T>... behaviours) {
		new BrainActivityGroup<T>(Activity.ADMIRE_ITEM).priority(10).behaviours(behaviours).requireAndWipeMemoriesOnUse(MemoryModuleType.ADMIRING_ITEM);
	}

	@SafeVarargs
	public static <T extends LivingEntity & SmartBrainOwner<T>> void retreatTasks(Behavior<? super T>... behaviours) {
		new BrainActivityGroup<T>(Activity.AVOID).priority(10).behaviours(behaviours).requireAndWipeMemoriesOnUse(MemoryModuleType.AVOID_TARGET);
	}

	@SafeVarargs
	public static <T extends LivingEntity & SmartBrainOwner<T>> void rideTasks(Behavior<? super T>... behaviours) {
		new BrainActivityGroup<T>(Activity.RIDE).priority(10).behaviours(behaviours).requireAndWipeMemoriesOnUse(MemoryModuleType.RIDE_TARGET);
	}
}
