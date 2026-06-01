package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class ModDamageTypes {
    public static final ResourceKey<DamageType> CHARGE_BALL_LIGHTNING = ResourceKey.create(Registries.DAMAGE_TYPE, MineTheSpire.id("charge_ball_lightning"));

    private ModDamageTypes() {
    }

    public static DamageSource chargeBallLightning(Level level, Entity directEntity, @Nullable Entity owner) {
        return owner == null
                ? level.damageSources().source(CHARGE_BALL_LIGHTNING, directEntity)
                : level.damageSources().source(CHARGE_BALL_LIGHTNING, directEntity, owner);
    }
}
