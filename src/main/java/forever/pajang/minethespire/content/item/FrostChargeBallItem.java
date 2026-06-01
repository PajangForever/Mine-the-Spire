package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.impl.ChargeBallManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class FrostChargeBallItem extends LightningChargeBallItem {
    public FrostChargeBallItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean tryCreateChargeBall(Player player) {
        return ChargeBallManager.get(player).tryCreateFrost();
    }
}
