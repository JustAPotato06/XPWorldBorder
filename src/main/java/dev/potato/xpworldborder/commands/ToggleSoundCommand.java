package dev.potato.xpworldborder.commands;

import dev.potato.xpworldborder.configurations.SoundConfig;
import dev.potato.xpworldborder.utilities.enumerations.configurations.SoundConfigKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ToggleSoundCommand implements TabExecutor {
    private final String BORDER_INCREASE = "border-increase";
    private final String BORDER_DECREASE = "border-decrease";
    private final Component INCORRECT_USAGE = Component.text("[XP World Border] Incorrect usage! Example: /togglesound [sound name]", NamedTextColor.RED);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length != 1 && args.length != 0) {
            player.sendMessage(INCORRECT_USAGE);
            return true;
        }

        if (args.length == 0) {
            toggleAllSound(player);
            return true;
        }

        switch (args[0]) {
            case BORDER_INCREASE:
                toggleSoundIncrease(player);
                break;
            case BORDER_DECREASE:
                toggleSoundDecrease(player);
                break;
        }

        return true;
    }

    private void toggleAllSound(Player player) {
        FileConfiguration soundConfig = SoundConfig.getConfig();

        List<String> currentIncreasePlayers = (List<String>) soundConfig.getList(SoundConfigKeys.NO_SOUND_INCREASE.KEY);
        List<String> currentDecreasePlayers = (List<String>) soundConfig.getList(SoundConfigKeys.NO_SOUND_DECREASE.KEY);

        if (currentIncreasePlayers.contains(player.getName()) || currentDecreasePlayers.contains(player.getName())) {
            currentIncreasePlayers.remove(player.getName());
            currentDecreasePlayers.remove(player.getName());
            player.sendMessage(Component.text("[XP World Border] All world border sounds have now been enabled!", NamedTextColor.GREEN));
        } else {
            currentIncreasePlayers.add(player.getName());
            currentDecreasePlayers.add(player.getName());
            player.sendMessage(Component.text("[XP World Border] All world border sounds have now been disabled!", NamedTextColor.RED));
        }

        soundConfig.set(SoundConfigKeys.NO_SOUND_INCREASE.KEY, currentIncreasePlayers);
        soundConfig.set(SoundConfigKeys.NO_SOUND_DECREASE.KEY, currentDecreasePlayers);
        SoundConfig.save();
    }

    private void toggleSoundIncrease(Player player) {
        FileConfiguration soundConfig = SoundConfig.getConfig();
        List<String> currentPlayers = (List<String>) soundConfig.getList(SoundConfigKeys.NO_SOUND_INCREASE.KEY);
        if (currentPlayers.contains(player.getName())) {
            currentPlayers.remove(player.getName());
            player.sendMessage(Component.text("[XP World Border] World border increase sounds have now been enabled!", NamedTextColor.GREEN));
        } else {
            currentPlayers.add(player.getName());
            player.sendMessage(Component.text("[XP World Border] World border increase sounds have now been disabled!", NamedTextColor.RED));
        }
        soundConfig.set(SoundConfigKeys.NO_SOUND_INCREASE.KEY, currentPlayers);
        SoundConfig.save();
    }

    private void toggleSoundDecrease(Player player) {
        FileConfiguration soundConfig = SoundConfig.getConfig();
        List<String> currentPlayers = (List<String>) soundConfig.getList(SoundConfigKeys.NO_SOUND_DECREASE.KEY);
        if (currentPlayers.contains(player.getName())) {
            currentPlayers.remove(player.getName());
            player.sendMessage(Component.text("[XP World Border] World border decrease sounds have now been enabled!", NamedTextColor.GREEN));
        } else {
            currentPlayers.add(player.getName());
            player.sendMessage(Component.text("[XP World Border] World border decrease sounds have now been disabled!", NamedTextColor.RED));
        }
        soundConfig.set(SoundConfigKeys.NO_SOUND_DECREASE.KEY, currentPlayers);
        SoundConfig.save();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add(BORDER_DECREASE);
            completions.add(BORDER_INCREASE);
        }

        return completions;
    }
}