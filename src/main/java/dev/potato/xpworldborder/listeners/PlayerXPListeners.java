package dev.potato.xpworldborder.listeners;

import dev.potato.xpworldborder.XPWorldBorder;
import dev.potato.xpworldborder.configurations.LevelConfig;
import dev.potato.xpworldborder.utilities.LangUtilities;
import dev.potato.xpworldborder.utilities.WorldBorderUtilities;
import dev.potato.xpworldborder.utilities.enumerations.PersistentDataContainerKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.ConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.LevelConfigKeys;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerXPListeners implements Listener {
    private final XPWorldBorder plugin = XPWorldBorder.getPlugin();
    private final FileConfiguration config = plugin.getConfig();
    private final WorldBorderUtilities worldBorderManager = WorldBorderUtilities.getManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        int startingLevel = config.getInt(ConfigKeys.STARTING_PLAYER_LEVEL.KEY);
        if (!player.hasPlayedBefore()) player.setLevel(startingLevel);

        handlePlayerOutsideBorder(player);

        LevelConfig.getConfig().set(player.getUniqueId() + "." + LevelConfigKeys.TIME_LAST_LEFT.KEY, System.currentTimeMillis());

        worldBorderManager.updateBorders();

        boolean shouldDisplayLevelsInTab = config.getBoolean(ConfigKeys.DISPLAY_LEVELS_IN_TAB.KEY);

        if (shouldDisplayLevelsInTab) {
            worldBorderManager.givePlayerScoreboard(player);
        }
    }

    private void handlePlayerOutsideBorder(Player player) {
        if (worldBorderManager.isLocationInsideBorder(player.getLocation(), true)) return;

        boolean killPlayersOutsideBorderOnLeave = config.getBoolean(ConfigKeys.KILL_PLAYERS_OUTSIDE_BORDER_ON_LEAVE.KEY);

        if (killPlayersOutsideBorderOnLeave) {
            PersistentDataContainer playerData = player.getPersistentDataContainer();

            if (playerData.has(PersistentDataContainerKeys.KILL_ON_JOIN.KEY)) {
                boolean shouldKill = playerData.get(PersistentDataContainerKeys.KILL_ON_JOIN.KEY, PersistentDataType.BOOLEAN);

                if (shouldKill) {
                    player.setHealth(0);
                    player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.LEFT_WHILE_OUTSIDE_BORDER)));
                    playerData.set(PersistentDataContainerKeys.KILL_ON_JOIN.KEY, PersistentDataType.BOOLEAN, false);
                    return;
                }
            }
        }

        boolean teleportPlayersInsideBorder = config.getBoolean(ConfigKeys.TELEPORT_PLAYERS_INSIDE_BORDER.KEY);

        if (teleportPlayersInsideBorder) {
            boolean teleport = LevelConfig.getConfig().getBoolean(LevelConfigKeys.TELEPORT_TO_BORDER.KEY);
            if (teleport) {
                worldBorderManager.tpPlayerToNearestBlockInBorder(player);
                player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.LEFT_AND_BORDER_SHRUNK)));
            } else {
                player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.LEFT_WHILE_BORDER_DECREASING)));
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        FileConfiguration levelConfig = LevelConfig.getConfig();
        ConfigurationSection playerSection = levelConfig.createSection(player.getUniqueId().toString());

        if (!worldBorderManager.isLocationInsideBorder(player.getLocation(), true)) {
            PersistentDataContainer playerData = player.getPersistentDataContainer();
            playerData.set(PersistentDataContainerKeys.KILL_ON_JOIN.KEY, PersistentDataType.BOOLEAN, true);
        } else {
            if (worldBorderManager.getCurrentUpdateTask() != null && !worldBorderManager.getCurrentUpdateTask().isIncrease()) {
                playerSection.addDefault(LevelConfigKeys.TELEPORT_TO_BORDER.KEY, false);
            } else {
                playerSection.addDefault(LevelConfigKeys.TELEPORT_TO_BORDER.KEY, true);
            }
        }

        boolean updateBorderOnLeave = config.getBoolean(ConfigKeys.UPDATE_BORDER_ON_LEAVE.KEY);

        if (updateBorderOnLeave) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    worldBorderManager.updateBorders();
                }
            }.runTaskLater(plugin, 10);
        } else {
            playerSection.addDefault(LevelConfigKeys.TIME_LAST_LEFT.KEY, System.currentTimeMillis());
            playerSection.addDefault(LevelConfigKeys.LEVEL_AMOUNT.KEY, player.getLevel());
            playerSection.addDefault(LevelConfigKeys.USERNAME.KEY, player.getName());

            LevelConfig.save();
        }
    }

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent e) {
        worldBorderManager.updateBorders();
    }
}