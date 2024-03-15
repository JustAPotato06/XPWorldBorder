package dev.potato.xpworldborder;

import dev.potato.xpworldborder.commands.ToggleSoundCommand;
import dev.potato.xpworldborder.configurations.LangConfig;
import dev.potato.xpworldborder.configurations.LevelConfig;
import dev.potato.xpworldborder.configurations.SetupConfig;
import dev.potato.xpworldborder.configurations.SoundConfig;
import dev.potato.xpworldborder.listeners.PlayerXPListeners;
import dev.potato.xpworldborder.tasks.OfflinePlayerLevelCheckTask;
import dev.potato.xpworldborder.utilities.enumerations.configurations.ConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.LangConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.SetupConfigKeys;
import dev.potato.xpworldborder.utilities.enumerations.configurations.SoundConfigKeys;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
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

        // Recipes
        registerRecipes();
    }

    private void initializeConfiguration() {
        File dataFolder = getDataFolder();

        // Config.yml
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Setup.yml
        SetupConfig.setup(dataFolder);
        FileConfiguration setupConfig = SetupConfig.getConfig();
        setupConfig.addDefault(SetupConfigKeys.SHOULD_INITIALIZE_WORLDS.KEY, true);
        setupConfig.options().copyDefaults(true);
        SetupConfig.save();

        // Sound.yml
        SoundConfig.setup(dataFolder);
        FileConfiguration soundConfig = SoundConfig.getConfig();
        soundConfig.addDefault(SoundConfigKeys.NO_SOUND_INCREASE.KEY, new ArrayList<>());
        soundConfig.addDefault(SoundConfigKeys.NO_SOUND_DECREASE.KEY, new ArrayList<>());
        soundConfig.addDefault(SoundConfigKeys.NO_SOUND_OUTSIDE_BORDER.KEY, new ArrayList<>());
        soundConfig.options().copyDefaults(true);
        SoundConfig.save();

        // Levels.yml
        LevelConfig.setup(dataFolder);
        FileConfiguration levelConfig = LevelConfig.getConfig();
        levelConfig.options().copyDefaults(true);
        LevelConfig.save();

        // Lang.yml
        LangConfig.setup(dataFolder);
        FileConfiguration langConfig = LangConfig.getConfig();
        addLangDefaults(langConfig);
        langConfig.options().copyDefaults(true);
        LangConfig.save();
    }

    private void addLangDefaults(FileConfiguration langConfig) {
        langConfig.addDefault(LangConfigKeys.PLUGIN_PREFIX.KEY, "&2&l[XP World Border]&r");
        langConfig.addDefault(LangConfigKeys.LEFT_WHILE_OUTSIDE_BORDER.KEY, "&cYou left while outside the world border! You have been killed and sent back to spawn.");
        langConfig.addDefault(LangConfigKeys.LEFT_AND_BORDER_SHRUNK.KEY, "&aThe world border shrunk while you were gone! You've been teleported inside the border.");
        langConfig.addDefault(LangConfigKeys.ALL_SOUNDS_ENABLED.KEY, "&aAll world border sounds have now been enabled!");
        langConfig.addDefault(LangConfigKeys.ALL_SOUNDS_DISABLED.KEY, "&cAll world border sounds have now been disabled!");
        langConfig.addDefault(LangConfigKeys.INCREASE_SOUNDS_ENABLED.KEY, "&aWorld border increase sounds have now been enabled!");
        langConfig.addDefault(LangConfigKeys.INCREASE_SOUNDS_DISABLED.KEY, "&cWorld border increase sounds have now been disabled!");
        langConfig.addDefault(LangConfigKeys.DECREASE_SOUNDS_ENABLED.KEY, "&aWorld border decrease sounds have now been enabled!");
        langConfig.addDefault(LangConfigKeys.DECREASE_SOUNDS_DISABLED.KEY, "&cWorld border decrease sounds have now been disabled!");
        langConfig.addDefault(LangConfigKeys.OUTSIDE_BORDER_SOUNDS_ENABLED.KEY, "&aOutside world border tick sounds have now been enabled!");
        langConfig.addDefault(LangConfigKeys.OUTSIDE_BORDER_SOUNDS_DISABLED.KEY, "&cOutside world border tick sounds have now been disabled!");
    }

    private void initializeWorlds() {
        FileConfiguration config = SetupConfig.getConfig();
        boolean shouldInitialize = config.getBoolean(SetupConfigKeys.SHOULD_INITIALIZE_WORLDS.KEY);
        if (!shouldInitialize) return;
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.SPAWN_RADIUS, 1);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            WorldBorder worldBorder = world.getWorldBorder();
            worldBorder.setCenter(world.getSpawnLocation());
            int startingWorldSize = getConfig().getInt(ConfigKeys.STARTING_PLAYER_LEVEL.KEY);
            if (startingWorldSize <= 1) startingWorldSize = 2;
            worldBorder.setSize(startingWorldSize);
            config.set(SetupConfigKeys.SHOULD_INITIALIZE_WORLDS.KEY, false);
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

    private void registerRecipes() {
        // TODO - Add special items
    }
}