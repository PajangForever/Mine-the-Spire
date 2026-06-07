package forever.pajang.minethespire.network;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.item.DarkShurikenItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public enum DarkShurikenChargePayload implements CustomPacketPayload {

    INSTANCE;

    public static final Type<DarkShurikenChargePayload> TYPE = new Type<>(MineTheSpire.id("dark_shuriken_charge"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DarkShurikenChargePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<DarkShurikenChargePayload> type() {
        return TYPE;
    }

    public static void handle(DarkShurikenChargePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> DarkShurikenItem.triggerServer(context.player()));
    }
}
