package forever.pajang.minethespire.impl;

import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.network.LizardTailActivationPayload;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public final class LizardTailHandler {
    private static final DustParticleOptions ORANGE_PARTICLE = new DustParticleOptions(0xFF7A00, 1.35F);
    private static final int RESISTANCE_DURATION = 20;
    private static final int RESISTANCE_AMPLIFIER = 4;

    private LizardTailHandler() {
    }

    public static boolean tryPreventDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return false;
        }

        ICuriosItemHandler curios = CuriosApi.getCuriosInventoryOrNull(entity);
        if (curios == null) {
            return false;
        }

        SlotResult tail = curios.findFirstCurio(stack -> stack.is(ModItems.LIZARD_TAIL.get())).orElse(null);
        if (tail == null) {
            return false;
        }

        consumeTail(curios, tail);
        applyProtection(entity);
        event.setCanceled(true);
        return true;
    }

    private static void consumeTail(ICuriosItemHandler curios, SlotResult tail) {
        ItemStack stack = tail.stack().copy();
        stack.shrink(1);
        curios.setEquippedCurio(tail.slotContext().identifier(), tail.slotContext().index(), stack);
    }

    private static void applyProtection(LivingEntity entity) {
        float maxHealth = entity.getMaxHealth();
        float overheal = maxHealth * 2.0F;

        entity.setHealth(maxHealth * 0.5F);
        OverhealHandler.grant(entity, overheal);
        entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.RESISTANCE, RESISTANCE_DURATION, RESISTANCE_AMPLIFIER));
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        if (entity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, LizardTailActivationPayload.INSTANCE);
        }
        entity.level().addParticle(ORANGE_PARTICLE, entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D, entity.getZ(), 0.0D, 0.0D, 0.0D);
        if (entity.level() instanceof net.minecraft.server.level.ServerLevel level) {
            level.sendParticles(ORANGE_PARTICLE,
                    entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D, entity.getZ(),
                    140, 0.85D, entity.getBbHeight() * 0.55D, 0.85D, 0.18D);
        }
    }
}
