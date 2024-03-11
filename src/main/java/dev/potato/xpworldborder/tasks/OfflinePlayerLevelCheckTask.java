package dev.potato.xpworldborder.tasks;

import dev.potato.xpworldborder.XPWorldBorder;
import dev.potato.xpworldborder.configurations.LevelConfig;
import dev.potato.xpworldborder.utilities.LangUtilities;
import dev.potato.xpworldborder.utilities.WorldBorderUtilities;
import dev.potato.xpworldborder.utilities.enumerations.configurations.ConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.LevelConfigKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class OfflinePlayerLevelCheckTask extends BukkitRunnable {
    private final XPWorldBorder plugin = XPWorldBorder.getPlugin();
    private final WorldBorderUtilities worldBorderManager = WorldBorderUtilities.getManager();

    @Override
    public void run() {
        FileConfiguration levelConfig = LevelConfig.getConfig();
        FileConfiguration config = plugin.getConfig();

        levelConfig.getKeys(false).forEach(uuid -> {
            long timeLastLeft = levelConfig.getLong(uuid + "." + LevelConfigKeys.TIME_LAST_LEFT.KEY);
            int currentLevel = levelConfig.getInt(uuid + "." + LevelConfigKeys.LEVEL_AMOUNT.KEY);
            String username = levelConfig.getString(uuid + "." + LevelConfigKeys.USERNAME.KEY);

            long secondsBeforeWipe = config.getLong(ConfigKeys.TIME_BEFORE_WIPE.KEY);
            long millisecondsBeforeWipe = secondsBeforeWipe * 1000;
            if (System.currentTimeMillis() - timeLastLeft >= millisecondsBeforeWipe) {
                boolean wipeOldLevelData = plugin.getConfig().getBoolean(ConfigKeys.WIPE_OLD_LEVEL_DATA.KEY);

                if (wipeOldLevelData && currentLevel != 0) {
                    boolean shouldNotifyPlayersOnWipe = config.getBoolean(ConfigKeys.SHOULD_NOTIFY_PLAYERS_ON_WIPE.KEY);
                    if (shouldNotifyPlayersOnWipe) {
                        String timeDisplay = getTimeDisplay(secondsBeforeWipe);
                        Bukkit.broadcast(LangUtilities.PLUGIN_PREFIX
                                .append(Component.text(username, NamedTextColor.GOLD)
                                        .append(Component.text(" has been offline for more than ", NamedTextColor.RED)
                                                .append(Component.text(timeDisplay, NamedTextColor.GOLD))
                                                .append(Component.text(" Their levels have been subtracted from all world borders.", NamedTextColor.RED)))
                                ));
                    }
                    levelConfig.set(uuid + "." + LevelConfigKeys.LEVEL_AMOUNT.KEY, 0);
                    LevelConfig.save();
                    worldBorderManager.updateBorders();
                }
            }
        });
    }

    private String getTimeDisplay(Long seconds) {
        String timeDisplay;
        if (seconds < 60) {
            timeDisplay = seconds + " second(s)!";
        } else if (seconds < 3600) {
            timeDisplay = (seconds / 60) + " minute(s)!";
        } else if (seconds < 86400) {
            timeDisplay = (seconds / 3600) + " hour(s)!";
        } else {
            timeDisplay = (seconds / 86400) + " day(s)!";
        }
        return timeDisplay;
    }
}