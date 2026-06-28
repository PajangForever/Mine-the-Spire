package forever.pajang.minethespire.register;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import snownee.jade.addon.vanilla.StatusEffectsProvider;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class EffectBuilder<T extends MobEffect> extends RegisterCore.Builder {
    private String en = null;
    private int color;
    private MobEffectCategory sentiment = MobEffectCategory.NEUTRAL;
    private BiFunction<MobEffectCategory, Integer, T> constructor;
    private boolean renderLevel = false;

    EffectBuilder(RegisterCore registerCore, String name, BiFunction<MobEffectCategory, Integer, T> constructor) {
        super(registerCore, name);
        this.constructor = constructor;
    }

    public EffectBuilder<T> isBeneficial() {
        sentiment = MobEffectCategory.BENEFICIAL;
        return this;
    }

    public EffectBuilder<T> isHarmful() {
        sentiment = MobEffectCategory.HARMFUL;
        return this;
    }

    public EffectBuilder<T> color(int color) {
        this.color = color;
        return this;
    }

    public EffectBuilder<T> renderLevel() {
        this.renderLevel = true;
        return this;
    }

    public EffectBuilder<T> en(String en) {
        this.en = en;
        return this;
    }

    public DeferredHolder<MobEffect, T> register() {
        DeferredHolder<MobEffect, T> effect = registerCore.effects.register(name, () -> constructor.apply(sentiment, color));
        if (renderLevel) {
            registerCore.renderEffectLevels.add(effect);
        }
        if (registerCore.runningDataGen()) {
            String display = this.en == null ? RegisterCore.getDisplayTitle(this.name) : this.en;
            registerCore.deferredLang.put(() -> effect.get().getDescriptionId(), display);
        }
        return effect;
    }
}
