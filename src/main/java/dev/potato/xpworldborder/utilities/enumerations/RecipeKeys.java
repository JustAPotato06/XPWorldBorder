package dev.potato.xpworldborder.utilities.enumerations;

import dev.potato.xpworldborder.XPWorldBorder;
import org.bukkit.NamespacedKey;

public enum RecipeKeys {
    COUNTDOWN_X2(new NamespacedKey(XPWorldBorder.getPlugin(), "countdown-x2")),
    COUNTDOWN_X3(new NamespacedKey(XPWorldBorder.getPlugin(), "countdown-x3")),
    COUNTDOWN_X4(new NamespacedKey(XPWorldBorder.getPlugin(), "countdown-x4"));

    public final NamespacedKey KEY;

    RecipeKeys(NamespacedKey KEY) {
        this.KEY = KEY;
    }
}