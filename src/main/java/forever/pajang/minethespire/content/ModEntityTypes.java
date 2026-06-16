package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.entity.*;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModEntityTypes {
    private static final RegisterCore REG = MineTheSpire.REG;
    private static final float ORB_SIZE = 5 / 16f;

    public static final DeferredHolder<EntityType<?>, EntityType<DarkShurikenProjectile>> DARK_SHURIKEN_PROJECTILE =
            REG.entity("dark_shuriken_projectile", DarkShurikenProjectile::new, MobCategory.MISC,
                    b -> b.sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(1));
    public static final DeferredHolder<EntityType<?>, EntityType<BouncingFlaskProjectile>> BOUNCING_FLASK =
            REG.entity("bouncing_flask", BouncingFlaskProjectile::new, MobCategory.MISC,
                    b -> b.sized(0.4F, 0.4F).clientTrackingRange(8).updateInterval(1));
    public static final DeferredHolder<EntityType<?>, EntityType<LightningOrbEntity>> LIGHTNING_CHARGE_BALL =
            REG.entity("lightning_orb", LightningOrbEntity::new, MobCategory.MISC,
                    b -> b.sized(ORB_SIZE, ORB_SIZE).clientTrackingRange(8).updateInterval(1));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostOrbEntity>> FROST_CHARGE_BALL =
            REG.entity("frost_orb", FrostOrbEntity::new, MobCategory.MISC,
                    b -> b.sized(ORB_SIZE, ORB_SIZE).clientTrackingRange(8).updateInterval(1));
    public static final DeferredHolder<EntityType<?>, EntityType<DarkOrbEntity>> DARK_CHARGE_BALL =
            REG.entity("dark_orb", DarkOrbEntity::new, MobCategory.MISC,
                    b -> b.sized(ORB_SIZE, ORB_SIZE).clientTrackingRange(8).updateInterval(1));
    public static final DeferredHolder<EntityType<?>, EntityType<PlasmaOrbEntity>> PLASMA_CHARGE_BALL =
            REG.entity("plasma_orb", PlasmaOrbEntity::new, MobCategory.MISC,
                    b -> b.sized(ORB_SIZE, ORB_SIZE).clientTrackingRange(8).updateInterval(1));

    public static void register() {
    }
}
