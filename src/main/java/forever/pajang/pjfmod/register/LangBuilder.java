package forever.pajang.pjfmod.register;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Arrays;

public abstract class LangBuilder {
    protected final RegisterCore registerCore;
    protected String key;
    String en = null;

    protected LangBuilder(RegisterCore registerCore) {
        this.registerCore = registerCore;
    }

    public LangBuilder en(String en) {
        this.en = en;
        return this;
    }

    public abstract MutableComponent register();

    public static class FixedKey extends LangBuilder {

        public FixedKey(RegisterCore registerCore, String key) {
            super(registerCore);
            this.key = key;
        }

        @Override
        public MutableComponent register() {
            String key;
            if (en == null) en = this.key.replace(".", " ").trim();
            registerCore.lang.put(this.key, en);
            return Component.translatable(this.key);
        }
    }

    public static class CombinedKey extends LangBuilder {
        String type = "text";
        String info = null;

        public CombinedKey(RegisterCore registerCore) {
            super(registerCore);
        }

        public LangBuilder.CombinedKey type(String type) {
            this.type = type;
            return this;
        }

        public LangBuilder.CombinedKey info(String... info) {
            this.info = "";
            Arrays.stream(info).forEach(s -> this.info += "." + s);
            return this;
        }

        private String getDefaultedInfo() {
            return ".unknown." + Integer.toHexString(hashCode());
        }

        @Override
        public MutableComponent register() {
            String key;
            if (info == null) info = getDefaultedInfo();
            if (en == null) en = info.replace(".", " ").trim();
            key = type + "." + registerCore.modid + info.replace('/', '.');
            registerCore.lang.put(key, en);
            return Component.translatable(key);
        }
    }
}
