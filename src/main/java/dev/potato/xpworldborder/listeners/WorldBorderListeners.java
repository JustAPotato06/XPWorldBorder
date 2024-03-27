package dev.potato.xpworldborder.listeners;

import dev.potato.xpworldborder.XPWorldBorder;
import dev.potato.xpworldborder.tasks.KillCountdownTask;
import dev.potato.xpworldborder.utilities.ItemUtilities;
import dev.potato.xpworldborder.utilities.LangUtilities;
import dev.potato.xpworldborder.utilities.WorldBorderUtilities;
import dev.potato.xpworldborder.utilities.enumerations.PersistentDataContainerKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.ConfigKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldBorderListeners implements Listener {
    private final XPWorldBorder plugin = XPWorldBorder.getPlugin();
    private final FileConfiguration config = plugin.getConfig();
    private final WorldBorderUtilities worldBorderManager = WorldBorderUtilities.getManager();
    private final ItemStack x2Item = ItemUtilities.getCountdownX2Item();
    private final ItemStack x3Item = ItemUtilities.getCountdownX3Item();
    private final ItemStack x4Item = ItemUtilities.getCountdownX4Item();

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
    public void onPlayerInteract(PlayerInteractEvent e) {
        Action action = e.getAction();

        if (action != Action.RIGHT_CLICK_AIR) return;

        if (!worldBorderManager.isLocationInsideBorder(e.getPlayer().getLocation(), true)) return;

        Player player = e.getPlayer();
        PersistentDataContainer playerData = player.getPersistentDataContainer();
        ItemStack item = player.getInventory().getItemInMainHand();

        x2Item.setAmount(item.getAmount());
        x3Item.setAmount(item.getAmount());
        x4Item.setAmount(item.getAmount());

        if (item.equals(x2Item)) {
            playerData.set(PersistentDataContainerKeys.JUST_THREW_MULTIPLIER_X2.KEY, PersistentDataType.BOOLEAN, true);
        } else if (item.equals(x3Item)) {
            playerData.set(PersistentDataContainerKeys.JUST_THREW_MULTIPLIER_X3.KEY, PersistentDataType.BOOLEAN, true);
        } else if (item.equals(x4Item)) {
            playerData.set(PersistentDataContainerKeys.JUST_THREW_MULTIPLIER_X4.KEY, PersistentDataType.BOOLEAN, true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        handleKillCountdown(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent e) {
        ProjectileSource source = e.getEntity().getShooter();

        if (!(source instanceof Player player)) return;

        handleKillCountdown(player);
    }

    private void handleKillCountdown(Player player) {
        if (worldBorderManager.getCountdownTasks().containsKey(player)) return;

        boolean isInBorder = worldBorderManager.isLocationInsideBorder(player.getLocation(), true);

        if (!isInBorder) {
            int counter = config.getInt(ConfigKeys.OUTSIDE_BORDER_COUNTDOWN_TIME.KEY);

            if (counter == 0) return;

            int personalizedCounter = handleMultipliers(player, counter);
            KillCountdownTask countdownTask = new KillCountdownTask(player, personalizedCounter);
            worldBorderManager.getCountdownTasks().put(player, countdownTask);
            countdownTask.runTaskTimer(plugin, 0, 20);
        }
    }

    private int handleMultipliers(Player player, int counter) {
        int personalizedCounter;
        PersistentDataContainer playerData = player.getPersistentDataContainer();
        boolean threwX2 = playerData.has(PersistentDataContainerKeys.JUST_THREW_MULTIPLIER_X2.KEY) && playerData.get(PersistentDataContainerKeys.JUST_THREW_MULTIPLIER_X2.KEY, PersistentDataType.BOOLEAN);
        boolean threwX3 = playerData.has(PersistentDataContainerKeys.JUST_THREW_MULTIPLIER_X3.KEY) && playerData.get(PersistentDataContainerKeys.JUST_THREW_MULTIPLIER_X3.KEY, PersistentDataType.BOOLEAN);
        boolean threwX4 = playerData.has(PersistentDataContainerKeys.JUST_THREW_MULTIPLIER_X4.KEY) && playerData.get(PersistentDataContainerKeys.JUST_THREW_MULTIPLIER_X4.KEY, PersistentDataType.BOOLEAN);

        if (threwX2 || threwX3 || threwX4) player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 100);

        if (threwX4) {
            personalizedCounter = counter * 4;
            playerData.set(PersistentDataContainerKeys.JUST_THREW_MULTIPLIER_X4.KEY, PersistentDataType.BOOLEAN, false);
            showX4Title(player, personalizedCounter);
            return personalizedCounter;
        } else if (threwX3) {
            personalizedCounter = counter * 3;
            playerData.set(PersistentDataContainerKeys.JUST_THREW_MULTIPLIER_X3.KEY, PersistentDataType.BOOLEAN, false);
            showX3Title(player, personalizedCounter);
            return personalizedCounter;
        } else if (threwX2) {
            personalizedCounter = counter * 2;
            playerData.set(PersistentDataContainerKeys.JUST_THREW_MULTIPLIER_X2.KEY, PersistentDataType.BOOLEAN, false);
            showX2Title(player, personalizedCounter);
            return personalizedCounter;
        }

        boolean hasX2Multiplier = false;
        boolean hasX3Multiplier = false;
        boolean hasX4Multiplier = false;

        ItemStack item = player.getInventory().getItemInMainHand();

        x2Item.setAmount(item.getAmount());
        x3Item.setAmount(item.getAmount());
        x4Item.setAmount(item.getAmount());

        if (item.equals(x2Item)) hasX2Multiplier = true;
        if (item.equals(x3Item)) hasX3Multiplier = true;
        if (item.equals(x4Item)) hasX4Multiplier = true;

        if (hasX2Multiplier || hasX3Multiplier || hasX4Multiplier) {
            player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 100);
            item.setAmount(item.getAmount() - 1);
        }

        if (hasX4Multiplier) {
            personalizedCounter = counter * 4;
            showX4Title(player, personalizedCounter);
        } else if (hasX3Multiplier) {
            personalizedCounter = counter * 3;
            showX3Title(player, personalizedCounter);
        } else if (hasX2Multiplier) {
            personalizedCounter = counter * 2;
            showX2Title(player, personalizedCounter);
        } else {
            personalizedCounter = counter;
        }

        return personalizedCounter;
    }

    private void showX4Title(Player player, int personalizedCounter) {
        Title title = Title.title(
                LangUtilities.USED_X4_MULTIPLIER,
                Component.text("You now have ", NamedTextColor.WHITE)
                        .append(Component.text(personalizedCounter, NamedTextColor.GOLD))
                        .append(Component.text(" seconds to live!", NamedTextColor.WHITE))
        );
        player.showTitle(title);
    }

    private void showX3Title(Player player, int personalizedCounter) {
        Title title = Title.title(
                LangUtilities.USED_X3_MULTIPLIER,
                Component.text("You now have ", NamedTextColor.WHITE)
                        .append(Component.text(personalizedCounter, NamedTextColor.AQUA))
                        .append(Component.text(" seconds to live!", NamedTextColor.WHITE))
        );
        player.showTitle(title);
    }

    private void showX2Title(Player player, int personalizedCounter) {
        Title title = Title.title(
                LangUtilities.USED_X2_MULTIPLIER,
                Component.text("You now have ", NamedTextColor.WHITE)
                        .append(Component.text(personalizedCounter, NamedTextColor.GREEN))
                        .append(Component.text(" seconds to live!", NamedTextColor.WHITE))
        );
        player.showTitle(title);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        int countdownTime = config.getInt(ConfigKeys.OUTSIDE_BORDER_COUNTDOWN_TIME.KEY);
        if (countdownTime == 0) return;
        if (worldBorderManager.isLocationInsideBorder(player.getLocation(), true)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();

        if (!worldBorderManager.isLocationInsideBorder(player.getLocation(), true) && e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            e.setCancelled(true);
        }
    }
}