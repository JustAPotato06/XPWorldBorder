package dev.potato.xpworldborder.utilities.enumerations;

import dev.potato.xpworldborder.XPWorldBorder;
import org.bukkit.NamespacedKey;

public enum PersistentDataContainerKeys {
    SHOULD_KILL_ON_JOIN(new NamespacedKey(XPWorldBorder.getPlugin(), "should-kill-on-join"));

    public final NamespacedKey KEY;

    PersistentDataContainerKeys(NamespacedKey KEY) {
        this.KEY = KEY;
    }
}