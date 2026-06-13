package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.content.specials.OrbManager;
import net.minecraft.world.entity.player.Player;

public class PlasmaChargeBallItem extends LightningChargeBallItem {
    public PlasmaChargeBallItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean tryCreateChargeBall(Player player) {
        return OrbManager.get(player).tryCreatePlasma();
    }
}
