package dev.potato.xpworldborder.utilities.enumerations.configurations;

public enum LangConfigKeys {
    PLUGIN_PREFIX("plugin-prefix"),
    LEFT_WHILE_OUTSIDE_BORDER("left-while-outside-border"),
    LEFT_AND_BORDER_SHRUNK("left-and-border-shrunk"),
    ALL_SOUNDS_ENABLED("all-sounds-enabled"),
    ALL_SOUNDS_DISABLED("all-sounds-disabled"),
    INCREASE_SOUNDS_ENABLED("increase-sounds-enabled"),
    INCREASE_SOUNDS_DISABLED("increase-sounds-disabled"),
    DECREASE_SOUNDS_ENABLED("decrease-sounds-enabled"),
    DECREASE_SOUNDS_DISABLED("decrease-sounds-disabled"),
    OUTSIDE_BORDER_SOUNDS_ENABLED("outside-border-sounds-enabled"),
    OUTSIDE_BORDER_SOUNDS_DISABLED("outside-border-sounds-disabled");

    public final String KEY;

    LangConfigKeys(String KEY) {
        this.KEY = KEY;
    }
}