package dev.potato.xpworldborder.commands;

import dev.potato.xpworldborder.utilities.ItemUtilities;
import dev.potato.xpworldborder.utilities.LangUtilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GiveSpecialItemCommand implements TabExecutor {
    private final Component INCORRECT_USAGE = LangUtilities.PLUGIN_PREFIX.append(LegacyComponentSerializer.legacy('&').deserialize(" &cIncorrect usage! Example: /givespecialitem [item name]"));
    private final Component SPECIAL_ITEM_NOT_FOUND = LangUtilities.PLUGIN_PREFIX.append(LegacyComponentSerializer.legacy('&').deserialize(" &cThe special item you specified was not found! Please try again."));
    private final String COUNTDOWN_X2_ITEM = "countdown_x2";
    private final String COUNTDOWN_X3_ITEM = "countdown_x3";
    private final String COUNTDOWN_X4_ITEM = "countdown_x4";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length != 1) {
            player.sendMessage(INCORRECT_USAGE);
            return true;
        }

        String itemName = args[0];

        switch (itemName) {
            case COUNTDOWN_X2_ITEM:
                player.getInventory().addItem(ItemUtilities.getCountdownX2Item());
                player.sendMessage(
                        LangUtilities.PLUGIN_PREFIX.append(
                                LegacyComponentSerializer.legacy('&').deserialize(" &aYou have been given a &a&lX2 COUNTDOWN MULTIPLIER&r&a!")
                        )
                );
                break;
            case COUNTDOWN_X3_ITEM:
                player.getInventory().addItem(ItemUtilities.getCountdownX3Item());
                player.sendMessage(
                        LangUtilities.PLUGIN_PREFIX.append(
                                LegacyComponentSerializer.legacy('&').deserialize(" &aYou have been given a &b&lX3 COUNTDOWN MULTIPLIER&r&a!")
                        )
                );
                break;
            case COUNTDOWN_X4_ITEM:
                player.getInventory().addItem(ItemUtilities.getCountdownX4Item());
                player.sendMessage(
                        LangUtilities.PLUGIN_PREFIX.append(
                                LegacyComponentSerializer.legacy('&').deserialize(" &aYou have been given a &6&lX4 COUNTDOWN MULTIPLIER&r&a!")
                        )
                );
                break;
            default:
                player.sendMessage(SPECIAL_ITEM_NOT_FOUND);
                break;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add(COUNTDOWN_X2_ITEM);
            completions.add(COUNTDOWN_X3_ITEM);
            completions.add(COUNTDOWN_X4_ITEM);
        }

        return completions;
    }
}