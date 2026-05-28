package forever.pajang.minethespire.register;

import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public final class EffectBuilder<T extends MobEffect> extends RegisterCore.Builder {
    private final Supplier<T> constructor;
    private String en = null;

    EffectBuilder(RegisterCore registerCore, String name, Supplier<T> constructor) {
        super(registerCore, name);
        this.constructor = constructor;
    }

    public EffectBuilder<T> en(String en) {
        this.en = en;
        return this;
    }

    public DeferredHolder<MobEffect, T> register() {
        DeferredHolder<MobEffect, T> effect = registerCore.effects.register(name, constructor);
        if (registerCore.runningDataGen()) {
            String display = this.en == null ? RegisterCore.getDisplayTitle(this.name) : this.en;
            registerCore.deferredLang.put(() -> effect.get().getDescriptionId(), display);
        }
        return effect;
    }
}
