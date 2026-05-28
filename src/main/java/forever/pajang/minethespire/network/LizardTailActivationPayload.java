package forever.pajang.minethespire.network;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.client.ClientEventListeners;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LizardTailActivationPayload() implements CustomPacketPayload {
    public static final LizardTailActivationPayload INSTANCE = new LizardTailActivationPayload();
    public static final Type<LizardTailActivationPayload> TYPE = new Type<>(MineTheSpire.id("lizard_tail_activation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LizardTailActivationPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<LizardTailActivationPayload> type() {
        return TYPE;
    }

    public static void handle(LizardTailActivationPayload payload, IPayloadContext context) {
        context.enqueueWork(ClientEventListeners::displayLizardTailActivation);
    }
}
