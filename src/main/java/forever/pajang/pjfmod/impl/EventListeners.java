package forever.pajang.pjfmod.impl;

import forever.pajang.pjfmod.PajangForeversMod;
import forever.pajang.pjfmod.content.ModAttachments;
import forever.pajang.pjfmod.content.ModDataComponents;
import forever.pajang.pjfmod.content.ModEnchantments;
import forever.pajang.pjfmod.content.GreatswordItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = PajangForeversMod.MODID)
public final class EventListeners {

    @SubscribeEvent
    public static void onServerToStart(ServerAboutToStartEvent event) {
        PlayerInnateTracker.TRACKER.clear();
    }

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

//    @SubscribeEvent
//    public static void onAttackEntity(AttackEntityEvent event) {
//        Player player = event.getEntity();
//        if (!GreatswordItem.isGreatsword(player.getMainHandItem())) {
//            return;
//        }
//        if (!(event.getTarget() instanceof LivingEntity target)) {
//            return;
//        }
//        GreatswordItem.performSweep(player, target);
//    }
//
//    @SubscribeEvent
//    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
//        if (!(event.getEntity() instanceof Player player)) {
//            return;
//        }
//        if (!GreatswordItem.isBlocking(player)) {
//            return;
//        }
//        DamageSource source = event.getSource();
//        if (!GreatswordItem.isBlockableDamage(source)) {
//            return;
//        }
//        Vec3 sourcePos = source.getSourcePosition();
//        if (sourcePos == null && source.getEntity() != null) {
//            sourcePos = source.getEntity().position();
//        }
//        if (sourcePos == null || !GreatswordItem.isInFrontArc(player, sourcePos)) {
//            return;
//        }
//        GreatswordItem.damageBlockingItem(player);
//        event.setCanceled(true);
//    }
//
//    @SubscribeEvent
//    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
//        if (event.getHand() != InteractionHand.OFF_HAND) {
//            return;
//        }
//        Player player = event.getEntity();
//        if (!GreatswordItem.isGreatsword(player.getMainHandItem())) {
//            return;
//        }
//        event.setCanceled(true);
//    }
//
//    @SubscribeEvent
//    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
//        if (event.getHand() != InteractionHand.OFF_HAND) {
//            return;
//        }
//        Player player = event.getEntity();
//        if (!GreatswordItem.isGreatsword(player.getMainHandItem())) {
//            return;
//        }
//        event.setCanceled(true);
//    }
//
//    @SubscribeEvent
//    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
//        if (event.getHand() != InteractionHand.OFF_HAND) {
//            return;
//        }
//        Player player = event.getEntity();
//        if (!GreatswordItem.isGreatsword(player.getMainHandItem())) {
//            return;
//        }
//        event.setCanceled(true);
//    }
//
//    @SubscribeEvent
//    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
//        if (event.getHand() != InteractionHand.OFF_HAND) {
//            return;
//        }
//        Player player = event.getEntity();
//        if (!GreatswordItem.isGreatsword(player.getMainHandItem())) {
//            return;
//        }
//        event.setCanceled(true);
//    }

}
