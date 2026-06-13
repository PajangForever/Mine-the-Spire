package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.effect.BarricadeEffect;
import forever.pajang.minethespire.content.effect.FairyBlessingEffect;
import forever.pajang.minethespire.content.effect.FocusBoostEffect;
import forever.pajang.minethespire.content.effect.MindBloomEffect;
import forever.pajang.minethespire.content.effect.IntangibleEffect;
import forever.pajang.minethespire.content.effect.PlasmaChargeEffect;
import forever.pajang.minethespire.content.effect.QuickBlockEffect;
import forever.pajang.minethespire.content.effect.SerpentSpeedEffect;
import forever.pajang.minethespire.content.effect.VeninEffect;
import forever.pajang.minethespire.content.effect.VulnerableEffect;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModEffects {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredHolder<MobEffect, MindBloomEffect> MIND_BLOOM = REG.effect("mind_bloom", MindBloomEffect::new).color(0x461255).isHarmful().register();
    public static final DeferredHolder<MobEffect, FairyBlessingEffect> FAIRY_BLESSING = REG.effect("fairy_blessing", FairyBlessingEffect::new).color(0x17D8B3).register();
    public static final DeferredHolder<MobEffect, FocusBoostEffect> FOCUS_BOOST = REG.effect("focus_boost", FocusBoostEffect::new).color(0xD4B6FF).register();
    public static final DeferredHolder<MobEffect, PlasmaChargeEffect> PLASMA_CHARGE = REG.effect("plasma_charge", PlasmaChargeEffect::new).color(0xB875FF).register();
    public static final DeferredHolder<MobEffect, BarricadeEffect> BARRICADE = REG.effect("barricade", BarricadeEffect::new).color(0x8FA6AA).register();
    public static final DeferredHolder<MobEffect, IntangibleEffect> NO_ENTITY = REG.effect("intangible", IntangibleEffect::new).color(0x74F0C8).register();
    public static final DeferredHolder<MobEffect, QuickBlockEffect> QUICK_BLOCK = REG.effect("quick_block", QuickBlockEffect::new).color(0x70836F).register();
    public static final DeferredHolder<MobEffect, SerpentSpeedEffect> SERPENT_SPEED = REG.effect("serpent_speed", SerpentSpeedEffect::new).color(0x69B95A).register();
    public static final DeferredHolder<MobEffect, VulnerableEffect> VULNERABLE = REG.effect("vulnerable", VulnerableEffect::new).color(0xC85A5A).isHarmful().register();
    public static final DeferredHolder<MobEffect, VeninEffect> VENIN = REG.effect("venin", VeninEffect::new).color(0x4E8D34).isHarmful().register();

    public static void register() {
    }
}
