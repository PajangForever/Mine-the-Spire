package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.effect.*;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModEffects {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredHolder<MobEffect, MindBloomEffect> MIND_BLOOM = REG.effect("mind_bloom", MindBloomEffect::new).color(0x461255).isHarmful().register();
    public static final DeferredHolder<MobEffect, FairyBlessingEffect> FAIRY_BLESSING = REG.effect("fairy_blessing", FairyBlessingEffect::new).color(0x17D8B3).isBeneficial().register();
    public static final DeferredHolder<MobEffect, FocusBoostEffect> FOCUS_BOOST = REG.effect("focus_boost", FocusBoostEffect::new).color(0xD4B6FF).renderLevel().isBeneficial().register();
    public static final DeferredHolder<MobEffect, PlasmaChargeEffect> PLASMA_CHARGE = REG.effect("plasma_charge", PlasmaChargeEffect::new).color(0xB875FF).renderLevel().isBeneficial().register();
    public static final DeferredHolder<MobEffect, BarricadeEffect> BARRICADE = REG.effect("barricade", BarricadeEffect::new).color(0x8FA6AA).isBeneficial().register();
    public static final DeferredHolder<MobEffect, IntangibleEffect> INTANGIBLE = REG.effect("intangible", IntangibleEffect::new).color(0x74F0C8).register();
    public static final DeferredHolder<MobEffect, QuickBlockEffect> QUICK_BLOCK = REG.effect("quick_block", QuickBlockEffect::new).color(0x70836F).isBeneficial().register();
    @Deprecated
    public static final DeferredHolder<MobEffect, SerpentSpeedEffect> SERPENT_SPEED = REG.effect("serpent_speed", SerpentSpeedEffect::new).color(0x69B95A).isBeneficial().register();
    public static final DeferredHolder<MobEffect, VulnerableEffect> VULNERABLE = REG.effect("vulnerable", VulnerableEffect::new).color(0xC85A5A).isHarmful().register();
    public static final DeferredHolder<MobEffect, VeninEffect> VENIN = REG.effect("venin", VeninEffect::new).color(0x4E8D34).isHarmful().renderLevel().register();
    public static final DeferredHolder<MobEffect, VigorEffect> VIGOR = REG.effect("vigor", VigorEffect::new).color(0xFFCB48).isBeneficial().renderLevel().register();

    public static void register() {
    }
}
