package forever.pajang.minethespire.impl;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

public class SwapEquipmentEvent extends Event {
    private final Player player;
    private final Equippable equippable;
    private final ItemStack swapOut;
    private final ItemStack swappedToEquipment;

    public SwapEquipmentEvent(Player player, Equippable equippable, ItemStack swapOut, ItemStack swappedToEquipment) {
        this.player = player;
        this.equippable = equippable;
        this.swapOut = swapOut;
        this.swappedToEquipment = swappedToEquipment;
    }

    public Player player() {
        return player;
    }

    public ItemStack swappedToHand() {
        return swapOut;
    }

    public ItemStack swappedToEquipment() {
        return swappedToEquipment;
    }

    public Equippable equippable() {
        return equippable;
    }

    public static void post(Player player, Equippable equippable, ItemStack swapOut, ItemStack swappedToEquipment) {
        NeoForge.EVENT_BUS.post(new SwapEquipmentEvent(player, equippable, swapOut, swappedToEquipment));
    }
}
