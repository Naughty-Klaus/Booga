package io.booga.plugin.command.impl;

import io.booga.plugin.BoogaCore;
import io.booga.plugin.BoogaGame;
import io.booga.plugin.command.PluginCommand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class Offer extends PluginCommand {

    public final BoogaCore plugin;

    public Offer() {
        plugin = BoogaCore.getPlugin();
    }

    @Override
    public String getPermission() {
        return "booga.offer";
    }

    public void removeFromInventory(Inventory inventory, ItemStack item) {
        int amt = item.getAmount();
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() == item.getType() && items[i].getDurability() == item.getDurability()) {
                if (items[i].getAmount() > amt) {
                    items[i].setAmount(items[i].getAmount() - amt);
                    break;
                } else if (items[i].getAmount() == amt) {
                    items[i] = null;
                    break;
                } else {
                    amt -= items[i].getAmount();
                    items[i] = null;
                }
            }
        }
        inventory.setContents(items);
    }

    private boolean inventoryContains(Inventory inventory, ItemStack item) {
        int count = 0;
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() == item.getType() && items[i].getDurability() == item.getDurability()) {
                count += items[i].getAmount();
            }
            if (count >= item.getAmount()) {
                return true;
            }
        }
        return false;
    }

    public static Material getValidContributionMaterial(String itemName, Player p) {
        switch(itemName) {
            case "diamond":
                return Material.DIAMOND;
            case "emerald":
                return Material.EMERALD;
            case "gold-ingot":
                return Material.GOLD_INGOT;
            case "iron-ingot":
                return Material.IRON_INGOT;
            case "redstone":
                return Material.REDSTONE;
            case "wheat":
                if(p != null && p instanceof Player) {
                    return Material.values()[772];
                }
                return Material.LEGACY_CROPS;
            case "carrot":
                return Material.CARROT;
            case "potato":
                return Material.POTATO;
            default:
                return null;
        }
    }

    @Override
    public void run(CommandSender sender, Command command, String[] args) {
        Player p = sender.getServer().getPlayer(sender.getName());
        if (hasPermission(p)) {
            if (args.length > 2) {
                int offeredAmount = 0;
                String itemName = args[1].replaceAll("_", "-").toLowerCase();

                if(itemName.equalsIgnoreCase("hand"))
                    p.sendMessage(Material.values()[p.getInventory().getItemInMainHand().getType().ordinal()] + ", " + p.getInventory().getItemInMainHand().getType().getKey().getKey() + ", " + p.getInventory().getItemInMainHand().getType().ordinal());

                try {
                    offeredAmount = Integer.parseInt(args[2]);
                } catch (Exception e) {
                    p.sendMessage("Could not offer a malformed number of items.");
                    return;
                }

                boolean donated = false;
                boolean isValuable = false;

                switch (plugin.getConfig().getString("game-settings.current-mode")) {
                    case "WAR":
                        p.sendMessage("§eThe Gods are §cangry§e, and only time will calm them!");
                        break;
                    case "PEACE":
                        long peaceTime = plugin.getConfig().getLong("game-settings.peace-time");
                        Material ref = getValidContributionMaterial(itemName, p);

                        if(ref == null) {
                            p.sendMessage(itemName + " is not a valid item.");
                            return;
                        }

                        if(itemName.equalsIgnoreCase("diamond") || itemName.equalsIgnoreCase("emerald"))
                            isValuable = true;


                        ItemStack offeredStack = new ItemStack(ref, offeredAmount);
                        if (inventoryContains(p.getInventory(), offeredStack)) {
                            long contribution = 0;

                            try {
                                contribution = plugin.getConfig().getLong("contributions." + itemName);
                            } catch (Exception e) {
                                p.sendMessage("§eThe Gods are not interested in this item§e.");
                                break;
                            }

                            long offeredMilliseconds = offeredAmount * contribution;
                            long offeredMinutes = (offeredMilliseconds / 1000) / 60;
                            long offeredSeconds = (offeredMilliseconds / 1000) % 60;

                            removeFromInventory(p.getInventory(), offeredStack);

                            peaceTime += offeredAmount * contribution;

                            donated = true;

                            plugin.getConfig().set("game-settings.peace-time", peaceTime);

                            p.sendMessage("§eThe Gods are grateful. You've offered: " + offeredMinutes + " minutes and " + offeredSeconds + " seconds worth of protection.");
                        } else {
                            p.sendMessage("§eYou require §b" + offeredAmount + " " + (offeredAmount == 1 ? itemName.replaceAll("-", " ") : (itemName.replaceAll("-", " ") + "s")) + "§e.");
                        }
                        break;
                }

                if (donated && isValuable) {
                    int duration = offeredAmount * 1500;

                    for (int i = 0; i < offeredAmount; i++) {
                        PotionEffectType type = BoogaGame.boosts[ThreadLocalRandom.current().nextInt(BoogaGame.boosts.length)];
                        p.addPotionEffect(new PotionEffect(type, duration, ThreadLocalRandom.current().nextInt(2)));
                    }

                    p.sendMessage("§eThe Gods appreciate your offer and §abless §eyou.");
                }
            } else {
                p.sendMessage("/booga offer [item name (use '-' instead of spaces)] [amount]");
            }
        }
    }
}
