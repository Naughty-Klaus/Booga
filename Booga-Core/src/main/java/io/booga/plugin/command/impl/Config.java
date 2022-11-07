package io.booga.plugin.command.impl;

import io.booga.plugin.BoogaCore;
import io.booga.plugin.command.PluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Config extends PluginCommand {

    public final BoogaCore plugin;

    public Config() {
        plugin = BoogaCore.plugin;
    }

    @Override
    public String getPermission() {
        return "booga.config";
    }

    @Override
    public void run(CommandSender sender, Command command, String[] args) {
        Player p = sender.getServer().getPlayer(sender.getName());
        if ((sender instanceof Player && hasPermission(p)) || sender instanceof ConsoleCommandSender) {
            if (args.length > 1) {
                switch (args[1]) {
                    case "save":
                        try {
                            plugin.getConfig().save(plugin.configFile);
                            plugin.getDataConfig().save(plugin.dataConfigFile);
                            sender.sendMessage("§eConfig saved!");
                        } catch (IOException e) {
                            sender.sendMessage("§cError occurred when saving config.");
                            e.printStackTrace();
                        }
                        break;
                    case "reload":
                        try {
                            plugin.getConfig().load(plugin.configFile);
                            plugin.getDataConfig().load(plugin.dataConfigFile);
                            sender.sendMessage("§eConfig reloaded!");
                        } catch (IOException | InvalidConfigurationException e) {
                            sender.sendMessage("§cError occurred when reloading config.");
                            e.printStackTrace();
                        }
                        break;
                    default:
                        p.sendMessage("Example: /booga config <save/reload>");
                        break;
                }
            }
        }
    }
}
