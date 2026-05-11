package forever.pajang.minethespire.impl;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModAttachments;
import forever.pajang.minethespire.content.ModDataComponents;
import forever.pajang.minethespire.content.ModEnchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@EventBusSubscriber(modid = MineTheSpire.MODID)
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

}
