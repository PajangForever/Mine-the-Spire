package forever.pajang.minethespire.content.effect;

import forever.pajang.minethespire.content.ModEffects;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public class VulnerableEffect extends MobEffect {
    public VulnerableEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static void boostDamage(LivingEntity target, Supplier<Float> getter, FloatConsumer setter) {
        MobEffectInstance effect = target.getEffect(ModEffects.VULNERABLE);
        if (effect == null) {
            return;
        }

        float multiplier = 1.0F + 0.25F * (effect.getAmplifier() + 1);
        setter.accept(getter.get() * multiplier);
    }
}
