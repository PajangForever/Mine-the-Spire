package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.content.specials.OrbManager;
import forever.pajang.minethespire.content.specials.OrbType;
import net.minecraft.world.entity.player.Player;

public class DarkOrbItem extends OrbItem {
    public DarkOrbItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean channelOrb(Player player) {
        return OrbManager.get(player).tryChannel(OrbType.Dark);
    }
}
