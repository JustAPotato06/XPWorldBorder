package dev.potato.xpworldborder.configurations;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SoundConfig {
    private static File file;
    private static FileConfiguration config;

    public static FileConfiguration getConfig() {
        return config;
    }

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("XPWorldBorder").getDataFolder(), "sound.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage(Component.text("[XP World Border] Could not create sound.yml!", NamedTextColor.RED));
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(Component.text("[XP World Border] Could not save sound.yml!", NamedTextColor.RED));
        }
    }
}