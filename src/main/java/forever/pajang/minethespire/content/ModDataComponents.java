package forever.pajang.minethespire.content;

import com.mojang.serialization.Codec;
import forever.pajang.minethespire.MineTheSpire;
//import forever.pajang.minethespire.impl.ItemStackInnateTracker;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public final class ModDataComponents {
    private static final RegisterCore REG = MineTheSpire.REG;

//    public static final Supplier<DataComponentType<ItemStackInnateTracker>> INNATE_TRACKER = REG.dataComponent("innate_tracker",
//                builder -> builder.persistent(ItemStackInnateTracker.CODEC.codec()));
    public static final Supplier<DataComponentType<Integer>> DOUBLE_RELEASE_ENERGY = REG.dataComponent("double_release_energy",
            builder -> builder.persistent(Codec.INT)
                    .networkSynchronized(StreamCodec.of(
                            (RegistryFriendlyByteBuf buf, Integer value) -> buf.writeVarInt(value),
                            RegistryFriendlyByteBuf::readVarInt
                    )));

    public static void register() {}
}
