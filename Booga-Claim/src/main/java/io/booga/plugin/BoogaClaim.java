package io.booga.plugin;

import io.booga.plugin.command.impl.Claim;
import io.booga.plugin.command.impl.Explosives;
import io.booga.plugin.command.impl.Unclaim;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BoogaClaim extends JavaPlugin {
    public static BoogaClaim plugin;

    public static BoogaClaim getPlugin() {
        return plugin;
    }

    public File getDataConfigFile() {
        return BoogaCore.getPlugin().getDataConfigFile();
    }

    public File getConfigFile() {
        return BoogaCore.getPlugin().getConfigFile();
    }

    @Override
    public void onLoad() {
        plugin = this;

        BoogaCore.getCommands().put("claim", Claim.class);
        BoogaCore.getCommands().put("unclaim", Unclaim.class);
        BoogaCore.getCommands().put("explosives", Explosives.class);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PluginEventListener(), plugin);
    }
    @Override
    public void onDisable() {}

    public FileConfiguration getDataConfig() {
        return BoogaCore.getPlugin().getDataConfig();
    }

    public static Set<String> getChunksByOwner(String owner) {
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
                    if (chunkOwner.equalsIgnoreCase(owner)) {
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
}
