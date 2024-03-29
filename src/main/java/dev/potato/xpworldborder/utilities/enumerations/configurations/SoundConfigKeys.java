package dev.potato.xpworldborder.utilities.enumerations.configurations;

public enum SoundConfigKeys {
    NO_SOUND_INCREASE("no-sound-increase"),
    NO_SOUND_DECREASE("no-sound-decrease"),
    NO_SOUND_OUTSIDE_BORDER("no-sound-outside-border");

    public final String KEY;

    SoundConfigKeys(String KEY) {
        this.KEY = KEY;
    }
}