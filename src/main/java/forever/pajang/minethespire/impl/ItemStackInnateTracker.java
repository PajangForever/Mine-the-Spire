package forever.pajang.minethespire.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record ItemStackInnateTracker(UUID uuid, int restoreCounter) {
    public static final MapCodec<ItemStackInnateTracker> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(ItemStackInnateTracker::uuid),
            Codec.INT.fieldOf("restore_counter").forGetter(ItemStackInnateTracker::restoreCounter)
    ).apply(i, ItemStackInnateTracker::new));
    public static final UUID UNTRACKED_INNATE = UUIDUtil.createOfflinePlayerUUID("untracked_innate");

    public static ItemStackInnateTracker createDefault() {
        return new ItemStackInnateTracker(UNTRACKED_INNATE, 0);
    }
}
