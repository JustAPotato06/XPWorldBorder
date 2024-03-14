package dev.potato.xpworldborder.tasks;

import dev.potato.xpworldborder.XPWorldBorder;
import dev.potato.xpworldborder.configurations.SoundConfig;
import dev.potato.xpworldborder.utilities.enumerations.configurations.ConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.SoundConfigKeys;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class WorldBordersUpdateTask extends BukkitRunnable {
    private final XPWorldBorder plugin = XPWorldBorder.getPlugin();
    private final double currentTotalLevels;

    public WorldBordersUpdateTask(double currentTotalLevels) {
        this.currentTotalLevels = currentTotalLevels;
    }

    @Override
    public void run() {
        boolean isIncrease = true;

        for (World world : Bukkit.getWorlds()) {
            WorldBorder worldBorder = world.getWorldBorder();
            double currentSize = worldBorder.getSize();

            if (currentTotalLevels == Math.round(currentSize)) {
                this.cancel();
                return;
            }

            double speedSeconds = plugin.getConfig().getDouble(ConfigKeys.BORDER_UPDATE_SPEED.KEY);
            long speedMilliseconds = Math.round(speedSeconds * 1000);
            isIncrease = currentTotalLevels > currentSize;

            if (speedMilliseconds == 0) {
                playSound(isIncrease);
                worldBorder.setSize(currentTotalLevels);
                continue;
            }

            if (isIncrease) {
                worldBorder.setSize(currentSize + 1, TimeUnit.MILLISECONDS, speedMilliseconds);
            } else {
                worldBorder.setSize(currentSize - 1, TimeUnit.MILLISECONDS, speedMilliseconds);
            }
        }

        playSound(isIncrease);
    }

    private void playSound(boolean isIncrease) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isIncrease) {
                boolean shouldPlayIncrease = !SoundConfig.getConfig().getList(SoundConfigKeys.NO_SOUND_INCREASE.KEY).contains(player.getName());
                if (shouldPlayIncrease)
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
            } else {
                boolean shouldPlayDecrease = !SoundConfig.getConfig().getList(SoundConfigKeys.NO_SOUND_DECREASE.KEY).contains(player.getName());
                if (shouldPlayDecrease)
                    player.playNote(player.getLocation(), Instrument.BASS_GUITAR, Note.natural(1, Note.Tone.C));
            }
        }
    }
}