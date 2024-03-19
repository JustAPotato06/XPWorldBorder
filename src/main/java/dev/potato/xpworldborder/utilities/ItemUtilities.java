package dev.potato.xpworldborder.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUtilities {
    public static ItemStack getCountdownX2Item() {
        ItemStack countdownX2 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta countdownX2Meta = countdownX2.getItemMeta();
        countdownX2Meta.addEnchant(Enchantment.DURABILITY, 1, false);
        countdownX2Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        countdownX2Meta.displayName(
                Component.text("X2 COUNTDOWN MULTIPLIER", NamedTextColor.GREEN, TextDecoration.BOLD)
        );
        countdownX2Meta.lore(
                List.of(
                        Component.text("Multiplies the amount of ", NamedTextColor.WHITE),
                        Component.text("seconds you can survive while ", NamedTextColor.WHITE),
                        Component.text("outside the world border by ", NamedTextColor.WHITE)
                                .append(Component.text("2", NamedTextColor.GREEN))
                                .append(Component.text("!", NamedTextColor.WHITE))
                )
        );
        countdownX2.setItemMeta(countdownX2Meta);
        return countdownX2;
    }

    public static ItemStack getCountdownX3Item() {
        ItemStack countdownX3 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta countdownX3Meta = countdownX3.getItemMeta();
        countdownX3Meta.addEnchant(Enchantment.DURABILITY, 1, false);
        countdownX3Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        countdownX3Meta.displayName(
                Component.text("X3 COUNTDOWN MULTIPLIER", NamedTextColor.AQUA, TextDecoration.BOLD)
        );
        countdownX3Meta.lore(
                List.of(
                        Component.text("Multiplies the amount of ", NamedTextColor.WHITE),
                        Component.text("seconds you can survive while ", NamedTextColor.WHITE),
                        Component.text("outside the world border by ", NamedTextColor.WHITE)
                                .append(Component.text("3", NamedTextColor.AQUA))
                                .append(Component.text("!", NamedTextColor.WHITE))
                )
        );
        countdownX3.setItemMeta(countdownX3Meta);
        return countdownX3;
    }

    public static ItemStack getCountdownX4Item() {
        ItemStack countdownX4 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta countdownX4Meta = countdownX4.getItemMeta();
        countdownX4Meta.addEnchant(Enchantment.DURABILITY, 1, false);
        countdownX4Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        countdownX4Meta.displayName(
                Component.text("X4 COUNTDOWN MULTIPLIER", NamedTextColor.GOLD, TextDecoration.BOLD)
        );
        countdownX4Meta.lore(
                List.of(
                        Component.text("Multiplies the amount of ", NamedTextColor.WHITE),
                        Component.text("seconds you can survive while ", NamedTextColor.WHITE),
                        Component.text("outside the world border by ", NamedTextColor.WHITE)
                                .append(Component.text("4", NamedTextColor.GOLD))
                                .append(Component.text("!", NamedTextColor.WHITE))
                )
        );
        countdownX4.setItemMeta(countdownX4Meta);
        return countdownX4;
    }
}