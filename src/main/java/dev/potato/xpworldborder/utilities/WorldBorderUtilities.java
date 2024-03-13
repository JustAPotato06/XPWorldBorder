package dev.potato.xpworldborder.utilities;

import dev.potato.xpworldborder.XPWorldBorder;
import dev.potato.xpworldborder.configurations.LevelConfig;
import dev.potato.xpworldborder.models.Quadrant;
import dev.potato.xpworldborder.tasks.WorldBordersUpdateTask;
import dev.potato.xpworldborder.utilities.enumerations.configurations.ConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.LevelConfigKeys;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
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
        currentUpdateTask.runTaskTimer(plugin, 0, 10);
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

    public boolean isLocationOutsideBorder(Location location) {
        World world = location.getWorld();
        WorldBorder worldBorder = world.getWorldBorder();
        Location worldBorderCenter = worldBorder.getCenter();
        double radius = (worldBorder.getSize() / 2) + 1;
        double distanceX = location.getX() - worldBorderCenter.getX();
        double distanceZ = location.getZ() - worldBorderCenter.getZ();
        return Math.abs(distanceX) > radius || Math.abs(distanceZ) > radius;
    }

    public boolean isHighestLocationSafe(Location location) {
        return getHighestLocationNoNetherRoof(location).getBlock().isSolid();
    }

    public void tpPlayerToNearestInBorderBlock(Player player) {
        Location location = player.getLocation();
        Location worldBorderCenter = player.getWorld().getWorldBorder().getCenter();
        Quadrant playerQuadrant = Quadrant.getQuadrant(location, worldBorderCenter);
        Location closestBorderLocation = location;
        for (int i = 0; i < playerQuadrant.getDiagonalDistance(); i++) {
            closestBorderLocation = getNextLocation(i, playerQuadrant, location);
            if (!isLocationOutsideBorder(closestBorderLocation)) break;
        }
        Location finalLocation = getHighestLocationNoNetherRoof(closestBorderLocation).add(0, 1, 0);
        if (isLocationOutsideBorder(finalLocation))
            finalLocation = getHighestLocationNoNetherRoof(worldBorderCenter).add(0, 1, 0);
        player.teleport(finalLocation);
        if (!isHighestLocationSafe(finalLocation)) tpPlayerToNearestSafeLocation(player);
    }

    public void tpPlayerToNearestSafeLocation(Player player) {
        Location location = player.getLocation();
        Location worldBorderCenter = player.getWorld().getWorldBorder().getCenter();
        Quadrant playerQuadrant = Quadrant.getQuadrant(location, worldBorderCenter);
        Location closestSafeLocation = location;
        for (int i = 0; i < playerQuadrant.getDiagonalDistance(); i++) {
            closestSafeLocation = getNextLocation(i, playerQuadrant, location);
            if (isHighestLocationSafe(closestSafeLocation)) break;
        }
        Location finalLocation = getHighestLocationNoNetherRoof(closestSafeLocation).add(0, 1, 0);
        if (isLocationOutsideBorder(finalLocation))
            finalLocation = getHighestLocationNoNetherRoof(worldBorderCenter).add(0, 1, 0);
        player.teleport(finalLocation);
    }

    private Location getNextLocation(int i, Quadrant playerQuadrant, Location location) {
        return switch (playerQuadrant.getQuadrantType()) {
            case POS_POS -> location.subtract(i, 0, i);
            case POS_NEG -> location.subtract(i, 0, 0).add(0, 0, i);
            case NEG_NEG -> location.add(i, 0, i);
            case NEG_POS -> location.add(i, 0, 0).subtract(0, 0, i);
            default -> location;
        };
    }

    public Location getHighestLocationNoNetherRoof(Location location) {
        if (location.toHighestLocation().getBlock().getType() != Material.BEDROCK) return location.toHighestLocation();
        Block highestBlock = location.getBlock();
        for (int i = 129; i > 0; i--) {
            Block currentBlock = new Location(location.getWorld(), location.getX(), i, location.getZ()).getBlock();
            boolean isBlockSolid = currentBlock.isSolid();
            boolean isAirAbove = currentBlock.getLocation().add(0, 1, 0).getBlock().getType() == Material.AIR && currentBlock.getLocation().add(0, 2, 0).getBlock().getType() == Material.AIR;
            boolean isInRange = currentBlock.getLocation().getY() <= 75;
            if (isBlockSolid && isAirAbove && isInRange) {
                highestBlock = currentBlock;
                break;
            }
        }
        return highestBlock.getLocation();
    }
}