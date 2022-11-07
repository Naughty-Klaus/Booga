package io.booga.plugin;

import io.booga.plugin.command.PluginCommand;
import io.booga.plugin.command.impl.*;
import io.booga.plugin.player.Extension;
import io.booga.plugin.script.ScriptHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class BoogaTribes extends JavaPlugin {

    public static BoogaTribes plugin;

    public static BoogaTribes getPlugin() {
        return plugin;
    }

    public File getDataConfigFile() {
        return dataConfigFile;
    }

    public File getConfigFile() {
        return configFile;
    }
    public File dataConfigFile;
    public File configFile;
    private FileConfiguration dataConfig;

    @Override
    public void onLoad() {
        plugin = this;
        plugin.saveDefaultConfig();
        plugin.configFile = new File(getDataFolder(), "config.yml");
        plugin.createDataConfig();

        BoogaCore.getCommands().put("tribe", TribesCommand.class);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PluginEventListener(), plugin);
        activate();
    }

    @Override
    public void onDisable() {
        clean();
    }

    public void activate() {}

    public void clean() {
        try {
            plugin.getConfig().save(configFile);
            plugin.getDataConfig().save(dataConfigFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ScriptHandler.shutdown();
    }

    public static Set<String> getChunksByTribe(String tribe) {
        ConfigurationSection chunks = BoogaCore.getPlugin().getDataConfig().getConfigurationSection("chunks");
        Set<String> setChunks = chunks.getKeys(false);
        Iterator<String> keys = setChunks.iterator();
        Set<String> ownedChunks = new HashSet();

        while (keys.hasNext()) {
            String key = keys.next();
            if (chunks.contains(key)) {
                ConfigurationSection chunk = chunks.getConfigurationSection(key);
                if (chunk != null && chunk.contains("owner")) {
                    String chunkOwner = chunk.getString("owner");
                    if (chunkOwner.equalsIgnoreCase("tribes." + tribe)) {
                        try {
                            ownedChunks.add(chunk.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return ownedChunks;
    }

    public FileConfiguration getDataConfig() {
        return this.dataConfig;
    }

    private void createDataConfig() {
        dataConfigFile = new File(getDataFolder(), "tribes.yml");

        if (!dataConfigFile.exists()) {
            dataConfigFile.getParentFile().mkdirs();
            saveResource("tribes.yml", false);
        }

        dataConfig = new YamlConfiguration();

        try {
            dataConfig.load(dataConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
