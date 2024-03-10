package dev.potato.xpworldborder;

import dev.potato.xpworldborder.commands.ToggleSoundCommand;
import dev.potato.xpworldborder.configurations.LevelConfig;
import dev.potato.xpworldborder.configurations.SetupConfig;
import dev.potato.xpworldborder.configurations.SoundConfig;
import dev.potato.xpworldborder.listeners.PlayerXPListeners;
import dev.potato.xpworldborder.tasks.OfflinePlayerLevelCheckTask;
import dev.potato.xpworldborder.utilities.enumerations.configurations.ConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.SetupConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.SoundConfigKeys;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class XPWorldBorder extends JavaPlugin {
    private static XPWorldBorder plugin;

    public static XPWorldBorder getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Initialization
        plugin = this;
        initializeConfiguration();
        initializeWorlds();

        // Listeners
        registerListeners();

        // Commands
        registerCommands();

        // Tasks
        runTasks();
    }

    private void initializeConfiguration() {
        // Config.yml
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Setup.yml
        SetupConfig.setup();
        FileConfiguration setupConfig = SetupConfig.getConfig();
        setupConfig.addDefault(SetupConfigKeys.SHOULD_INITIALIZE.KEY, true);
        setupConfig.options().copyDefaults(true);
        SetupConfig.save();

        // Sound.yml
        SoundConfig.setup();
        FileConfiguration soundConfig = SoundConfig.getConfig();
        soundConfig.addDefault(SoundConfigKeys.NO_SOUND_INCREASE.KEY, new ArrayList<>());
        soundConfig.addDefault(SoundConfigKeys.NO_SOUND_DECREASE.KEY, new ArrayList<>());
        soundConfig.options().copyDefaults(true);
        SoundConfig.save();

        // Levels.yml
        LevelConfig.setup();
        FileConfiguration levelConfig = LevelConfig.getConfig();
        levelConfig.options().copyDefaults(true);
        LevelConfig.save();
    }

    private void initializeWorlds() {
        FileConfiguration config = SetupConfig.getConfig();
        boolean shouldInitialize = config.getBoolean(SetupConfigKeys.SHOULD_INITIALIZE.KEY);
        if (!shouldInitialize) return;
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.SPAWN_RADIUS, 1);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            WorldBorder worldBorder = world.getWorldBorder();
            worldBorder.setCenter(world.getSpawnLocation());
            worldBorder.setSize(5);
            config.set(SetupConfigKeys.SHOULD_INITIALIZE.KEY, false);
            SetupConfig.save();
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerXPListeners(), this);
    }

    private void registerCommands() {
        getCommand("togglesound").setExecutor(new ToggleSoundCommand());
    }

    private void runTasks() {
        boolean updateBorderOnLeave = plugin.getConfig().getBoolean(ConfigKeys.UPDATE_BORDER_ON_LEAVE.KEY);
        boolean wipeOldLevelData = plugin.getConfig().getBoolean(ConfigKeys.WIPE_OLD_LEVEL_DATA.KEY);
        if (!updateBorderOnLeave && wipeOldLevelData) {
            new OfflinePlayerLevelCheckTask().runTaskTimer(this, 0, 100);
        }
    }
}