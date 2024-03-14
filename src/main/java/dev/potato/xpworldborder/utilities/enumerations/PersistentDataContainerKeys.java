package dev.potato.xpworldborder.utilities.enumerations;

import dev.potato.xpworldborder.XPWorldBorder;
import org.bukkit.NamespacedKey;

public enum PersistentDataContainerKeys {
    KILL_ON_JOIN(new NamespacedKey(XPWorldBorder.getPlugin(), "kill-on-join")),
    IS_WORLD_INITIALIZED(new NamespacedKey(XPWorldBorder.getPlugin(), "is-world-initialized"));

    public final NamespacedKey KEY;

    PersistentDataContainerKeys(NamespacedKey KEY) {
        this.KEY = KEY;
    }
}