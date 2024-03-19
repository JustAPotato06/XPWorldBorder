package dev.potato.xpworldborder.utilities.enumerations;

import dev.potato.xpworldborder.XPWorldBorder;
import org.bukkit.NamespacedKey;

public enum PersistentDataContainerKeys {
    KILL_ON_JOIN(new NamespacedKey(XPWorldBorder.getPlugin(), "kill-on-join")),
    IS_WORLD_INITIALIZED(new NamespacedKey(XPWorldBorder.getPlugin(), "is-world-initialized")),
    JUST_THREW_MULTIPLIER_X2(new NamespacedKey(XPWorldBorder.getPlugin(), "just-threw-multiplier-x2")),
    JUST_THREW_MULTIPLIER_X3(new NamespacedKey(XPWorldBorder.getPlugin(), "just-threw-multiplier-x3")),
    JUST_THREW_MULTIPLIER_X4(new NamespacedKey(XPWorldBorder.getPlugin(), "just-threw-multiplier-x4"));

    public final NamespacedKey KEY;

    PersistentDataContainerKeys(NamespacedKey KEY) {
        this.KEY = KEY;
    }
}