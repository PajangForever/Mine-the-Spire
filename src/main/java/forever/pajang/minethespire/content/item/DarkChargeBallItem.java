package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.impl.ChargeBallManager;
import net.minecraft.world.entity.player.Player;

public class DarkChargeBallItem extends LightningChargeBallItem {
    public DarkChargeBallItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean tryCreateChargeBall(Player player) {
        return ChargeBallManager.get(player).tryCreateDark();
    }
}
