package dev.potato.xpworldborder.utilities.enumerations.configurations;

public enum LevelConfigKeys {
    TIME_LAST_LEFT("time-last-left"),
    LEVEL_AMOUNT("level-amount"),
    USERNAME("username"),
    TELEPORT_TO_BORDER("teleport-to-border");

    public final String KEY;

    LevelConfigKeys(String KEY) {
        this.KEY = KEY;
    }
}