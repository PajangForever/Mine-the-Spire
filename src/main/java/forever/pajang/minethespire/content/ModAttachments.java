package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.impl.PlayerInnateTracker;
import forever.pajang.minethespire.register.RegisterCore;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public final class ModAttachments {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final Supplier<AttachmentType<PlayerInnateTracker>> INNATE_TRACKER = REG.attachmentType("innate_tracker", _ -> new PlayerInnateTracker(0),
            builder -> builder.serialize(PlayerInnateTracker.CODEC).copyOnDeath());

    public static void register() {}
}
