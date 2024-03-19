package dev.potato.xpworldborder.utilities.enumerations.configurations;

public enum ConfigKeys {
    STARTING_PLAYER_LEVEL("starting-player-level"),
    BORDER_UPDATE_SPEED("border-update-speed"),
    OUTSIDE_BORDER_COUNTDOWN_TIME("outside-border-countdown-time"),
    PLAYERS_EXPLODE_ON_BORDER_DEATH("players-explode-on-border-death"),
    NUMBER_OF_PARTICLES_ON_EXPLOSION("number-of-particles-on-explosion"),
    ENABLE_MULTIPLIER_ITEMS("enable-multiplier-items"),
    TELEPORT_PLAYERS_INSIDE_BORDER("teleport-players-inside-border"),
    KILL_PLAYERS_OUTSIDE_BORDER_ON_LEAVE("kill-players-outside-border-on-leave"),
    UPDATE_BORDER_ON_LEAVE("update-border-on-leave"),
    WIPE_OLD_LEVEL_DATA("wipe-old-level-data"),
    TIME_BEFORE_WIPE("time-before-wipe"),
    NOTIFY_PLAYERS_ON_WIPE("notify-players-on-wipe"),
    CHANGE_DEATH_MESSAGE("change-death-message"),
    DISPLAY_LEVELS_IN_TAB("display-levels-in-tab");

    public final String KEY;

    ConfigKeys(String KEY) {
        this.KEY = KEY;
    }
}