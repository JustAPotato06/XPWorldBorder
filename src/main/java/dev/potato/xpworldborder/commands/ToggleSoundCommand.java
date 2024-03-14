package dev.potato.xpworldborder.commands;

import dev.potato.xpworldborder.configurations.SoundConfig;
import dev.potato.xpworldborder.utilities.LangUtilities;
import dev.potato.xpworldborder.utilities.enumerations.configurations.SoundConfigKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
    private final String OUTSIDE_BORDER = "outside-border";
    private final Component INCORRECT_USAGE = LangUtilities.PLUGIN_PREFIX.append(LegacyComponentSerializer.legacy('&').deserialize(" &cIncorrect usage! Example: /togglesound [sound name]"));

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        int length = args.length;

        if (length != 1 && length != 0) {
            player.sendMessage(INCORRECT_USAGE);
            return true;
        }

        if (length == 0) {
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
            case OUTSIDE_BORDER:
                toggleSoundOutsideBorder(player);
                break;
        }

        return true;
    }

    private void toggleAllSound(Player player) {
        FileConfiguration soundConfig = SoundConfig.getConfig();

        List<String> currentIncreasePlayers = (List<String>) soundConfig.getList(SoundConfigKeys.NO_SOUND_INCREASE.KEY);
        List<String> currentDecreasePlayers = (List<String>) soundConfig.getList(SoundConfigKeys.NO_SOUND_DECREASE.KEY);
        List<String> currentOutsideBorderPlayers = (List<String>) soundConfig.getList(SoundConfigKeys.NO_SOUND_OUTSIDE_BORDER.KEY);

        if (currentIncreasePlayers.contains(player.getName()) || currentDecreasePlayers.contains(player.getName()) || currentOutsideBorderPlayers.contains(player.getName())) {
            currentIncreasePlayers.remove(player.getName());
            currentDecreasePlayers.remove(player.getName());
            currentOutsideBorderPlayers.remove(player.getName());
            player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.ALL_SOUNDS_ENABLED)));
        } else {
            currentIncreasePlayers.add(player.getName());
            currentDecreasePlayers.add(player.getName());
            currentOutsideBorderPlayers.add(player.getName());
            player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.ALL_SOUNDS_DISABLED)));
        }

        soundConfig.set(SoundConfigKeys.NO_SOUND_INCREASE.KEY, currentIncreasePlayers);
        soundConfig.set(SoundConfigKeys.NO_SOUND_DECREASE.KEY, currentDecreasePlayers);
        soundConfig.set(SoundConfigKeys.NO_SOUND_OUTSIDE_BORDER.KEY, currentOutsideBorderPlayers);
        SoundConfig.save();
    }

    private void toggleSoundIncrease(Player player) {
        FileConfiguration soundConfig = SoundConfig.getConfig();
        List<String> currentPlayers = (List<String>) soundConfig.getList(SoundConfigKeys.NO_SOUND_INCREASE.KEY);

        if (currentPlayers.contains(player.getName())) {
            currentPlayers.remove(player.getName());
            player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.INCREASE_SOUNDS_ENABLED)));
        } else {
            currentPlayers.add(player.getName());
            player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.INCREASE_SOUNDS_DISABLED)));
        }

        soundConfig.set(SoundConfigKeys.NO_SOUND_INCREASE.KEY, currentPlayers);
        SoundConfig.save();
    }

    private void toggleSoundDecrease(Player player) {
        FileConfiguration soundConfig = SoundConfig.getConfig();
        List<String> currentPlayers = (List<String>) soundConfig.getList(SoundConfigKeys.NO_SOUND_DECREASE.KEY);

        if (currentPlayers.contains(player.getName())) {
            currentPlayers.remove(player.getName());
            player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.DECREASE_SOUNDS_ENABLED)));
        } else {
            currentPlayers.add(player.getName());
            player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.DECREASE_SOUNDS_DISABLED)));
        }

        soundConfig.set(SoundConfigKeys.NO_SOUND_DECREASE.KEY, currentPlayers);
        SoundConfig.save();
    }

    private void toggleSoundOutsideBorder(Player player) {
        FileConfiguration soundConfig = SoundConfig.getConfig();
        List<String> currentPlayers = (List<String>) soundConfig.getList(SoundConfigKeys.NO_SOUND_OUTSIDE_BORDER.KEY);

        if (currentPlayers.contains(player.getName())) {
            currentPlayers.remove(player.getName());
            player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.OUTSIDE_BORDER_SOUNDS_ENABLED)));
        } else {
            currentPlayers.add(player.getName());
            player.sendMessage(LangUtilities.PLUGIN_PREFIX.append(Component.text(" ").append(LangUtilities.OUTSIDE_BORDER_SOUNDS_DISABLED)));
        }

        soundConfig.set(SoundConfigKeys.NO_SOUND_OUTSIDE_BORDER.KEY, currentPlayers);
        SoundConfig.save();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add(BORDER_DECREASE);
            completions.add(BORDER_INCREASE);
            completions.add(OUTSIDE_BORDER);
        }

        return completions;
    }
}