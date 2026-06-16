package forever.pajang.minethespire;

import forever.pajang.minethespire.compat.curios.CuriosCompat;
import forever.pajang.minethespire.compat.curios.ModCuriosSlot;
import forever.pajang.minethespire.compat.jade.BlockingValueJade;
import forever.pajang.minethespire.content.*;
import forever.pajang.minethespire.impl.EventListeners;
import forever.pajang.minethespire.register.RegisterCore;
import forever.pajang.minethespire.network.ModNetworking;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(MineTheSpire.MODID)
public class MineTheSpire {
    public static final String MODID = "minethespire";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final RegisterCore REG = RegisterCore.create(MODID);

    public MineTheSpire(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.register();
        ModEntityTypes.register();
        ModEffects.register();
        ModPotions.register();
        ModAttachments.register();
        ModAttributes.register();
        ModDataComponents.register();
        ModEnchantments.register();
        ModDamageTypes.register();
        MiscRegister.register();
        ModCuriosSlot.register();
        modEventBus.addListener(ModNetworking::register);
        modEventBus.register(EventListeners.OnModBus.class);
        CuriosCompat.registerEventsIfLoaded(modEventBus);
        BlockingValueJade.register();
        ConfigTheSpire.register(modContainer);
        REG.register(modEventBus);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @Deprecated
    public static void debug() {
        LOGGER.debug("Triggered!");
    }
}
