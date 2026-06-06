package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.effect.BarricadeEffect;
import forever.pajang.minethespire.content.effect.FairyBlessingEffect;
import forever.pajang.minethespire.content.effect.FocusBoostEffect;
import forever.pajang.minethespire.content.effect.MindBloomEffect;
import forever.pajang.minethespire.content.effect.NoEntityEffect;
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

    public static final DeferredHolder<MobEffect, MindBloomEffect> MIND_BLOOM = REG.effect("mind_bloom", () -> new MindBloomEffect(MobEffectCategory.HARMFUL, 0x461255))
            .en("Mind Bloom")
            .register();
    public static final DeferredHolder<MobEffect, FairyBlessingEffect> FAIRY_BLESSING = REG.effect("fairy_blessing", () -> new FairyBlessingEffect(MobEffectCategory.BENEFICIAL, 0x17D8B3))
            .en("Fairy Blessing")
            .register();
    public static final DeferredHolder<MobEffect, FocusBoostEffect> FOCUS_BOOST = REG.effect("focus_boost", () -> new FocusBoostEffect(MobEffectCategory.BENEFICIAL, 0xD4B6FF))
            .en("Focus Boost")
            .register();
    public static final DeferredHolder<MobEffect, PlasmaChargeEffect> PLASMA_CHARGE = REG.effect("plasma_charge", () -> new PlasmaChargeEffect(MobEffectCategory.BENEFICIAL, 0xB875FF))
            .en("Plasma Charge")
            .register();
    public static final DeferredHolder<MobEffect, BarricadeEffect> BARRICADE = REG.effect("barricade", () -> new BarricadeEffect(MobEffectCategory.BENEFICIAL, 0x8FA6AA))
            .en("Barricade")
            .register();
    public static final DeferredHolder<MobEffect, NoEntityEffect> NO_ENTITY = REG.effect("no_entity", () -> new NoEntityEffect(MobEffectCategory.BENEFICIAL, 0x74F0C8))
            .en("Intangible")
            .register();
    public static final DeferredHolder<MobEffect, QuickBlockEffect> QUICK_BLOCK = REG.effect("quick_block", () -> new QuickBlockEffect(MobEffectCategory.BENEFICIAL, 0x70836F))
            .en("Quick Block")
            .register();
    public static final DeferredHolder<MobEffect, SerpentSpeedEffect> SERPENT_SPEED = REG.effect("serpent_speed", () -> new SerpentSpeedEffect(MobEffectCategory.BENEFICIAL, 0x69B95A))
            .en("Serpent Speed")
            .register();
    public static final DeferredHolder<MobEffect, VulnerableEffect> VULNERABLE = REG.effect("vulnerable", () -> new VulnerableEffect(MobEffectCategory.HARMFUL, 0xC85A5A))
            .en("Vulnerable")
            .register();
    public static final DeferredHolder<MobEffect, VeninEffect> VENIN = REG.effect("venin", () -> new VeninEffect(MobEffectCategory.HARMFUL, 0x4E8D34))
            .en("Venin")
            .register();

    public static void register() {
    }
}
