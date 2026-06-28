package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.register.LangBuilder;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModAttributes {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final Component STATE_ACTIVATE = REG.text().type("attribute").info("activate").en("Activate: ").register();
    public static final Component STATE_INVALID_OPERATION = REG.text().type("attribute").info("invalid").en("Invalid Operation! ").register();

    public static final DeferredHolder<Attribute, Attribute> ACTIVATABLE_STATES = REG.stateSet("activatable_states")
            .setState(State.BURNING_BLOOD.getIndex(), stateLang().info("burning_blood").en("After a perfect victory, heals 6 Health").color(0xFF7fCf).register())
            .setState(State.RING_OF_THE_SNAKE.getIndex(), stateLang().info("ring_of_the_snake").en("When entered combat, boost Movement Speed by 30%").color(0x00cf00).register())
            .setState(State.CRACKED_CORE.getIndex(), stateLang().info("cracked_core").en("When entered combat, summon a lightning charge ball").color(0x00cfff).register())
            .setState(State.LIZARD_TAIL.getIndex(), stateLang().info("lizard_tail").en("When you would die, heal to 50% of your Max Health instead").color(0xffaa00).register())
            .setState(State.AKABEKO.getIndex(), stateLang().info("akabeko").en("When entered combat, obtain Vigor 8").color(0xFFCB48).register())
            .attachToAll().register();

    public static final DeferredHolder<Attribute, Attribute> BLOCKING_VALUE = REG.attribute("blocking_value")
            .max(256.0D).defaultValue(0.0D).attachToAll().register();

    public static final DeferredHolder<Attribute, Attribute> BLOCKING_VALUE_CHANGE_RATE = REG.attribute("blocking_value_change_rate")
            .min(-1024.0D).max(1024.0D).defaultValue(-0.5D).attachToAll().register();

    public static final DeferredHolder<Attribute, Attribute> FOCUS = REG.attribute("focus")
            .min(-1024.0D).max(1024.0D).defaultValue(0.0D).attachToAll().register();

    public static final DeferredHolder<Attribute, Attribute> MAX_ORB = REG.attribute("max_charge_ball")
            .min(0.0D).max(16.0D).defaultValue(1.0D).attachToAll().register();

    public static final DeferredHolder<Attribute, Attribute> ORB_ATTACK_RANGE = REG.attribute("orb_attack_range")
            .min(0.0D).max(64.0D).defaultValue(8.0D).attachToAll().register();

    public static final DeferredHolder<Attribute, Attribute> ORB_PASSIVE_SPEED = REG.attribute("orb_passive_speed")
            .min(0.01D).max(20.0D).defaultValue(0.5D).attachToAll().register();

    private static LangBuilder.CombinedKey stateLang() {
        return REG.text().type("attribute").info("state");
    }

    public enum State {
        BURNING_BLOOD(0),
        RING_OF_THE_SNAKE(1),
        CRACKED_CORE(2),
        LIZARD_TAIL(3),
        AKABEKO(4)
        ;

        private final int index;

        State(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    private ModAttributes() {
    }

    public static void register() {
    }
}
