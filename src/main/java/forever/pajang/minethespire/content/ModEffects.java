package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.effect.MindBloomEffect;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModEffects {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredHolder<MobEffect, MindBloomEffect> MIND_BLOOM = REG.effect(
            "mind_bloom", () -> new MindBloomEffect(MobEffectCategory.HARMFUL, 0x461255));

    public static void register() {
        REG.text(Util.makeDescriptionId("effect", MineTheSpire.id("mind_bloom"))).en("Mind Bloom").register();
    }
}
