package forever.pajang.minethespire.impl;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModAttachments;
import forever.pajang.minethespire.content.ModDataComponents;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModEnchantments;
import forever.pajang.minethespire.content.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@EventBusSubscriber(modid = MineTheSpire.MODID)
public final class EventListeners {

    @SubscribeEvent
    public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
        MineTheSpire.REG.registerBrewingRecipes(event);
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        MineTheSpire.REG.addEntityAttributes(event);
    }

    @SubscribeEvent
    public static void onServerToStart(ServerAboutToStartEvent event) {
        PlayerInnateTracker.TRACKER.clear();
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        PlayerInnateTracker.TRACKER.clear();
    }

    @SubscribeEvent
    public static void onSwapEquipment(SwapEquipmentEvent event) {
        ImplUtils.applyEthereal(event.swappedToHand(), event.player());
    }

    @SubscribeEvent
    public static void onChangeSelectedSlot(ChangeSelectedSlotEvent event) {
        ImplUtils.applyEthereal(event.inventory().getItem(event.before()), event.player());
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity().getMainHandItem().is(ModItems.DARK_SHURIKEN.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().getMainHandItem().is(ModItems.DARK_SHURIKEN.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (event.getEntity().hasEffect(ModEffects.MIND_BLOOM)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (event.getEntity().hasEffect(ModEffects.NO_ENTITY)
                && !event.getSource().is(DamageTypes.GENERIC_KILL)
                && !event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)
                && event.getNewDamage() > 1.0F) {
            event.setNewDamage(1.0F);
        }
        OverhealHandler.onDamage(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onLivingUseTotem(LivingUseTotemEvent event) {
        if (event.getEntity().hasEffect(ModEffects.MIND_BLOOM) || event.getEntity().hasEffect(ModEffects.FAIRY_BLESSING)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level() instanceof ServerLevel level && event.getEntity().hasEffect(ModEffects.FAIRY_BLESSING)) {
            event.setCanceled(true);
            event.getEntity().removeEffect(ModEffects.FAIRY_BLESSING);
            event.getEntity().setHealth(event.getEntity().getMaxHealth() * 0.3F);
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 2 * 20, 3));
            level.playSound(null, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    event.getEntity().getX(), event.getEntity().getY() + event.getEntity().getBbHeight() * 0.5D, event.getEntity().getZ(),
                    80, 0.75D, event.getEntity().getBbHeight() * 0.45D, 0.75D, 0.35D);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    event.getEntity().getX(), event.getEntity().getY() + event.getEntity().getBbHeight() * 0.5D, event.getEntity().getZ(),
                    60, 0.9D, event.getEntity().getBbHeight() * 0.55D, 0.9D, 0.25D);
            return;
        }

        if (LizardTailHandler.tryPreventDeath(event)) {
            return;
        }
    }

    @SubscribeEvent
    public static void onMindBloomRemoved(MobEffectEvent.Remove event) {
        if (!event.getEntity().isDeadOrDying() && event.getEffect().is(ModEffects.MIND_BLOOM)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMindBloomExpired(MobEffectEvent.Expired event) {
        if (!event.getEntity().isDeadOrDying() && event.getEffectInstance() != null && event.getEffectInstance().is(ModEffects.MIND_BLOOM)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity living) {
            OverhealHandler.tick(living);
        }
    }

    @SubscribeEvent
    public static void onTossItem(ItemTossEvent event) {
        ImplUtils.applyEthereal(event.getEntity());
    }

    @SubscribeEvent
    public static void onTableEnchantingItem(PlayerEnchantItemEvent event) {
        Player player = event.getEntity();
        ItemStack stack = event.getEnchantedItem();
        if (EnchantmentHelper.has(stack, ModEnchantments.DATA_INNATE.get())) {
            stack.set(ModDataComponents.INNATE_TRACKER, new ItemStackInnateTracker(player.getUUID(), 0));
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        Player player = event.getPlayer();
        if (!player.level().isClientSide()){
            ItemStack result = event.getOutput();
            ImplUtils.updateInnateTracker(result, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            PlayerInnateTracker tracker = player.getData(ModAttachments.INNATE_TRACKER);
            tracker.restoreCounter().increment();
            tracker.innateStacks().forEach(i -> player.addItem(i.copy()));
            player.setData(ModAttachments.INNATE_TRACKER, tracker);
        }
    }

}
