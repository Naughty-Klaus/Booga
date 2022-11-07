package io.booga.plugin.command.impl;

import io.booga.plugin.BoogaCore;
import io.booga.plugin.command.PluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Claim extends PluginCommand {

    public final BoogaCore plugin;

    public Claim() {
        plugin = BoogaCore.getPlugin();
    }

    @Override
    public String getPermission() {
        return "booga.claim";
    }

    @Override
    public void run(CommandSender sender, Command command, String[] args) {
        Player p = sender.getServer().getPlayer(sender.getName());
        if (hasPermission(p)) {
            if (args.length > 1 && args[1].equalsIgnoreCase("server")) {
                if (p.isOp()) {
                    String local = new StringBuilder().append(p.getLocation().getChunk().getX())
                            .append("_")
                            .append(p.getLocation().getChunk().getZ()).toString();

                    if (plugin.getDataConfig().contains("chunks." + local)) {
                        p.sendMessage("§cThis chunk has already been claimed!");
                        return;
                    }

                    plugin.getDataConfig().set("chunks." + local + ".owner", "server");
                    plugin.getDataConfig().set("chunks." + local + ".tnt-disabled", true);

                    p.sendMessage("§eThe server has reserved this chunk.");
                } else {
                    p.sendMessage("§cYou do not have the required permissions to use this command.");
                }
            } else {
                boolean claimsEnabled = plugin.getConfig().getBoolean("economy.vault.enabled") && plugin.getConfig().getBoolean("claim.enabled");
                int claimCost = plugin.getConfig().getInt("claim.claim-cost");

                String local = new StringBuilder().append(p.getLocation().getChunk().getX())
                        .append("_")
                        .append(p.getLocation().getChunk().getZ()).toString();

                if (plugin.getDataConfig().contains("chunks." + local)) {
                    p.sendMessage("§cThis chunk has already been claimed!");
                    return;
                }

                int maxClaims = plugin.getConfig().getInt("claim.max-claims");
                int totalClaims = plugin.getDataConfig().getInt("players." + p.getUniqueId() + ".total-claims");

                if (totalClaims >= maxClaims) {
                    p.sendMessage("You have too many claims!");
                    return;
                }

                if (BoogaCore.getEconomy() != null && claimsEnabled) {
                    if (BoogaCore.getEconomy().getBalance(p) >= claimCost) {
                        p.sendMessage("§e$" + claimCost + " has been taken from your account.");
                        BoogaCore.getEconomy().depositPlayer(p, -claimCost);
                    } else {
                        p.sendMessage("§eYou need at least $" + claimCost + " to claim a property.");
                        return;
                    }
                }

                plugin.getDataConfig().set("chunks." + local + ".owner", p.getUniqueId().toString());
                plugin.getDataConfig().set("chunks." + local + ".tnt-disabled", true);

                plugin.getDataConfig().set("players." + p.getUniqueId() + ".total-claims", totalClaims + 1);

                p.sendMessage("§eSuccessfully claimed this chunk!");
            }
        }
    }
}
