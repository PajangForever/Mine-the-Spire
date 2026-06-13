package forever.pajang.minethespire.register;

import forever.pajang.minethespire.impl.ActivatableStatesAttribute;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Predicate;

public abstract class AttributeBuilder extends RegisterCore.Builder {
    protected String en = null;
    protected Predicate<EntityType<? extends LivingEntity>> entities = _ -> false;

    AttributeBuilder(RegisterCore registerCore, String name) {
        super(registerCore, name);
    }

    public AttributeBuilder en(String en) {
        this.en = en;
        return this;
    }

    public AttributeBuilder attachTo(Predicate<EntityType<? extends LivingEntity>> entities) {
        this.entities = this.entities.or(entities);
        return this;
    }

    public AttributeBuilder attachToAll() {
        this.entities = _ -> true;
        return this;
    }

    public abstract DeferredHolder<Attribute, Attribute> register();

    public static class Ranged extends AttributeBuilder {
        private double minValue = 0.0D;
        private double maxValue = 1024.0D;
        private double defaultValue = 0.0D;

        Ranged(RegisterCore registerCore, String name) {
            super(registerCore, name);
        }

        public Ranged min(double minValue) {
            this.minValue = minValue;
            return this;
        }

        public Ranged max(double maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Ranged defaultValue(double defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public DeferredHolder<Attribute, Attribute> register () {
            String key = "attribute." + registerCore.modid + "." + name;
            DeferredHolder<Attribute, Attribute> attribute = registerCore.attributes.register(name,
                    () -> new RangedAttribute(key, defaultValue, minValue, maxValue).setSyncable(true));
            registerCore.livingAttributes.put(attribute, entities);
            if (registerCore.runningDataGen()) {
                String display = this.en == null ? RegisterCore.getDisplayTitle(this.name) : this.en;
                registerCore.text(key).en(display).register();
            }
            return attribute;
        }

    }

    public static class StateSet extends AttributeBuilder {
        private final MutableComponent[] descriptions = new MutableComponent[8];

        public StateSet(RegisterCore registerCore, String name) {
            super(registerCore, name);
        }

        public StateSet setState(int index, MutableComponent description) {
            if (index < 0  || index >= 8) {
                throw new IndexOutOfBoundsException("Index %d out of range [0, 7]".formatted(index));
            }
            this.descriptions[index] = description;
            return this;
        }

        @Override
        public DeferredHolder<Attribute, Attribute> register() {
            String key = "attribute." + registerCore.modid + "." + name;
            DeferredHolder<Attribute, Attribute> attribute = registerCore.attributes.register(name,
                    () -> new ActivatableStatesAttribute(key, descriptions).setSyncable(true));
            registerCore.livingAttributes.put(attribute, entities);
            if (registerCore.runningDataGen()) {
                String display = this.en == null ? RegisterCore.getDisplayTitle(this.name) : this.en;
                registerCore.text(key).en(display).register();
            }
            return attribute;
        }

    }
}
