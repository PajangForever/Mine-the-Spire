package forever.pajang.minethespire.content.effect;

import forever.pajang.minethespire.content.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class VeninEffect extends MobEffect {
    public VeninEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static boolean tryDamageOnExpired(LivingEntity entity, MobEffectInstance effect) {
        if (entity.level().isClientSide() || entity.isDeadOrDying() || effect == null || !effect.is(ModEffects.VENIN)) {
            return false;
        }

        int level = effect.getAmplifier() + 1;
        entity.hurtServer((ServerLevel) entity.level(), entity.level().damageSources().source(DamageTypes.MAGIC), (float) level);
        if (level > 1 && !entity.isDeadOrDying()) {
            entity.forceAddEffect(new MobEffectInstance(ModEffects.VENIN, 5 * 20, effect.getAmplifier() - 1), null);
            return true;
        }
        return false;
    }
}
