package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.specials.BlockingValueHandler;
import forever.pajang.minethespire.impl.ActivatableStatesAttribute;
import forever.pajang.minethespire.register.LangBuilder;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModAttributes {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final Component STATE_ACTIVATE = REG.text().type("attribute").info("activate").en("Activate: ").register();
    public static final Component STATE_INVALID_OPERATION = REG.text().type("attribute").info("invalid").en("Invalid Operation! ").register();

    public static final DeferredHolder<Attribute, Attribute> FLAGS_GROUP_0 = REG.stateSet("flags_group_0")
            .setState(State.BURNING_BLOOD.getIndex(), stateLang().info("burning_blood").en("After a perfect victory, heals 6 Health").color(0xFF7fCf).register())
            .setState(State.RING_OF_THE_SNAKE.getIndex(), stateLang().info("ring_of_the_snake").en("When entered combat, boost Movement Speed by 30%").color(0x00cf00).register())
            .setState(State.CRACKED_CORE.getIndex(), stateLang().info("cracked_core").en("When entered combat, summon a lightning charge ball").color(0x00cfff).register())
            .setState(State.LIZARD_TAIL.getIndex(), stateLang().info("lizard_tail").en("When you would die, heal to 50% of your Max Health instead").color(0xffaa00).register())
            .setState(State.AKABEKO.getIndex(), stateLang().info("akabeko").en("When entered combat, obtain Vigor 8").color(0xFFCB48).register())
            .setState(State.BAG_OF_MARBLES.getIndex(), stateLang().info("bag_of_marbles").en("When entered combat, apply Vulnerable to enemies").color(0xC85A5A).register())
            .setState(State.BLOOD_VIAL.getIndex(), stateLang().info("blood_vial").en("When entered combat, heal 2 Health").color(0xFF96A4).register())
            .setState(State.CENTENNIAL_PUZZLE.getIndex(), stateLang().info("centennial_puzzle").en("When get hurt, gain Speed II").color(0x69B95A).register())
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

    public static final DeferredHolder<Attribute, Attribute> PREPARED_BLOCKING = REG.attribute("prepared_blocking")
            .min(0.0D).max(256.0D).defaultValue(0.0D).attachToAll().register();

    public static final DeferredHolder<Attribute, Attribute> THORNS = REG.attribute("thorns")
            .min(0.0D).max(1024.0D).defaultValue(0.0D).attachToAll().register();


    private static LangBuilder.CombinedKey stateLang() {
        return REG.text().type("attribute").info("state");
    }

    public enum State {
        BURNING_BLOOD(0),
        RING_OF_THE_SNAKE(1),
        CRACKED_CORE(2),
        LIZARD_TAIL(3),
        AKABEKO(4),
        BAG_OF_MARBLES(5),
        BLOOD_VIAL(6),
        CENTENNIAL_PUZZLE(7)
        ;

        private final int index;

        State(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public static final class Impl {

        public static void applyPreparedBlocking(LivingEntity entity) {
            double value = entity.getAttributeValue(PREPARED_BLOCKING);
            if (value > 0) {
                BlockingValueHandler.add(entity, (float) value);
            }
        }

        public static void applyBagOfMarbles(LivingEntity entity, LivingEntity target) {
            if (ActivatableStatesAttribute.getBoolean(State.BAG_OF_MARBLES.getIndex(), entity.getAttributeValue(FLAGS_GROUP_0))) {
                target.addEffect(new MobEffectInstance(ModEffects.VULNERABLE, 100, 1), entity);
            }
        }

        public static void thornsDamage(LivingEntity entity, DamageSource source) {
            if (entity.level().isClientSide()) return;
            ServerLevel level = (ServerLevel) entity.level();
            Entity attacker = source.getDirectEntity();
            double thorns = entity.getAttributeValue(THORNS);
            if (thorns > 0 && attacker != null && attacker.isAlive()) {
                float thornsDamage = (float) thorns;
                attacker.hurtServer(level, entity.damageSources().thorns(entity), thornsDamage);
            }
        }

        public static void bloodVialHeal(LivingEntity entity) {
            if (ActivatableStatesAttribute.getBoolean(State.BLOOD_VIAL.getIndex(), entity.getAttributeValue(FLAGS_GROUP_0))) {
                entity.heal(2f);
            }
        }

        public static void centennialPuzzleBoostSpeed(LivingEntity entity) {
            if (ActivatableStatesAttribute.getBoolean(State.CENTENNIAL_PUZZLE.getIndex(), entity.getAttributeValue(FLAGS_GROUP_0))) {
                entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 1), entity);
            }
        }
    }

    private ModAttributes() {
    }

    public static void register() {
    }
}
