package io.booga.plugin.command.impl;

import io.booga.plugin.BoogaCore;
import io.booga.plugin.command.PluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class Status extends PluginCommand {

    public final BoogaCore plugin;

    public Status() {
        plugin = BoogaCore.getPlugin();
    }

    @Override
    public String getPermission() {
        return "booga.status";
    }

    @Override
    public void run(CommandSender sender, Command command, String[] args) {
        Player p = sender.getServer().getPlayer(sender.getName());
        if (hasPermission(p)) {
            switch (plugin.getConfig().getString("game-settings.current-mode")) {
                case "WAR":
                    long warTime = plugin.getConfig().getLong("game-settings.war-time");
                    long totalMilliseconds = warTime - (new Date().getTime());
                    totalMilliseconds = totalMilliseconds < 0 ? 0 : totalMilliseconds;

                    long minutes = (totalMilliseconds / 1000) / 60;
                    long seconds = (totalMilliseconds / 1000) % 60;

                    p.sendMessage("§eThe Gods are §cenraged§e.");
                    p.sendMessage("§eThey will make peace in " + minutes + " minutes, and " + seconds + " seconds.");
                    break;
                case "PEACE":
                    long peaceTime = plugin.getConfig().getLong("game-settings.peace-time");
                    totalMilliseconds = peaceTime - (new Date().getTime());
                    totalMilliseconds = totalMilliseconds < 0 ? 0 : totalMilliseconds;

                    minutes = (totalMilliseconds / 1000) / 60;
                    seconds = (totalMilliseconds / 1000) % 60;

                    p.sendMessage("§eThe Gods are §acalm§e.");
                    p.sendMessage("§eThey expect their offerings in " + minutes + " minutes, and " + seconds + " seconds.");
                    break;
            }
        }
    }
}
