package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.content.ModAttributes;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.content.specials.CombatState;
import forever.pajang.minethespire.impl.ActivatableStatesAttribute;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.cow.CowSoundVariants;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AkabekoItem extends RelicItem{
    public AkabekoItem(Properties properties) {
        super(properties);
    }

    public static void checkAttributeAndApplyVigor(LivingEntity entity) {
        Level level = entity.level();
        if (level.isClientSide()) return;
        if (ActivatableStatesAttribute.getBoolean(ModAttributes.State.AKABEKO.getIndex(), entity.getAttributeValue(ModAttributes.FLAGS_GROUP_0))) {
            applyVigor(entity, level);
        }
    }

    public static void checkStackAndApplyVigor(LivingEntity entity, ItemStack stack) {
        Level level = entity.level();
        if (level.isClientSide()) return;
        if (stack.is(ModItems.AKABEKO) && !CombatState.isInCombat(entity)) {
            applyVigor(entity, level);
        }
    }

    private static void applyVigor(LivingEntity entity, Level level) {
        entity.addEffect(new MobEffectInstance(ModEffects.VIGOR, -1, 7));
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.COW_SOUNDS.get(CowSoundVariants.SoundSet.CLASSIC).hurtSound(), entity.getSoundSource(), 0.8F, 1.3F);
        ((ServerLevel) level).sendParticles(ParticleTypes.FLAME, entity.getX(), entity.getY(0.5), entity.getZ(), 20, 0.5D, entity.getBbHeight() * 0.5D, 0.5D, 0.1D);
    }
}
