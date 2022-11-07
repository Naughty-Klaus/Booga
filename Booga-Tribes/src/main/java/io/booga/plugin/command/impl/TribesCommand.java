package io.booga.plugin.command.impl;

import io.booga.plugin.BoogaClaim;
import io.booga.plugin.BoogaCore;
import io.booga.plugin.BoogaTribes;
import io.booga.plugin.command.PluginCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class TribesCommand extends PluginCommand {

    public final BoogaTribes plugin;

    public TribesCommand() {
        plugin = BoogaTribes.getPlugin();
    }

    @Override
    public String getPermission() {
        return "booga.tribes";
    }

    public boolean unclaimAllTribeChunks(CommandSender sender, String tribeName) {
        Set<String> ownedChunks = plugin.getChunksByTribe(tribeName);
        Iterator<String> iterator = ownedChunks.iterator();

        if (iterator == null
                || (iterator != null && !iterator.hasNext())) {
            sender.sendMessage("§cSomething went wrong.");
            return false;
        } else {
            try {
                int total0 = ownedChunks.size();
                int total1 = ownedChunks.size();

                while (iterator.hasNext()) {
                    BoogaCore.getPlugin().getDataConfig().set("chunks." + iterator.next(), null);
                    total0--;
                }

                plugin.getDataConfig().set("tribes." + tribeName + ".total-claims", total0);
                // sender.sendMessage("Unclaimed " + (total1 - total0) + " chunks!");
            } catch (Exception e) {
                sender.sendMessage("§cSomething went wrong.");
                return false;
            }
        }
        return true;
    }

    @Override
    public void run(CommandSender sender, Command command, String[] args) throws IOException {
        Player p = sender.getServer().getPlayer(sender.getName());
        if (sender instanceof ConsoleCommandSender || (sender instanceof Player && hasPermission(p))) {
            if (args.length > 1) {
                switch (args[1]) {
                    case "create":
                        if (args.length > 2) {
                            if (args[2].length() > 3 && args[2].length() < 17) {
                                String tribeName = args[2];
                                boolean meetsConditions = !BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId() + ".tribe");

                                if (!meetsConditions) {
                                    sender.sendMessage("§cYou already belong to a tribe!");
                                    return;
                                }

                                meetsConditions = tribeName.matches("[a-zA-Z0-9_\\s]*")
                                        && !tribeName.substring(0, 1).matches("[_\\s]*")
                                        && !tribeName.substring(tribeName.length() - 1, tribeName.length()).matches("[_\\s]*");

                                if (!plugin.getDataConfig().contains("tribes." + tribeName.toLowerCase())) {
                                    if (meetsConditions) { //tribeName.matches("[a-zA-Z0-9_\\s]*")) { // "[a-zA-Z0-9,.;:_'\\s-]*"
                                        Collection<String> members = new ArrayList<>();
                                        members.add(p.getUniqueId().toString());

                                        plugin.getDataConfig().set("tribes." + tribeName.toLowerCase() + ".DisplayName", tribeName);
                                        plugin.getDataConfig().set("tribes." + tribeName.toLowerCase() + ".owner", p.getUniqueId().toString());
                                        plugin.getDataConfig().set("tribes." + tribeName.toLowerCase() + ".members", members);

                                        BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".tribe", tribeName.toLowerCase());

                                        sender.sendMessage("§eTribe created successfully! Welcome chief " + p.getDisplayName() + "!");
                                    } else {
                                        sender.sendMessage("§cThis tribe name is not allowed.");
                                    }
                                } else {
                                    sender.sendMessage("§cThis tribe name has already been taken!");
                                }
                            } else {
                                sender.sendMessage("§cTribe names must be in-between 3 and 16 characters long.");
                            }
                        } else {
                            sender.sendMessage("§eInvalid number of arguments.");
                            sender.sendMessage("§eTry /booga tribe create Name_Here");
                        }
                        break;
                    case "invite":
                        if (args.length > 2) {
                            if (sender instanceof ConsoleCommandSender || (sender instanceof Player && BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId() + ".tribe"))) {
                                String tribe = sender instanceof ConsoleCommandSender ? "demonlord" : BoogaCore.getPlugin().getDataConfig().getString("players." + p.getUniqueId() + ".tribe");
                                if (sender instanceof ConsoleCommandSender || (sender instanceof Player && plugin.getDataConfig().getString("tribes." + tribe + ".owner").equals(p.getUniqueId().toString()))) {
                                    String playerName = args[2];
                                    Player target = plugin.getServer().getPlayer(playerName);
                                    if (target != null && target.isOnline()) {
                                        BoogaCore.getPlayerExtensions().get(target).lastTribeInvite = tribe.toLowerCase();

                                        target.sendMessage("§eYou have been invited to the '" + tribe + "' tribe.");
                                        target.sendMessage("§eYou can accept this invite by using \"/booga tribe join\".");
                                        target.sendMessage("§eThis invite expires when you logout or someone else invites you to another tribe.");

                                        sender.sendMessage("§eYou have invited " + target.getDisplayName() + " to your tribe!");
                                    }
                                } else {
                                    sender.sendMessage("§cYou are not the chief of your tribe!");
                                }
                            } else {
                                sender.sendMessage("§cYou don't belong to a tribe!");
                            }
                        }
                        break;
                    case "join":
                        if (args.length > 1) {
                            if (!BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId() + ".tribe")) {
                                String tribe = BoogaCore.getPlayerExtensions().get(p).lastTribeInvite.toLowerCase();

                                if (tribe.equalsIgnoreCase("")) {
                                    sender.sendMessage("§cYou need to have been invited to a tribe.");
                                    return;
                                }

                                Collection<String> members = new ArrayList<>();

                                for (String uuid : plugin.getDataConfig().getStringList("tribes." + tribe + ".members")) {
                                    members.add(uuid);
                                }

                                if (!members.contains(p.getUniqueId().toString())) {
                                    members.add(p.getUniqueId().toString());

                                    plugin.getDataConfig().set("tribes." + tribe + ".members", members);
                                    BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".tribe", tribe);

                                    sender.sendMessage("§eWelcome to the " + tribe + " tribe!");
                                } else {
                                    sender.sendMessage("§eYou are already in this tribe.");
                                }
                            } else {
                                sender.sendMessage("§cYou already belong to a tribe!");
                            }
                        } else {
                            sender.sendMessage("§cTry again, correctly.");
                        }
                        break;
                    case "claim":
                        if (hasPermission(p)) {
                            if (BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId() + ".tribe")) {
                                boolean claimsEnabled = BoogaCore.getPlugin().getConfig().getBoolean("economy.vault.enabled") && plugin.getConfig().getBoolean("claim.enabled");
                                int claimCost = BoogaCore.getPlugin().getConfig().getInt("claim.claim-cost");

                                String tribe = BoogaCore.getPlugin().getDataConfig().getString("players." + p.getUniqueId() + ".tribe");

                                String local = new StringBuilder().append(p.getLocation().getChunk().getX())
                                        .append("_")
                                        .append(p.getLocation().getChunk().getZ()).toString();

                                if (plugin.getDataConfig().getString("tribes." + tribe + ".owner").equals(p.getUniqueId().toString())) {
                                    if (BoogaCore.getPlugin().getDataConfig().contains("chunks." + local)) {
                                        sender.sendMessage("§cThis chunk has already been claimed!");
                                        return;
                                    }

                                    Collection<String> members = new ArrayList<>();

                                    for (String uuid : plugin.getDataConfig().getStringList("tribes." + tribe + ".members")) {
                                        members.add(uuid);
                                    }

                                    int maxClaims = BoogaCore.getPlugin().getConfig().getInt("claim.max-claims") * members.size();
                                    int totalClaims = plugin.getDataConfig().getInt("tribes." + tribe + ".total-claims");

                                    if (totalClaims >= maxClaims) {
                                        sender.sendMessage("§cYour tribe has too many claims!");
                                        return;
                                    }

                                    if (BoogaCore.getEconomy() != null && claimsEnabled) {
                                        if (BoogaCore.getEconomy().getBalance(p) >= claimCost) {
                                            sender.sendMessage("§e$" + claimCost + " has been taken from your account.");
                                            BoogaCore.getEconomy().withdrawPlayer(p, claimCost);
                                        } else {
                                            sender.sendMessage("§eYou need at least $" + claimCost + " to claim land.");
                                            return;
                                        }
                                    }

                                    BoogaCore.getPlugin().getDataConfig().set("chunks." + local + ".owner", "tribes." + tribe);
                                    BoogaCore.getPlugin().getDataConfig().set("chunks." + local + ".tnt-disabled", true);

                                    plugin.getDataConfig().set("tribes." + tribe + ".total-claims", totalClaims + 1);

                                    sender.sendMessage("§eSuccessfully claimed this chunk for your tribe!");
                                } else {
                                    sender.sendMessage("§eYou have to be the chief to claim land.");
                                }
                            } else {
                                sender.sendMessage("§cYou do not belong to a tribe!");
                            }
                        }
                        break;
                    case "unclaim":
                        if (args.length > 2 && args[2].equalsIgnoreCase("all")) {
                            if (BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId() + ".tribe")) {
                                String tribe = BoogaCore.getPlugin().getDataConfig().getString("players." + p.getUniqueId() + ".tribe");
                                if (plugin.getDataConfig().getString("tribes." + tribe + ".owner").equals(p.getUniqueId().toString())) {
                                    boolean success = unclaimAllTribeChunks(sender, tribe);
                                    if(success)
                                        sender.sendMessage("§eUnclaimed all of your tribe-owned land.");
                                } else {
                                    sender.sendMessage("§cYou need to be the tribe chief to do this!");
                                }
                            } else {
                                sender.sendMessage("§cYou are not in a tribe.");
                            }
                        } else {
                            if (BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId() + ".tribe")) {
                                String tribe = BoogaCore.getPlugin().getDataConfig().getString("players." + p.getUniqueId() + ".tribe");
                                int totalClaims = plugin.getDataConfig().getInt("tribes." + tribe + ".total-claims");
                                if (plugin.getDataConfig().getString("tribes." + tribe + ".owner").equals(p.getUniqueId().toString())) {
                                    String local = new StringBuilder().append(p.getLocation().getChunk().getX())
                                            .append("_")
                                            .append(p.getLocation().getChunk().getZ()).toString();

                                    if (BoogaCore.getPlugin().getDataConfig().contains("chunks." + local)) {
                                        BoogaCore.getPlugin().getDataConfig().set("chunks." + local, null);
                                        plugin.getDataConfig().set("tribes." + tribe + ".total-claims", totalClaims - 1);
                                        sender.sendMessage("§eYou have successfully unclaimed this chunk.");
                                        return;
                                    }
                                } else {
                                    sender.sendMessage("§cYou need to be the tribe chief to do this!");
                                }
                            } else {
                                sender.sendMessage("§cYou are not in a tribe.");
                            }
                        }
                        break;
                    case "abandon":
                        if (args.length > 2) {
                            String tribeName = args[2].toLowerCase();
                            if(BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId() + ".tribe")) {
                                if (tribeName.equalsIgnoreCase(BoogaCore.getPlugin().getDataConfig().getString("players." + p.getUniqueId() + ".tribe"))) {
                                    String tribeOwnerUUID = plugin.getDataConfig().getString("tribes." + tribeName + ".owner");
                                    Player tribeOwner = plugin.getServer().getPlayer(UUID.fromString(tribeOwnerUUID));

                                    Collection<String> members = new ArrayList<>();

                                    for (String uuid : plugin.getDataConfig().getStringList("tribes." + tribeName + ".members")) {
                                        members.add(uuid);
                                    }

                                    members.remove(tribeOwnerUUID);

                                    if(p.equals(tribeOwner)) {
                                        if(members.size() > 0) {
                                            String newOwnerUUID = members.stream().toList().get(0);
                                            OfflinePlayer newOwner = plugin.getServer().getOfflinePlayer(UUID.fromString(newOwnerUUID));

                                            plugin.getDataConfig().set("tribes." + tribeName + ".owner", newOwnerUUID);
                                            BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".tribe", null);

                                            plugin.getDataConfig().set("tribes." + tribeName + ".members", members);

                                            sender.sendMessage("§eAs you abandon your tribe, §c" + newOwner.getName() + " §ehas become the new chief.");
                                        } else {
                                            unclaimAllTribeChunks(sender, tribeName);

                                            plugin.getDataConfig().set("tribes." + tribeName, null);
                                            BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".tribe", null);

                                            sender.sendMessage("§e" + tribeName + " tribe has been disbanded!");
                                        }
                                    } else {
                                        BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".tribe", null);
                                        plugin.getDataConfig().set("tribes." + tribeName + ".members", members);

                                        sender.sendMessage("§eYou have abandoned your tribe.");
                                    }
                                } else {
                                    sender.sendMessage("In order to abandon your tribe, confirm your decision by using:");
                                    sender.sendMessage("/booga tribe abandon " + tribeName);
                                }
                            } else {
                                sender.sendMessage("§cYou don't belong to a tribe.");
                            }
                        } else {
                            sender.sendMessage("§ePlease include your current tribe's name to confirm!");
                        }
                        break;
                    case "info":
                        if(sender instanceof Player) {
                            if (BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId() + ".tribe")) {
                                String tribeName = BoogaCore.getPlugin().getDataConfig().getString("players." + p.getUniqueId() + ".tribe");
                                String tribeOwner = plugin.getDataConfig().getString("tribes." + tribeName + ".owner");

                                List<String> members = plugin.getDataConfig().getStringList("tribes." + tribeName + ".members");
                                int totalMembers = members.size();
                                members.remove(p.getUniqueId().toString());

                                int claimLimit = totalMembers * 9;
                                int claimAmount = BoogaTribes.getChunksByTribe(tribeName).size();

                                String strMembers = "";

                                for (String uuid: members) {
                                    strMembers += "§c" + plugin.getServer().getOfflinePlayer(UUID.fromString(uuid)).getName() + "§e, ";
                                }

                                p.sendMessage("§eYou belong to the §c" + tribeName + " §etribe.");
                                p.sendMessage("§eThe tribe is lead by §cChief " + plugin.getServer().getOfflinePlayer(UUID.fromString(tribeOwner)).getName() + "§e.");
                                p.sendMessage("§eYour fellow tribesmen are: " + strMembers.substring(0, strMembers.length() - 2) + "§e.");
                                p.sendMessage("");
                                p.sendMessage("§eYour tribe has §c" + totalMembers + "/9 §etribesmen.");
                                p.sendMessage("§eYour tribe has claimed §c" + claimAmount + "/" + claimLimit + " §echunks.");

                            } else {
                                p.sendMessage("§eYou need to be part of a tribe to use this command.");
                            }
                        } else {
                            sender.sendMessage("Only online players can issue this command!");
                        }
                        break;
                    case "save":
                        if (sender instanceof ConsoleCommandSender || (sender instanceof Player && p.hasPermission("booga.config")))
                            if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
                                plugin.getDataConfig().save(BoogaTribes.getPlugin().getDataConfigFile());
                                plugin.getConfig().save(BoogaTribes.getPlugin().getConfigFile());
                                sender.sendMessage("§eTribe config saved!");
                            }
                        break;
                    case "reload":
                        if (sender instanceof ConsoleCommandSender || (sender instanceof Player && p.hasPermission("booga.config")))
                            if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
                                try {
                                    plugin.getDataConfig().load(BoogaTribes.getPlugin().getDataConfigFile());
                                    plugin.getConfig().load(BoogaTribes.getPlugin().getConfigFile());
                                    sender.sendMessage("§eTribe config reloaded!");
                                } catch (InvalidConfigurationException e) {
                                    sender.sendMessage("§cError occurred while reloading configuration!");
                                }

                            }
                        break;
                    default:
                        sender.sendMessage("§eExample: /booga config <save/reload>");
                        break;
                }
            }
        }
    }
}
