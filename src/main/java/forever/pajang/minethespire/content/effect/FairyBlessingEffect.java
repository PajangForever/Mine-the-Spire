package forever.pajang.minethespire.content.effect;

import forever.pajang.minethespire.content.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class FairyBlessingEffect extends MobEffect {
    public FairyBlessingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static boolean tryPreventDeath(LivingEntity entity) {
        if (entity.level() instanceof ServerLevel level && entity.hasEffect(ModEffects.FAIRY_BLESSING)) {
            entity.removeEffect(ModEffects.FAIRY_BLESSING);
            entity.setHealth(entity.getMaxHealth() * 0.3F);
            entity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 2 * 20, 3));
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            level.sendParticles(ParticleTypes.FIREFLY,
                    entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D, entity.getZ(),
                    80, 0.75D, entity.getBbHeight() * 0.45D, 0.75D, 0.35D);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D, entity.getZ(),
                    60, 0.9D, entity.getBbHeight() * 0.55D, 0.9D, 0.25D);
            return true;
        }
        return false;
    }

    public static boolean tryProtectTotem(LivingEntity entity) {
        return entity.hasEffect(ModEffects.FAIRY_BLESSING);
    }
}
