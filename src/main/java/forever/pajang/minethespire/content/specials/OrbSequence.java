package forever.pajang.minethespire.content.specials;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PairCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import forever.pajang.minethespire.util.Sequence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class OrbSequence extends Sequence<UUID> {
    public static final Codec<OrbSequence> CODEC = RecordCodecBuilder.create(instance -> instance.apply2(
            OrbSequence::new,
            Codec.INT.fieldOf("capacity").forGetter(OrbSequence::capacity),
            UUIDUtil.CODEC.listOf().fieldOf("uuids").forGetter(OrbSequence::uuids)));

    public OrbSequence() {
        super(1);
    }

    public OrbSequence(int capacity, Collection<UUID> ids) {
        super(Math.max(ids.size(), capacity));
        ids.forEach(this::addUnique);
    }

    public Optional<UUID> addOrb(UUID uuid, int maxSize) {
        removeElement(uuid);
        setCapacity(Math.max(1, maxSize));
        return addElement(uuid);
    }

    public void removeOrb(UUID uuid) {
        removeElement(uuid);
    }

    public List<UUID> uuids() {
        return List.copyOf(this);
    }

    private void addUnique(UUID uuid) {
        if (contains(uuid)) {
            return;
        }
        addElement(uuid);
    }

//    public void encode(RegistryFriendlyByteBuf buf) {
//        buf.writeVarInt(size());
//        forEach(buf::writeUUID);
//    }
//
//    public static OrbSequence decode(RegistryFriendlyByteBuf buf) {
//        int size = buf.readVarInt();
//        java.util.ArrayList<UUID> ids = new java.util.ArrayList<>(size);
//        for (int i = 0; i < size; i++) {
//            ids.add(buf.readUUID());
//        }
//        return new OrbSequence(ids);

//    }
}
