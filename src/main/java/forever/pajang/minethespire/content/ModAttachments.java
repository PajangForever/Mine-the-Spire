package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.specials.OrbManager;
import forever.pajang.minethespire.content.specials.CombatState;
//import forever.pajang.minethespire.impl.PlayerInnateTracker;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public final class ModAttachments {
    private static final RegisterCore REG = MineTheSpire.REG;

//    public static final Supplier<AttachmentType<PlayerInnateTracker>> INNATE_TRACKER = REG.attachmentType("innate_tracker", _ -> new PlayerInnateTracker(0),
//            builder -> builder.serialize(PlayerInnateTracker.CODEC).copyOnDeath());

    public static final Supplier<AttachmentType<CombatState>> COMBAT_STATE = REG.attachmentType("combat_state", CombatState::new,
            builder -> builder.serialize(CombatState.CODEC).sync(ByteBufCodecs.fromCodec(CombatState.CODEC.codec())));

    public static final Supplier<AttachmentType<OrbManager>> ORB_MANAGER = REG.attachmentType("orb_manager",
            holder -> new OrbManager(holder instanceof LivingEntity owner ? owner : null),
            builder -> builder.serialize(OrbManager.CODEC).sync(OrbManager.STREAM_CODEC));

    public static void register() {}
}
