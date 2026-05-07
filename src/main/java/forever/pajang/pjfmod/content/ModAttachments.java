package forever.pajang.pjfmod.content;

import forever.pajang.pjfmod.PajangForeversMod;
import forever.pajang.pjfmod.impl.PlayerInnateTracker;
import forever.pajang.pjfmod.register.RegisterCore;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public final class ModAttachments {
    private static final RegisterCore REG = PajangForeversMod.REG;

    public static final Supplier<AttachmentType<PlayerInnateTracker>> INNATE_TRACKER = REG.attachmentType("innate_tracker", _ -> new PlayerInnateTracker(0),
            builder -> builder.serialize(PlayerInnateTracker.CODEC).copyOnDeath());

    public static void register() {}
}
