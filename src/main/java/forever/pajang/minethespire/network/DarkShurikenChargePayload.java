package forever.pajang.minethespire.network;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.impl.DarkShurikenChargeTracker;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DarkShurikenChargePayload(boolean charging) implements CustomPacketPayload {
    public static final Type<DarkShurikenChargePayload> TYPE = new Type<>(MineTheSpire.id("dark_shuriken_charge"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DarkShurikenChargePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, DarkShurikenChargePayload::charging, DarkShurikenChargePayload::new);

    @Override
    public Type<DarkShurikenChargePayload> type() {
        return TYPE;
    }

    public static void handle(DarkShurikenChargePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null) {
                if (payload.charging()) {
                    DarkShurikenChargeTracker.start(context.player());
                }
                else {
                    DarkShurikenChargeTracker.stop(context.player());
                }
            }
        });
    }
}
