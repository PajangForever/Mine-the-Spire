package forever.pajang.minethespire.impl;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
@Deprecated
public final class ChangeSelectedSlotEvent extends Event {
    private final Inventory inventory;
    private final int before;
    private final int after;

    public ChangeSelectedSlotEvent(Inventory inventory, int before, int after) {
        this.inventory = inventory;
        this.before = before;
        this.after = after;
    }

    public Player player() {
        return inventory().player;
    }

    public Inventory inventory() {
        return inventory;
    }

    public int before() {
        return before;
    }

    public int after() {
        return after;
    }

    public static void post(Inventory inventory, int before, int after) {
        NeoForge.EVENT_BUS.post(new ChangeSelectedSlotEvent(inventory, before, after));
    }

}
