package forever.pajang.minethespire.mixin;

import forever.pajang.minethespire.impl.ChangeSelectedSlotEvent;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Inject(method = "setSelectedSlot", at = @At("HEAD"))
    public void onChangeSelected(int selected, CallbackInfo ci) {
        if (!Inventory.isHotbarSlot(selected)) return;
        Inventory self = (Inventory) (Object) this;
        int before = self.getSelectedSlot();
        ChangeSelectedSlotEvent.post(self, before, selected);
    }
}
