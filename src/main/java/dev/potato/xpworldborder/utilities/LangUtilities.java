package dev.potato.xpworldborder.utilities;

import dev.potato.xpworldborder.configurations.LangConfig;
import dev.potato.xpworldborder.utilities.enumerations.configurations.LangConfigKeys;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;

public class LangUtilities {
    private static final FileConfiguration langConfig = LangConfig.getConfig();
    public static final TextComponent PLUGIN_PREFIX = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.PLUGIN_PREFIX.KEY));
    public static final TextComponent LEFT_WHILE_OUTSIDE_BORDER = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.LEFT_WHILE_OUTSIDE_BORDER.KEY));
    public static final TextComponent LEFT_AND_BORDER_SHRUNK = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.LEFT_AND_BORDER_SHRUNK.KEY));
    public static final TextComponent LEFT_WHILE_BORDER_DECREASING = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.LEFT_WHILE_BORDER_DECREASING.KEY));
    public static final TextComponent ALL_SOUNDS_ENABLED = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.ALL_SOUNDS_ENABLED.KEY));
    public static final TextComponent ALL_SOUNDS_DISABLED = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.ALL_SOUNDS_DISABLED.KEY));
    public static final TextComponent INCREASE_SOUNDS_ENABLED = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.INCREASE_SOUNDS_ENABLED.KEY));
    public static final TextComponent INCREASE_SOUNDS_DISABLED = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.INCREASE_SOUNDS_DISABLED.KEY));
    public static final TextComponent DECREASE_SOUNDS_ENABLED = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.DECREASE_SOUNDS_ENABLED.KEY));
    public static final TextComponent DECREASE_SOUNDS_DISABLED = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.DECREASE_SOUNDS_DISABLED.KEY));
    public static final TextComponent OUTSIDE_BORDER_SOUNDS_ENABLED = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.OUTSIDE_BORDER_SOUNDS_ENABLED.KEY));
    public static final TextComponent OUTSIDE_BORDER_SOUNDS_DISABLED = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.OUTSIDE_BORDER_SOUNDS_DISABLED.KEY));
    public static final TextComponent USED_X2_MULTIPLIER = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.USED_X2_MULTIPLIER.KEY));
    public static final TextComponent USED_X3_MULTIPLIER = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.USED_X3_MULTIPLIER.KEY));
    public static final TextComponent USED_X4_MULTIPLIER = LegacyComponentSerializer.legacy('&').deserialize(langConfig.getString(LangConfigKeys.USED_X4_MULTIPLIER.KEY));
}