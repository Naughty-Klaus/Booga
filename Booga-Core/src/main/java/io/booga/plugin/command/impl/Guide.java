package io.booga.plugin.command.impl;

import io.booga.plugin.BoogaCore;
import io.booga.plugin.PluginEventListener;
import io.booga.plugin.command.PluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Guide extends PluginCommand {

    public final BoogaCore plugin;

    public Guide() {
        plugin = BoogaCore.plugin;
    }

    @Override
    public String getPermission() {
        return "booga.guide";
    }

    @Override
    public void run(CommandSender sender, Command command, String[] args) {
        Player p = sender.getServer().getPlayer(sender.getName());
        if (hasPermission(p)) {
            p.getInventory().addItem(PluginEventListener.getNewcomerBook(p));
        }
    }
}
