package forever.pajang.pjfmod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import forever.pajang.pjfmod.impl.SwapEquipmentEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Equippable.class)
public class EquippableMixin {
    @Inject(method = "swapWithEquipmentSlot(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/InteractionResult;",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"
            , shift = At.Shift.AFTER, ordinal = 0))
    private void onSwapUncountable(ItemStack inHand, Player player, CallbackInfoReturnable<InteractionResult> cir,
                      @Local(name = "inEquipmentSlot") ItemStack inEquipmentSlot,
                      @Local(name = "swappedToHand") ItemStack swappedToHand,
                      @Local(name = "swappedToEquipment") ItemStack swappedToEquipment) {
        SwapEquipmentEvent.post(player, (Equippable) (Object) this, inEquipmentSlot.isEmpty() ? ItemStack.EMPTY : swappedToHand, swappedToEquipment);
    }

    @Inject(method = "swapWithEquipmentSlot(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"
                    , shift = At.Shift.AFTER, ordinal = 1))
    private void onSwapCountable(ItemStack inHand, Player player, CallbackInfoReturnable<InteractionResult> cir,
                      @Local(name = "swappedToInventory") ItemStack swappedToInventory,
                      @Local(name = "swappedToEquipment") ItemStack swappedToEquipment) {
        SwapEquipmentEvent.post(player, (Equippable) (Object) this, swappedToInventory, swappedToEquipment);
    }
}
