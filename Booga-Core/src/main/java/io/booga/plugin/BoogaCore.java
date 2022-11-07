package io.booga.plugin;

import io.booga.plugin.command.PluginCommand;
import io.booga.plugin.command.impl.*;
import io.booga.plugin.player.Extension;
import io.booga.plugin.script.ScriptHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

public class BoogaCore extends JavaPlugin {

    public static Map<Player, Extension> getPlayerExtensions() {
        return playerExtensions;
    }

    private static Map<Player, Extension> playerExtensions = new HashMap();

    public static BoogaCore plugin;
    public static Economy econ = null;

    public static BoogaCore getPlugin() {
        return plugin;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Map<String, Class<? extends PluginCommand>> getCommands() {
        return commands;
    }

    public File getDataConfigFile() {
        return dataConfigFile;
    }

    public File getConfigFile() {
        return configFile;
    }

    private static final Map<String, Class<? extends PluginCommand>> commands = new HashMap();
    public File dataConfigFile;
    public File configFile;
    private FileConfiguration dataConfig;

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public void onLoad() {
        plugin = this;
        plugin.saveDefaultConfig();
        plugin.configFile = new File(getDataFolder(), "config.yml");
        plugin.createDataConfig();

        commands.put("config", Config.class);
        commands.put("guide", Guide.class);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PluginEventListener(), plugin);

        if (!setupEconomy()) {
            getLogger().severe(String.format("Cannot find an economy plugin! Economic features will not work.", getDescription().getName()));
        }

        activate();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        super.onCommand(sender, command, label, args);
        Player p = sender.getServer().getPlayer(sender.getName());
        if (plugin.isEnabled()) {
            try {
                if (args.length > 0 && commands.get(args[0]) != null)
                    commands.get(args[0]).getConstructor().newInstance().run(sender, command, args);
                else
                    p.performCommand("help booga");
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void onDisable() {
        clean();
    }

    public void activate() {
        //ScriptHandler.getScripts().put("LoopedLogic", new LoopedLogic());
        ScriptHandler.processor();
    }

    public void clean() {
        try {
            plugin.getConfig().save(configFile);
            plugin.getDataConfig().save(dataConfigFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ScriptHandler.shutdown();
    }

    public FileConfiguration getDataConfig() {
        return this.dataConfig;
    }

    private void createDataConfig() {
        dataConfigFile = new File(getDataFolder(), "data.yml");

        if (!dataConfigFile.exists()) {
            dataConfigFile.getParentFile().mkdirs();
            saveResource("data.yml", false);
        }

        dataConfig = new YamlConfiguration();

        try {
            dataConfig.load(dataConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
