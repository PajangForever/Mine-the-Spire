package forever.pajang.pjfmod.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public record PlayerInnateTracker(List<ItemStack> innateStacks, Set<ItemStack> trackedStacks, MutableInt restoreCounter) {

    public static final MapCodec<PlayerInnateTracker> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemStack.CODEC.listOf().fieldOf("innate_stacks").forGetter(PlayerInnateTracker::innateStacks),
            Codec.INT.fieldOf("restore_counter").forGetter(t -> t.restoreCounter().intValue())
    ).apply(i, (stacks, count) -> new PlayerInnateTracker(stacks, new HashSet<>(), new MutableInt(count))));

    public static final Map<UUID, PlayerInnateTracker> TRACKER = new ConcurrentHashMap<>();

    public PlayerInnateTracker(int restoreCounter) {
        this(new ArrayList<>(), new HashSet<>(), new MutableInt(restoreCounter));
    }

    public List<ItemStack> doInnate() {
        this.restoreCounter().increment();
        this.trackedStacks().forEach(i -> i.setCount(0));
        return this.innateStacks().stream().map(ItemStack::copy).toList();
    }
}
