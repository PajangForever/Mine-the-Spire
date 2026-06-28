package forever.pajang.minethespire.content.effect;

import forever.pajang.minethespire.content.ModEffects;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public class IntangibleEffect extends MobEffect {
    public IntangibleEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static void reduceDamage(LivingEntity entity, DamageSource source, Supplier<Float> getter, FloatConsumer setter) {
        if (entity.hasEffect(ModEffects.INTANGIBLE)
                && !source.is(DamageTypes.GENERIC_KILL)
                && getter.get() > 1.0F) {
            setter.accept(1.0F);
        }
    }
}
