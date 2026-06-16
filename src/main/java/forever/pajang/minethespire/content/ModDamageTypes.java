package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypes {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final ResourceKey<DamageType> ORB_LIGHTNING = REG.damageType("orb_lightning", () -> new DamageType("lightningBolt", 0.1F),
            DamageTypeTags.IS_LIGHTNING, DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.BYPASSES_SHIELD,DamageTypeTags.AVOIDS_GUARDIAN_THORNS,
            DamageTypeTags.NO_KNOCKBACK, DamageTypeTags.PANIC_CAUSES, DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH, DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS);

    public static final ResourceKey<DamageType> ORB_DARK = REG.damageType("orb_dark", () -> new DamageType("magic", 0.1F),
            DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.BYPASSES_SHIELD,DamageTypeTags.AVOIDS_GUARDIAN_THORNS, DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH,
            DamageTypeTags.NO_KNOCKBACK, DamageTypeTags.PANIC_CAUSES, DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS);

    public static final ResourceKey<DamageType> BOUNCING_HIT = REG.damageType("bouncing_hit", () -> new DamageType("thrown", 0.1F),
            DamageTypeTags.NO_KNOCKBACK, DamageTypeTags.PANIC_CAUSES, DamageTypeTags.IS_PROJECTILE, DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH);

    public static void register() {}
}
