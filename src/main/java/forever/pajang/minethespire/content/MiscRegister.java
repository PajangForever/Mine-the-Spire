package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.UUID;
import java.util.function.Supplier;

public final class MiscRegister {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZER = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, MineTheSpire.MODID);

    public static final Supplier<EntityDataSerializer<UUID>> UUID_SERIALIZER = ENTITY_DATA_SERIALIZER.register("uuid", () -> new EntityDataSerializer<>() {
        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, UUID> codec() {
            return UUIDUtil.STREAM_CODEC;
        }

        @Override
        public UUID copy(UUID value) {
            return new UUID(value.getMostSignificantBits(), value.getLeastSignificantBits());
        }
    });

    public static void register() {}
}
