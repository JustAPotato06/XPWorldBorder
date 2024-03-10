package dev.potato.xpworldborder.tasks;

import dev.potato.xpworldborder.XPWorldBorder;
import dev.potato.xpworldborder.configurations.LevelConfig;
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

        levelConfig.getKeys(false).forEach(uuid -> {
            long timeLastLeft = levelConfig.getLong(uuid + "." + LevelConfigKeys.TIME_LAST_LEFT.KEY);
            int currentLevel = levelConfig.getInt(uuid + "." + LevelConfigKeys.LEVEL_AMOUNT.KEY);
            String username = levelConfig.getString(uuid + "." + LevelConfigKeys.USERNAME.KEY);

            if (System.currentTimeMillis() - timeLastLeft >= 86400000) {
                boolean wipeOldLevelData = plugin.getConfig().getBoolean(ConfigKeys.WIPE_OLD_LEVEL_DATA.KEY);

                if (wipeOldLevelData && currentLevel != 0) {
                    Bukkit.broadcast(Component.text("[XP World Border] ", NamedTextColor.RED)
                                .append(Component.text(username, NamedTextColor.GOLD)
                                .append(Component.text(" has been offline for more than 24 hours! Their levels have been subtracted from all world borders.", NamedTextColor.RED))
                            ));
                    levelConfig.set(uuid + "." + LevelConfigKeys.LEVEL_AMOUNT.KEY, 0);
                    LevelConfig.save();
                    worldBorderManager.updateBorders();
                }
            }
        });
    }
}