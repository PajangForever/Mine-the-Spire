package forever.pajang.minethespire.mixin;

import forever.pajang.minethespire.content.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
    private void preventMindBloomHealing(float health, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(ModEffects.MIND_BLOOM) && health > self.getHealth()) {
            ci.cancel();
        }
    }

    @Inject(method = "removeEffectNoUpdate", at = @At("HEAD"), cancellable = true)
    private void preventMindBloomSilentRemoval(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.isDeadOrDying() && effect.is(ModEffects.MIND_BLOOM)) {
            cir.setReturnValue(null);
        }
    }
}
