package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.impl.ChargeBallManager;
import forever.pajang.minethespire.impl.CombatState;
import forever.pajang.minethespire.impl.DarkShurikenChargeState;
import forever.pajang.minethespire.impl.PlayerInnateTracker;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public final class ModAttachments {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final Supplier<AttachmentType<PlayerInnateTracker>> INNATE_TRACKER = REG.attachmentType("innate_tracker", _ -> new PlayerInnateTracker(0),
            builder -> builder.serialize(PlayerInnateTracker.CODEC).copyOnDeath());
    public static final Supplier<AttachmentType<DarkShurikenChargeState>> DARK_SHURIKEN_CHARGE_STATE = REG.attachmentType("dark_shuriken_charge_state", _ -> new DarkShurikenChargeState(),
            builder -> {});
    public static final Supplier<AttachmentType<CombatState>> COMBAT_STATE = REG.attachmentType("combat_state", _ -> new CombatState(),
            builder -> builder.serialize(CombatState.CODEC).sync(ByteBufCodecs.fromCodec(CombatState.CODEC.codec())));
    public static final Supplier<AttachmentType<ChargeBallManager>> CHARGE_BALL_MANAGER = REG.attachmentType("charge_ball_manager",
            holder -> new ChargeBallManager(holder instanceof LivingEntity owner ? owner : null),
            builder -> builder.serialize(ChargeBallManager.CODEC).sync(ChargeBallManager.STREAM_CODEC));

    public static void register() {}
}
