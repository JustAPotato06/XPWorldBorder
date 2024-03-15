package dev.potato.xpworldborder.tasks;

import dev.potato.xpworldborder.utilities.WorldBorderUtilities;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Entity;
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

        boolean shouldOthersHear = true;
        for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
            if (currentPlayer.equals(player)) continue;
            if (!worldBorderManager.getCountdownTasks().containsKey(currentPlayer)) continue;
            if (worldBorderManager.getCountdownTasks().get(currentPlayer).getCounter() <= originatingTask.getCounter())
                shouldOthersHear = false;
        }

        if (counter == 1) {
            Instrument sticks = Instrument.STICKS;
            Note note = Note.natural(0, Note.Tone.C);
            player.playNote(player.getLocation(), sticks, note);
            playNoteForAllPlayers(shouldOthersHear, sticks, note);
        }

        boolean onGreen = currentColor == NamedTextColor.GREEN && counter == 1;
        boolean onYellow = currentColor == NamedTextColor.YELLOW && (counter == 1 || counter == 11);
        boolean onOrange = currentColor == NamedTextColor.GOLD && (counter == 1 || counter == 6 || counter == 11 || counter == 16);
        boolean onRed = currentColor == NamedTextColor.RED && !(counter == 1 || counter == 4 || counter == 7 || counter == 10 || counter == 13 || counter == 16 || counter == 19);
        Instrument pling = Instrument.PLING;

        if (onGreen) {
            Note note = Note.natural(0, Note.Tone.A);
            player.playNote(player.getLocation(), pling, note);
            playNoteForAllPlayers(shouldOthersHear, pling, note);
        } else if (onYellow) {
            Note note = Note.sharp(0, Note.Tone.A);
            player.playNote(player.getLocation(), pling, note);
            playNoteForAllPlayers(shouldOthersHear, pling, note);
        } else if (onOrange) {
            Note note = Note.natural(0, Note.Tone.B);
            player.playNote(player.getLocation(), pling, note);
            playNoteForAllPlayers(shouldOthersHear, pling, note);
        } else if (onRed) {
            Note note = Note.natural(0, Note.Tone.C);
            player.playNote(player.getLocation(), pling, note);
            playNoteForAllPlayers(shouldOthersHear, pling, note);
        }

        if (counter == 20) counter = 1;
        else counter++;
    }

    private void playNoteForAllPlayers(boolean shouldOthersHear, Instrument instrument, Note note) {
        if (shouldOthersHear) {
            for (Entity currentEntity : player.getNearbyEntities(50, 50, 50)) {
                if (!(currentEntity instanceof Player currentPlayer)) continue;
                if (worldBorderManager.getCountdownTasks().containsKey(currentPlayer)) continue;
                currentPlayer.playNote(player.getLocation(), instrument, note);
            }
        }
    }
}