package dev.potato.xpworldborder.utilities;

import dev.potato.xpworldborder.XPWorldBorder;
import dev.potato.xpworldborder.configurations.LevelConfig;
import dev.potato.xpworldborder.models.Quadrant;
import dev.potato.xpworldborder.tasks.KillCountdownTask;
import dev.potato.xpworldborder.tasks.WorldBordersUpdateTask;
import dev.potato.xpworldborder.utilities.enumerations.configurations.ConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.LevelConfigKeys;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.UUID;

public class WorldBorderUtilities {
    private static final WorldBorderUtilities manager = new WorldBorderUtilities();
    private final XPWorldBorder plugin = XPWorldBorder.getPlugin();
    private double currentTotalLevels;
    private WorldBordersUpdateTask currentUpdateTask;
    private final HashMap<Player, KillCountdownTask> countdownTasks = new HashMap<>();

    public static WorldBorderUtilities getManager() {
        return manager;
    }

    public HashMap<Player, KillCountdownTask> getCountdownTasks() {
        return countdownTasks;
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
        if (currentTotalLevels <= 1) currentTotalLevels = 2;
        if (currentUpdateTask != null) currentUpdateTask.cancel();
        currentUpdateTask = new WorldBordersUpdateTask(currentTotalLevels);
        double speedSeconds = plugin.getConfig().getDouble(ConfigKeys.BORDER_UPDATE_SPEED.KEY);
        long speedTicks = Math.round(speedSeconds * 20);
        currentUpdateTask.runTaskTimer(plugin, 0, speedTicks);
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

    public boolean isLocationInsideBorder(Location location, boolean addExtraBlock) {
        World world = location.getWorld();
        WorldBorder worldBorder = world.getWorldBorder();
        Location worldBorderCenter = worldBorder.getCenter();
        double radius = addExtraBlock ? (worldBorder.getSize() / 2) + 1 : worldBorder.getSize() / 2;
        double distanceX = location.getX() - worldBorderCenter.getX();
        double distanceZ = location.getZ() - worldBorderCenter.getZ();
        return Math.abs(distanceX) < radius && Math.abs(distanceZ) < radius;
    }

    public boolean isHighestLocationSafe(Location location) {
        Block block = toHighestLocationNoNetherRoof(location).getBlock();
        Block blockAbove = block.getLocation().add(0, 1, 0).getBlock();
        return block.isSolid() &&
                block.getType() != Material.MAGMA_BLOCK &&
                blockAbove.getType() != Material.LAVA &&
                blockAbove.getType() != Material.WATER &&
                blockAbove.getType() != Material.FIRE;
    }

    public Location toHighestLocationNoNetherRoof(Location location) {
        if (location.getWorld().getEnvironment() != World.Environment.NETHER) return location.toHighestLocation();
        for (int i = 75; i >= 0; i--) {
            location.setY(i);
            if (location.getBlock().isSolid()) break;
        }
        return location;
    }

    public void tpPlayerToNearestBlockInBorder(Player player) {
        Location location = player.getLocation();
        if (isLocationInsideBorder(location, true)) return;
        Location worldBorderCenter = player.getWorld().getWorldBorder().getCenter();
        Quadrant playerQuadrant = Quadrant.getQuadrant(location, worldBorderCenter);
        int distanceToCenter = (int) Math.round(playerQuadrant.getDiagonalDistance());
        for (int i = 0; i < distanceToCenter; i++) {
            location = switch (playerQuadrant.quadrantType()) {
                case POS_POS -> location.subtract(1, 0, 1);
                case POS_NEG -> location.subtract(1, 0, 0).add(0, 0, 1);
                case NEG_NEG -> location.add(1, 0, 1);
                case NEG_POS -> location.add(1, 0, 0).subtract(0, 0, 1);
                default -> location;
            };
            if (isLocationInsideBorder(location, true) && isHighestLocationSafe(location)) break;
        }
        Location destination = toHighestLocationNoNetherRoof(location).add(0, 1, 0);
        if (!isLocationInsideBorder(destination, true))
            destination = toHighestLocationNoNetherRoof(worldBorderCenter).add(0, 1, 0);
        player.teleport(destination);
    }
}