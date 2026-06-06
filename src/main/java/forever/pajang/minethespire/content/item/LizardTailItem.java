package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.impl.BlockingValueHandler;
import forever.pajang.minethespire.network.LizardTailActivationPayload;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;

public class LizardTailItem extends Relic {
    private static final DustParticleOptions ORANGE_PARTICLE = new DustParticleOptions(0xFF7A00, 1.35F);
    private static final int RESISTANCE_DURATION = 20;
    private static final int RESISTANCE_AMPLIFIER = 4;

    public LizardTailItem(Properties properties) {
        super(properties);
    }

    public static boolean tryPreventDeath(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return false;
        }

        Set<ItemStack> tails = ModItems.LIZARD_TAIL.get().getFromCuriosOrInventory(entity);
        if (tails.isEmpty()) {
            return false;
        } else {
            applyProtection(entity, tails.iterator().next());
            return true;
        }
    }

    private static void applyProtection(LivingEntity entity, ItemStack tail) {
        float maxHealth = entity.getMaxHealth();
        float blockingValue = maxHealth * 2.0F;
        entity.setHealth(maxHealth * 0.5F);
        BlockingValueHandler.grant(entity, blockingValue);
        entity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, RESISTANCE_DURATION, RESISTANCE_AMPLIFIER));
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        if (entity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, LizardTailActivationPayload.INSTANCE);
        }
        entity.level().addParticle(ORANGE_PARTICLE, entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D, entity.getZ(), 0.0D, 0.0D, 0.0D);
        if (entity.level() instanceof ServerLevel level) {
            level.sendParticles(ORANGE_PARTICLE,
                    entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D, entity.getZ(),
                    140, 0.85D, entity.getBbHeight() * 0.55D, 0.85D, 0.18D);
        }
    }
}