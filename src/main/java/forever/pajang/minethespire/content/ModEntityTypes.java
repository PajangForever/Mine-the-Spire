package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.entity.*;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModEntityTypes {
    private static final RegisterCore REG = MineTheSpire.REG;
    private static final float CHARGE_BALL_SIZE = 5 / 16f;

    public static final DeferredHolder<EntityType<?>, EntityType<DarkShurikenProjectile>> DARK_SHURIKEN_PROJECTILE =
            REG.entity("dark_shuriken_projectile", DarkShurikenProjectile::new, MobCategory.MISC,
                    b -> b.sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
    public static final DeferredHolder<EntityType<?>, EntityType<BouncingFlaskProjectile>> BOUNCING_FLASK =
            REG.entity("bouncing_flask", BouncingFlaskProjectile::new, MobCategory.MISC,
                    b -> b.sized(0.25F, 0.25F).clientTrackingRange(8).updateInterval(1));
    public static final DeferredHolder<EntityType<?>, EntityType<LightningOrbEntity>> LIGHTNING_CHARGE_BALL =
            REG.entity("lightning_charge_ball", LightningOrbEntity::new, MobCategory.MISC,
                    b -> b.sized(CHARGE_BALL_SIZE, CHARGE_BALL_SIZE).clientTrackingRange(8).updateInterval(1));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostOrbEntity>> FROST_CHARGE_BALL =
            REG.entity("frost_charge_ball", FrostOrbEntity::new, MobCategory.MISC,
                    b -> b.sized(CHARGE_BALL_SIZE, CHARGE_BALL_SIZE).clientTrackingRange(8).updateInterval(1));
    public static final DeferredHolder<EntityType<?>, EntityType<DarkOrbEntity>> DARK_CHARGE_BALL =
            REG.entity("dark_charge_ball", DarkOrbEntity::new, MobCategory.MISC,
                    b -> b.sized(CHARGE_BALL_SIZE, CHARGE_BALL_SIZE).clientTrackingRange(8).updateInterval(1));
    public static final DeferredHolder<EntityType<?>, EntityType<PlasmaOrbEntity>> PLASMA_CHARGE_BALL =
            REG.entity("plasma_charge_ball", PlasmaOrbEntity::new, MobCategory.MISC,
                    b -> b.sized(CHARGE_BALL_SIZE, CHARGE_BALL_SIZE).clientTrackingRange(8).updateInterval(1));

    public static void register() {
    }
}
