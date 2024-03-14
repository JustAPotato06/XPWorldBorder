package dev.potato.xpworldborder.tasks;

import dev.potato.xpworldborder.XPWorldBorder;
import dev.potato.xpworldborder.configurations.SoundConfig;
import dev.potato.xpworldborder.utilities.WorldBorderUtilities;
import dev.potato.xpworldborder.utilities.enumerations.configurations.ConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.SoundConfigKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KillCountdownTask extends BukkitRunnable {
    private final XPWorldBorder plugin = XPWorldBorder.getPlugin();
    private final WorldBorderUtilities worldBorderManager = WorldBorderUtilities.getManager();
    private final FileConfiguration config = plugin.getConfig();
    private final int initialValue = plugin.getConfig().getInt(ConfigKeys.OUTSIDE_BORDER_COUNTDOWN_TIME.KEY);
    private final int numberOfParticles = config.getInt(ConfigKeys.NUMBER_OF_PARTICLES_ON_EXPLOSION.KEY);
    private int counter = plugin.getConfig().getInt(ConfigKeys.OUTSIDE_BORDER_COUNTDOWN_TIME.KEY);
    private final Player player;

    public KillCountdownTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            endTask(false);
            return;
        } else if (worldBorderManager.isLocationInsideBorder(player.getLocation())) {
            endTask(false);
            return;
        } else if (counter == 0) {
            endTask(true);
            return;
        }

        NamedTextColor color = getColor();
        player.sendActionBar(
                Component.text("You have ", NamedTextColor.GRAY)
                        .append(Component.text(counter, color))
                        .append(Component.text(" seconds to reach the world border!", NamedTextColor.GRAY))
        );

        sendTickSound(player);
        for (Entity currentEntity : player.getNearbyEntities(50, 50, 50)) {
            if (!(currentEntity instanceof Player currentPlayer)) continue;
            sendTickSound(currentPlayer);
        }

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

    private void sendTickSound(Player player) {
        boolean playTickSound = !SoundConfig.getConfig().getList(SoundConfigKeys.NO_SOUND_OUTSIDE_BORDER.KEY).contains(player.getName());
        if (!playTickSound) return;
        Player playerOutsideBorder = this.player;
        player.playSound(this.player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        player.playNote(playerOutsideBorder.getLocation(), Instrument.BASS_DRUM, Note.natural(0, Note.Tone.C));
        new BukkitRunnable() {
            @Override
            public void run() {
                player.playNote(playerOutsideBorder.getLocation(), Instrument.BASS_DRUM, Note.natural(0, Note.Tone.C));
            }
        }.runTaskLater(plugin, 5);
    }
}