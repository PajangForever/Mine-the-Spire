package forever.pajang.minethespire.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(DarkShurikenChargePayload.TYPE, DarkShurikenChargePayload.STREAM_CODEC, DarkShurikenChargePayload::handle);
    }
}
