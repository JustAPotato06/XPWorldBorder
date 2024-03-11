package dev.potato.xpworldborder.utilities.enumerations.configurations;

public enum ConfigKeys {
    UPDATE_BORDER_ON_LEAVE("update-border-on-leave"),
    WIPE_OLD_LEVEL_DATA("wipe-old-level-data"),
    TIME_BEFORE_WIPE("time-before-wipe"),
    SHOULD_CHANGE_DEATH_MESSAGE("should-change-death-message"),
    SHOULD_DISPLAY_LEVELS_IN_TAB("should-display-levels-in-tab"),
    SHOULD_NOTIFY_PLAYERS_ON_WIPE("should-notify-players-on-wipe");

    public final String KEY;

    ConfigKeys(String KEY) {
        this.KEY = KEY;
    }
}