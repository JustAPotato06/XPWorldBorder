package dev.potato.xpworldborder.tasks;

import dev.potato.xpworldborder.utilities.WorldBorderUtilities;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KillCountdownSoundTask extends BukkitRunnable {
    private final WorldBorderUtilities worldBorderManager = WorldBorderUtilities.getManager();
    private final Player player;
    private final KillCountdownTask originatingTask;
    private int counter = 1;

    public KillCountdownSoundTask(Player player) {
        this.player = player;
        this.originatingTask = worldBorderManager.getCountdownTasks().get(this.player);
    }

    @Override
    public void run() {
        NamedTextColor currentColor = originatingTask.getCurrentColor();

        if (counter == 1) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        }

        boolean onGreen = currentColor == NamedTextColor.GREEN && counter == 1;
        boolean onYellow = currentColor == NamedTextColor.YELLOW && (counter == 1 || counter == 11);
        boolean onOrange = currentColor == NamedTextColor.GOLD && (counter == 1 || counter == 6 || counter == 11 || counter == 16);
        boolean onRed = currentColor == NamedTextColor.RED && !(counter == 1 || counter == 4 || counter == 6 || counter == 11 || counter == 16 || counter == 20);

        if (onGreen) {
            player.playNote(player.getLocation(), Instrument.PLING, Note.natural(0, Note.Tone.A));
        } else if (onYellow) {
            player.playNote(player.getLocation(), Instrument.PLING, Note.sharp(0, Note.Tone.A));
        } else if (onOrange) {
            player.playNote(player.getLocation(), Instrument.PLING, Note.natural(0, Note.Tone.B));
        } else if (onRed) {
            player.playNote(player.getLocation(), Instrument.PLING, Note.natural(0, Note.Tone.C));
        }

        if (counter == 20) counter = 1;
        else counter++;
    }
}