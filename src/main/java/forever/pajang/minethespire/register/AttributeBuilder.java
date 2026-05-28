package forever.pajang.minethespire.register;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class AttributeBuilder extends RegisterCore.Builder {
    private double minValue = 0.0D;
    private double maxValue = 1024.0D;
    private double defaultValue = 0.0D;
    private String en = null;

    AttributeBuilder(RegisterCore registerCore, String name) {
        super(registerCore, name);
    }

    public AttributeBuilder min(double minValue) {
        this.minValue = minValue;
        return this;
    }

    public AttributeBuilder max(double maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public AttributeBuilder defaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public AttributeBuilder en(String en) {
        this.en = en;
        return this;
    }

    public DeferredHolder<Attribute, Attribute> register() {
        DeferredHolder<Attribute, Attribute> attribute = registerCore.attributes.register(name,
                () -> new RangedAttribute("attribute." + registerCore.modid + "." + name, defaultValue, minValue, maxValue).setSyncable(true));
        registerCore.livingEntityAttributes.put(attribute, defaultValue);
        if (registerCore.runningDataGen()) {
            String display = this.en == null ? RegisterCore.getDisplayTitle(this.name) : this.en;
            registerCore.deferredLang.put(() -> attribute.get().getDescriptionId(), display);
        }
        return attribute;
    }
}
