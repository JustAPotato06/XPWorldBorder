package dev.potato.xpworldborder.utilities.enumerations.configurations;

public enum LangConfigKeys {
    PLUGIN_PREFIX("plugin-prefix"),
    LEFT_WHILE_OUTSIDE_BORDER("left-while-outside-border"),
    LEFT_AND_BORDER_SHRUNK("left-and-border-shrunk"),
    LEFT_WHILE_BORDER_DECREASING("left-while-border-decreasing"),
    ALL_SOUNDS_ENABLED("all-sounds-enabled"),
    ALL_SOUNDS_DISABLED("all-sounds-disabled"),
    INCREASE_SOUNDS_ENABLED("increase-sounds-enabled"),
    INCREASE_SOUNDS_DISABLED("increase-sounds-disabled"),
    DECREASE_SOUNDS_ENABLED("decrease-sounds-enabled"),
    DECREASE_SOUNDS_DISABLED("decrease-sounds-disabled"),
    OUTSIDE_BORDER_SOUNDS_ENABLED("outside-border-sounds-enabled"),
    OUTSIDE_BORDER_SOUNDS_DISABLED("outside-border-sounds-disabled"),
    USED_X2_MULTIPLIER("used-x2-multiplier"),
    USED_X3_MULTIPLIER("used-x3-multiplier"),
    USED_X4_MULTIPLIER("used-x4-multiplier");

    public final String KEY;

    LangConfigKeys(String KEY) {
        this.KEY = KEY;
    }
}