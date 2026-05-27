package forever.pajang.minethespire.impl;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModAttachments;
import forever.pajang.minethespire.content.ModDataComponents;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModEnchantments;
import forever.pajang.minethespire.content.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import forever.pajang.minethespire.impl.DarkShurikenChargeTracker;

@EventBusSubscriber(modid = MineTheSpire.MODID)
public final class EventListeners {

    @SubscribeEvent
    public static void onServerToStart(ServerAboutToStartEvent event) {
        PlayerInnateTracker.TRACKER.clear();
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        PlayerInnateTracker.TRACKER.clear();
        DarkShurikenChargeTracker.CHARGES.clear();
        DarkShurikenChargeTracker.BELL_RINGS.clear();
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
    public static void onLivingUseTotem(LivingUseTotemEvent event) {
        if (event.getEntity().hasEffect(ModEffects.MIND_BLOOM)) {
            event.setCanceled(true);
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
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide()) {
            DarkShurikenChargeTracker.tick(event.getEntity());
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
            DarkShurikenChargeTracker.stop(player);
            DarkShurikenChargeTracker.BELL_RINGS.remove(player.getUUID());
            PlayerInnateTracker tracker = player.getData(ModAttachments.INNATE_TRACKER);
            tracker.restoreCounter().increment();
            tracker.innateStacks().forEach(i -> player.addItem(i.copy()));
            player.setData(ModAttachments.INNATE_TRACKER, tracker);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        DarkShurikenChargeTracker.clear(event.getEntity());
    }

}
