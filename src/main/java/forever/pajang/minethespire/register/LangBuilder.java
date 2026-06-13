package forever.pajang.minethespire.register;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Arrays;

public abstract class LangBuilder {
    protected final RegisterCore registerCore;
    protected String key;
    protected boolean autoColor = true;
    protected int color = 0xffffff;
    String en = null;

    protected LangBuilder(RegisterCore registerCore) {
        this.registerCore = registerCore;
    }

    public LangBuilder en(String en) {
        this.en = en;
        return this;
    }

    public LangBuilder color(int color) {
        this.autoColor = false;
        this.color = color;
        return this;
    }

    public abstract MutableComponent register();

    public String registerAndGetKey() {
        register();
        return key;
    }

    public static class FixedKey extends LangBuilder {

        public FixedKey(RegisterCore registerCore, String key) {
            super(registerCore);
            this.key = key;
        }

        @Override
        public MutableComponent register() {
            if (en == null) en = this.key.replace(".", " ").trim();
            registerCore.lang.put(this.key, en);
            return Component.translatable(this.key).withColor(color);
        }
    }

    public static class CombinedKey extends LangBuilder {
        String type = "text";
        String info = "";

        public CombinedKey(RegisterCore registerCore) {
            super(registerCore);
        }

        public LangBuilder.CombinedKey type(String type) {
            this.type = type;
            return this;
        }

        public LangBuilder.CombinedKey info(String... info) {
            Arrays.stream(info).forEach(s -> this.info += "." + s);
            return this;
        }

        private String getDefaultedInfo() {
            return ".unknown." + Integer.toHexString(hashCode());
        }

        @Override
        public LangBuilder clone() {
            return new LangBuilder.CombinedKey(registerCore).type(type).info(info).en(en);
        }

        @Override
        public MutableComponent register() {
            if (info == null || info.isEmpty()) info = getDefaultedInfo();
            if (en == null) en = info.replace(".", " ").trim();
            key = type + "." + registerCore.modid + info.replace('/', '.');
            registerCore.lang.put(key, en);
            if (autoColor) {
                return Component.translatable(key);
            } else return Component.translatable(key).withColor(color);
        }
    }
}
