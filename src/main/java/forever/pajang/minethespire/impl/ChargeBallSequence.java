package forever.pajang.minethespire.impl;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import forever.pajang.minethespire.util.Sequence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class ChargeBallSequence extends Sequence<UUID> {
    public static final Codec<ChargeBallSequence> CODEC = UUIDUtil.CODEC.listOf().xmap(ChargeBallSequence::new, ChargeBallSequence::ids);

    public ChargeBallSequence() {
        super(1);
    }

    public ChargeBallSequence(Collection<UUID> ids) {
        super(Math.max(1, ids.size()));
        ids.forEach(this::addUniqueLast);
    }

    public Optional<UUID> addBall(UUID uuid, int maxSize) {
        removeElement(uuid);
        setCapacity(Math.max(1, maxSize));
        return addElement(uuid);
    }

    public void removeBall(UUID uuid) {
        removeElement(uuid);
    }

    public List<UUID> ids() {
        return List.copyOf(this);
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(size());
        forEach(buf::writeUUID);
    }

    public static ChargeBallSequence decode(RegistryFriendlyByteBuf buf) {
        int size = buf.readVarInt();
        java.util.ArrayList<UUID> ids = new java.util.ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ids.add(buf.readUUID());
        }
        return new ChargeBallSequence(ids);
    }

    private void addUniqueLast(UUID uuid) {
        if (contains(uuid)) {
            return;
        }
        addElement(uuid);
    }
}
