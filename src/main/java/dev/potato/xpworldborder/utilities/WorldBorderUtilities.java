package dev.potato.xpworldborder.utilities;

import dev.potato.xpworldborder.XPWorldBorder;
import dev.potato.xpworldborder.configurations.LevelConfig;
import dev.potato.xpworldborder.tasks.WorldBordersUpdateTask;
import dev.potato.xpworldborder.utilities.enumerations.configurations.ConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.LevelConfigKeys;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.UUID;

public class WorldBorderUtilities {
    private static final WorldBorderUtilities manager = new WorldBorderUtilities();
    private final XPWorldBorder plugin = XPWorldBorder.getPlugin();
    private double currentTotalLevels;
    private WorldBordersUpdateTask currentUpdateTask;

    public static WorldBorderUtilities getManager() {
        return manager;
    }

    public void updateBorders() {
        currentTotalLevels = 0;
        for (Player player : Bukkit.getOnlinePlayers()) currentTotalLevels += player.getLevel();
        boolean updateBorderOnLeave = plugin.getConfig().getBoolean(ConfigKeys.UPDATE_BORDER_ON_LEAVE.KEY);
        if (!updateBorderOnLeave) {
            FileConfiguration levelConfig = LevelConfig.getConfig();
            levelConfig.getKeys(false).forEach(uuid -> {
                Player currentPlayer = Bukkit.getPlayer(UUID.fromString(uuid));
                int currentLevel = levelConfig.getInt(uuid + "." + LevelConfigKeys.LEVEL_AMOUNT.KEY);
                if (currentPlayer == null || !currentPlayer.isOnline()) currentTotalLevels += currentLevel;
            });
        }
        if (currentTotalLevels < 1) currentTotalLevels = 1;
        if (currentUpdateTask != null) currentUpdateTask.cancel();
        currentUpdateTask = new WorldBordersUpdateTask(currentTotalLevels);
        currentUpdateTask.runTaskTimer(plugin, 0, 20);
    }

    public void givePlayerScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Objective objective = scoreboard.getObjective("LevelDisplay") == null ?
                scoreboard.registerNewObjective("LevelDisplay", Criteria.LEVEL, Component.text("Levels")) :
                scoreboard.getObjective("LevelDisplay");
        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        player.setScoreboard(scoreboard);
    }
}