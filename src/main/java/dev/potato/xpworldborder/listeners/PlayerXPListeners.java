package dev.potato.xpworldborder.listeners;

import dev.potato.xpworldborder.XPWorldBorder;
import dev.potato.xpworldborder.configurations.LevelConfig;
import dev.potato.xpworldborder.tasks.KillCountdownTask;
import dev.potato.xpworldborder.utilities.LangUtilities;
import dev.potato.xpworldborder.utilities.WorldBorderUtilities;
import dev.potato.xpworldborder.utilities.enumerations.PersistentDataContainerKeys;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
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
            worldBorderManager.tpPlayerToNearestBlockInBorder(player);
            player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.LEFT_AND_BORDER_SHRUNK)));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (!worldBorderManager.isLocationInsideBorder(player.getLocation(), true)) {
            PersistentDataContainer playerData = player.getPersistentDataContainer();
            playerData.set(PersistentDataContainerKeys.KILL_ON_JOIN.KEY, PersistentDataType.BOOLEAN, true);
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
        boolean shouldChangeDeathMessage = config.getBoolean(ConfigKeys.CHANGE_DEATH_MESSAGE.KEY);
        if (!shouldChangeDeathMessage) return;
        Player player = e.getPlayer();
        Component deathMessage = e.deathMessage();
        if (worldBorderManager.getCountdownTasks().containsKey(player)) {
            boolean shouldExplode = config.getBoolean(ConfigKeys.PLAYERS_EXPLODE_ON_BORDER_DEATH.KEY);
            if (shouldExplode) {
                deathMessage = Component.text(player.getName() + " blew up after refusing to stay within the confines of this world");
            }
            worldBorderManager.getCountdownTasks().remove(player);
        }
        deathMessage = deathMessage.append(Component.text(". They had ").append(Component.text(e.getPlayer().getLevel(), NamedTextColor.GREEN).append(Component.text(" levels.", NamedTextColor.WHITE))));
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
        boolean isInitialized = currentWorldData.has(PersistentDataContainerKeys.IS_WORLD_INITIALIZED.KEY) ? currentWorldData.get(PersistentDataContainerKeys.IS_WORLD_INITIALIZED.KEY, PersistentDataType.BOOLEAN) : false;

        if (!isInitialized) {
            WorldBorder currentWorldBorder = currentWorld.getWorldBorder();
            WorldBorder previousWorldBorder = e.getFrom().getWorldBorder();

            currentWorldBorder.setSize(previousWorldBorder.getSize());
            currentWorldBorder.setCenter(player.getLocation());
            currentWorldData.set(PersistentDataContainerKeys.IS_WORLD_INITIALIZED.KEY, PersistentDataType.BOOLEAN, true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (worldBorderManager.getCountdownTasks().containsKey(player)) return;
        boolean isInBorder = worldBorderManager.isLocationInsideBorder(player.getLocation(), true);
        if (!isInBorder) {
            int counter = config.getInt(ConfigKeys.OUTSIDE_BORDER_COUNTDOWN_TIME.KEY);
            if (counter == 0) return;
            KillCountdownTask countdownTask = new KillCountdownTask(player);
            worldBorderManager.getCountdownTasks().put(player, countdownTask);
            countdownTask.runTaskTimer(plugin, 0, 20);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player player)) return;
        int countdownTime = config.getInt(ConfigKeys.OUTSIDE_BORDER_COUNTDOWN_TIME.KEY);
        if(countdownTime == 0) return;
        if(worldBorderManager.isLocationInsideBorder(player.getLocation(), true)) return;
        e.setCancelled(true);
    }
}