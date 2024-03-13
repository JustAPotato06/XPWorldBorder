package dev.potato.xpworldborder.listeners;

import dev.potato.xpworldborder.XPWorldBorder;
import dev.potato.xpworldborder.configurations.LevelConfig;
import dev.potato.xpworldborder.utilities.LangUtilities;
import dev.potato.xpworldborder.utilities.WorldBorderUtilities;
import dev.potato.xpworldborder.utilities.enumerations.PersistentDataContainerKeys;
import dev.potato.xpworldborder.utilities.enumerations.WorldDataKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.ConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.LevelConfigKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerXPListeners implements Listener {
    private final WorldBorderUtilities worldBorderManager = WorldBorderUtilities.getManager();
    private final XPWorldBorder plugin = XPWorldBorder.getPlugin();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (!player.hasPlayedBefore()) player.setLevel(5);

        handlePlayerOutsideBorder(player);

        LevelConfig.getConfig().set(player.getUniqueId() + "." + LevelConfigKeys.TIME_LAST_LEFT.KEY, System.currentTimeMillis());

        worldBorderManager.updateBorders();

        boolean shouldDisplayLevelsInTab = plugin.getConfig().getBoolean(ConfigKeys.SHOULD_DISPLAY_LEVELS_IN_TAB.KEY);

        if (shouldDisplayLevelsInTab) {
            worldBorderManager.givePlayerScoreboard(player);
        }
    }

    private void handlePlayerOutsideBorder(Player player) {
        if (worldBorderManager.isLocationInsideBorder(player.getLocation())) return;

        PersistentDataContainer playerData = player.getPersistentDataContainer();

        if (playerData.has(PersistentDataContainerKeys.SHOULD_KILL_ON_JOIN.KEY)) {
            boolean shouldKill = playerData.get(PersistentDataContainerKeys.SHOULD_KILL_ON_JOIN.KEY, PersistentDataType.BOOLEAN);
            if (shouldKill) {
                player.setHealth(0);
                player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.LEFT_WHILE_OUTSIDE_BORDER)));
                playerData.set(PersistentDataContainerKeys.SHOULD_KILL_ON_JOIN.KEY, PersistentDataType.BOOLEAN, false);
                return;
            }
        }

        worldBorderManager.tpPlayerToNearestBlockInBorder(player);
        player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.LEFT_AND_BORDER_SHRUNK)));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (!worldBorderManager.isLocationInsideBorder(player.getLocation())) {
            PersistentDataContainer playerData = player.getPersistentDataContainer();
            playerData.set(PersistentDataContainerKeys.SHOULD_KILL_ON_JOIN.KEY, PersistentDataType.BOOLEAN, true);
        }

        boolean updateBorderOnLeave = plugin.getConfig().getBoolean(ConfigKeys.UPDATE_BORDER_ON_LEAVE.KEY);

        if (updateBorderOnLeave) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    worldBorderManager.updateBorders();
                }
            }.runTaskLater(plugin, 10);
        } else {
            FileConfiguration levelConfig = LevelConfig.getConfig();

            ConfigurationSection playerSection = levelConfig.createSection(player.getUniqueId().toString());
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

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        boolean shouldChangeDeathMessage = plugin.getConfig().getBoolean(ConfigKeys.SHOULD_CHANGE_DEATH_MESSAGE.KEY);
        if (!shouldChangeDeathMessage) return;
        Component deathMessage = e.deathMessage().append(Component.text(". They had ").append(Component.text(e.getPlayer().getLevel(), NamedTextColor.GREEN).append(Component.text(" levels.", NamedTextColor.WHITE))));
        e.deathMessage(deathMessage);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = e.getPlayer();
                Location highestLocation = worldBorderManager.toHighestLocationNoNetherRoof(player.getLocation());
                player.teleport(highestLocation.add(0, 1, 0));
            }
        }.runTaskLater(plugin, 10);
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        World currentWorld = player.getWorld();
        PersistentDataContainer currentWorldData = currentWorld.getPersistentDataContainer();
        boolean isInitialized = currentWorldData.has(WorldDataKeys.IS_INITIALIZED.KEY) ? currentWorldData.get(WorldDataKeys.IS_INITIALIZED.KEY, PersistentDataType.BOOLEAN) : false;

        if (!isInitialized) {
            WorldBorder currentWorldBorder = currentWorld.getWorldBorder();
            WorldBorder previousWorldBorder = e.getFrom().getWorldBorder();

            currentWorldBorder.setSize(previousWorldBorder.getSize());
            currentWorldBorder.setCenter(player.getLocation());
            currentWorldData.set(WorldDataKeys.IS_INITIALIZED.KEY, PersistentDataType.BOOLEAN, true);
        }
    }
}