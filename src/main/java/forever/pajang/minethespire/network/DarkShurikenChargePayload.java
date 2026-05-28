package forever.pajang.minethespire.network;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.impl.DarkShurikenChargeTracker;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DarkShurikenChargePayload() implements CustomPacketPayload {
    public static final DarkShurikenChargePayload INSTANCE = new DarkShurikenChargePayload();
    public static final Type<DarkShurikenChargePayload> TYPE = new Type<>(MineTheSpire.id("dark_shuriken_charge"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DarkShurikenChargePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<DarkShurikenChargePayload> type() {
        return TYPE;
    }

    public static void handle(DarkShurikenChargePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null) {
                DarkShurikenChargeTracker.trigger(context.player());
            }
        });
    }
}
