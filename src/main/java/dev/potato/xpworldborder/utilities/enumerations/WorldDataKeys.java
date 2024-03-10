package dev.potato.xpworldborder.utilities.enumerations;

import dev.potato.xpworldborder.XPWorldBorder;
import org.bukkit.NamespacedKey;

public enum WorldDataKeys {
    IS_INITIALIZED(new NamespacedKey(XPWorldBorder.getPlugin(), "is-initialized"));

    public final NamespacedKey KEY;

    WorldDataKeys(NamespacedKey KEY) {
        this.KEY = KEY;
    }
}