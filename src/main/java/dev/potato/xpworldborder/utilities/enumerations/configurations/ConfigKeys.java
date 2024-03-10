package dev.potato.xpworldborder.utilities.enumerations.configurations;

public enum ConfigKeys {
    UPDATE_BORDER_ON_LEAVE("update-border-on-leave"),
    WIPE_OLD_LEVEL_DATA("wipe-old-level-data");

    public final String KEY;

    ConfigKeys(String KEY) {
        this.KEY = KEY;
    }
}