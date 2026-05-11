package forever.pajang.minethespire.mixin;

import forever.pajang.minethespire.impl.ImplUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Inject(method = "onTake", at = @At("TAIL"))
    private void onTake(Player player, ItemStack carried, CallbackInfo ci) {
        if (ImplUtils.isSlotEquipment((Slot) (Object) this, player)) {
            ImplUtils.applyEthereal(carried, player);
        }
    }
}
