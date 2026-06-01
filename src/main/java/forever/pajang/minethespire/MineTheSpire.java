package forever.pajang.minethespire;

import forever.pajang.minethespire.compat.curios.CuriosCompat;
import forever.pajang.minethespire.compat.jade.OverhealJade;
import forever.pajang.minethespire.content.ModAttachments;
import forever.pajang.minethespire.content.ModAttributes;
import forever.pajang.minethespire.content.ModDataComponents;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModEnchantments;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.ModPotions;
import forever.pajang.minethespire.register.ModDataProviders;
import forever.pajang.minethespire.register.RegisterCore;
import forever.pajang.minethespire.network.ModNetworking;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

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
        CuriosCompat.registerEventsIfLoaded();
        OverhealJade.register();
        ModDataProviders.register(REG);
        modEventBus.addListener(ModNetworking::register);
        REG.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @Deprecated
    public static void debug() {
        LOGGER.debug("Triggered!");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
