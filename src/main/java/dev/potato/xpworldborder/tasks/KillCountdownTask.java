package dev.potato.xpworldborder.tasks;

import dev.potato.xpworldborder.XPWorldBorder;
import dev.potato.xpworldborder.configurations.SoundConfig;
import dev.potato.xpworldborder.utilities.WorldBorderUtilities;
import dev.potato.xpworldborder.utilities.enumerations.configurations.ConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.SoundConfigKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class KillCountdownTask extends BukkitRunnable {
    private final XPWorldBorder plugin = XPWorldBorder.getPlugin();
    private final WorldBorderUtilities worldBorderManager = WorldBorderUtilities.getManager();
    private final FileConfiguration config = plugin.getConfig();
    private final int numberOfParticles = config.getInt(ConfigKeys.NUMBER_OF_PARTICLES_ON_EXPLOSION.KEY);
    private final int initialValue;
    private int counter;
    private KillCountdownSoundTask soundTask;
    private final Player player;
    private NamedTextColor currentColor;

    public KillCountdownTask(Player player, int counter) {
        this.player = player;
        this.counter = counter;
        this.initialValue = counter;
    }

    public int getInitialValue() {
        return initialValue;
    }

    public int getCounter() {
        return counter;
    }

    public NamedTextColor getCurrentColor() {
        return currentColor;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            endTask(false);
            return;
        } else if (worldBorderManager.isLocationInsideBorder(player.getLocation(), false)) {
            endTask(false);
            return;
        } else if (counter == 0) {
            endTask(true);
            return;
        }

        currentColor = getColor();
        player.sendActionBar(
                Component.text("You have ", NamedTextColor.GRAY)
                        .append(Component.text(counter, currentColor))
                        .append(Component.text(" seconds to reach the world border!", NamedTextColor.GRAY))
        );

        if (counter == initialValue) {
            boolean playOutsideBorderSound = !SoundConfig.getConfig().getList(SoundConfigKeys.NO_SOUND_OUTSIDE_BORDER.KEY).contains(player.getName());
            if (playOutsideBorderSound) {
                soundTask = new KillCountdownSoundTask(player);
                soundTask.runTaskTimer(plugin, 0, 1);
            }
        }

        makePlayerGlow(player, currentColor);

        counter--;
    }

    private void endTask(boolean isDeath) {
        if (isDeath) {
            player.setHealth(0);
            boolean shouldExplode = config.getBoolean(ConfigKeys.PLAYERS_EXPLODE_ON_BORDER_DEATH.KEY);
            if (shouldExplode) {
                spawnExplosion(player);
                for (Entity currentEntity : player.getNearbyEntities(50, 50, 50)) {
                    if (!(currentEntity instanceof Player currentPlayer)) continue;
                    spawnExplosion(currentPlayer);
                }
            }
        } else {
            worldBorderManager.getCountdownTasks().remove(player);
        }
        soundTask.cancel();
        turnOffPlayerGlow(player);
        this.cancel();
    }

    private NamedTextColor getColor() {
        NamedTextColor color = NamedTextColor.RED;
        int colorChangePeriod = initialValue / 4;
        boolean isGreen = (initialValue - (colorChangePeriod)) <= counter;
        boolean isYellow = (initialValue - (colorChangePeriod * 2)) <= counter && counter < (initialValue - colorChangePeriod);
        boolean isOrange = (initialValue - (colorChangePeriod * 3)) <= counter && counter < (initialValue - (colorChangePeriod * 2));
        boolean isRed = (initialValue - (colorChangePeriod * 4)) <= counter && counter < (initialValue - (colorChangePeriod * 3));
        if (isGreen) color = NamedTextColor.GREEN;
        else if (isYellow) color = NamedTextColor.YELLOW;
        else if (isOrange) color = NamedTextColor.GOLD;
        else if (isRed) color = NamedTextColor.RED;
        return color;
    }

    private void spawnExplosion(Player player) {
        player.playSound(this.player.getLocation(), org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        player.spawnParticle(Particle.FIREWORKS_SPARK, this.player.getLocation(), numberOfParticles);
    }

    private void makePlayerGlow(Player player, NamedTextColor color) {
        player.setGlowing(true);
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(player.getName()) == null ? scoreboard.registerNewTeam(player.getName()) : scoreboard.getTeam(player.getName());
        team.color(color);
        team.addEntity(player);
    }

    private void turnOffPlayerGlow(Player player) {
        player.setGlowing(false);
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(player.getName()) == null ? scoreboard.registerNewTeam(player.getName()) : scoreboard.getTeam(player.getName());
        team.removeEntity(player);
    }
}