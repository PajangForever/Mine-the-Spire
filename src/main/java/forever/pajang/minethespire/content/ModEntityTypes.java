package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.entity.FrostChargeBallEntity;
import forever.pajang.minethespire.content.entity.LightningChargeBallEntity;
import forever.pajang.minethespire.content.entity.DarkShurikenProjectile;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModEntityTypes {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredHolder<EntityType<?>, EntityType<DarkShurikenProjectile>> DARK_SHURIKEN_PROJECTILE =
            REG.entity("dark_shuriken_projectile", DarkShurikenProjectile::new, MobCategory.MISC,
                    b -> b.sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
    public static final DeferredHolder<EntityType<?>, EntityType<LightningChargeBallEntity>> LIGHTNING_CHARGE_BALL =
            REG.entity("lightning_charge_ball", LightningChargeBallEntity::new, MobCategory.MISC,
                    b -> b.sized(0.51F, 0.51F).clientTrackingRange(8).updateInterval(1));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostChargeBallEntity>> FROST_CHARGE_BALL =
            REG.entity("frost_charge_ball", FrostChargeBallEntity::new, MobCategory.MISC,
                    b -> b.sized(0.51F, 0.51F).clientTrackingRange(8).updateInterval(1));

    public static void register() {
    }
}
