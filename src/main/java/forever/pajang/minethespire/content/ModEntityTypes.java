package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
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

    public static void register() {
    }
}
