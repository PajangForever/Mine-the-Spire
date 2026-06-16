package forever.pajang.minethespire.content.specials;

import forever.pajang.minethespire.content.entity.DarkOrbEntity;
import forever.pajang.minethespire.content.entity.FrostOrbEntity;
import forever.pajang.minethespire.content.entity.LightningOrbEntity;
import forever.pajang.minethespire.content.entity.PlasmaOrbEntity;

import java.util.function.Predicate;

public enum OrbType {
    Lightning(m -> m.tryChannel(LightningOrbEntity::new)),
    Frost(m -> m.tryChannel(FrostOrbEntity::new)),
    Dark(m -> m.tryChannel(DarkOrbEntity::new)),
    Plasma(m -> m.tryChannel(PlasmaOrbEntity::new)),
    ;

    private final Predicate<OrbManager> channeler;

    OrbType(Predicate<OrbManager> channeler) {
        this.channeler = channeler;
    }

    public Predicate<OrbManager> getChanneler() {
        return channeler;
    }

}
