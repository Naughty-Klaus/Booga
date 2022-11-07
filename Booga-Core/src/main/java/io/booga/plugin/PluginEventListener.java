package io.booga.plugin;

import io.booga.plugin.player.Extension;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class PluginEventListener implements Listener {

    public static String[][] newcomerBookPagesTemplate = new String[][]{
            new String[]{
                    "Hello, <player_name>!",
                    "Welcome to BoogaMC!",
                    "",
                    "This guide will teach",
                    "you about Booga, what",
                    "to do in Booga, and",
                    "how you can survive.",
                    "",
                    "Booga is a dangerous",
                    "game, where the Gods",
                    "must be satisfied if",
                    "there is to be peace."
            },
            new String[]{
                    "There are 2 modes:",
                    "peace and war",
                    "",
                    "War is dangerous.",
                    "Griefing and PvP is",
                    "enabled. You'll also",
                    "be cursed by the",
                    "Gods every 5 minutes.",
                    "",
                    "Peace time is self",
                    "explanatory. Griefing",
                    "and PvP are disabled."
            },
            new String[]{
                    "Peace time lasts as",
                    "long as players",
                    "offer various items",
                    "the Gods. Each item",
                    "contributes a different",
                    "amount of time.",
                    "",
                    "If no-one offers items",
                    "to the Gods, peace time",
                    "will expire and then",
                    "war time will begin.",
            },
            new String[]{
                    "At the end of war",
                    "time, the Gods will",
                    "calm themselves, and",
                    "peace time will begin."
            },
            new String[]{
                    "Contribution reward:",
                    "2 minutes per Emerald",
                    "1 minutes per Diamond",
                    "10 seconds per Gold Ingot",
                    "5 seconds per Iron Ingot",
                    "200 milliseconds per these:",
                    "wheat, carrot, potato, redstone",
                    "",
                    "An additional 3 blessings",
                    "will be acquired at random.",
            },
            new String[]{
                    "In Booga, you can",
                    "claim world-chunks",
                    "from the wilderness",
                    "as protected land.",
                    "",
                    "Each chunk costs",
                    "$1,000. You can own",
                    "a maximum of",
                    "9 chunks.",
                    "",
                    "You earn $10 for",
                    "every monster slain."
            },
            new String[]{
                    "Basic instructions on claiming land:",
                    "",
                    "I recommend visualizing chunk borders",
                    "by pressing G while holding F3. (F3 + G)",
                    "",
                    "There are two types of land-claims.",
                    "Personal chunks can be claimed with",
                    "'/booga claim'",
                    "",
                    "Tribe claims can be claimed using",
                    "'/booga tribe claim'",
            },
            new String[]{
                    "You can unclaim land by using",
                    "'/booga tribe unclaim' or alternatively",
                    "'/booga tribe unclaim all' to unclaim all",
                    "of your tribe's land.",
                    "",
                    "You can create a tribe by using",
                    "'/booga tribe create <tribe_name>'",
            },
            new String[]{
                    "You must use '_' instead of spaces",
                    "for tribe names.",
                    "",
                    "Tribes can claim chunks up to",
                    "(9 x total tribe members)",
                    "",
                    "You can invite online players to your",
                    "tribe by using the following command:",
                    "/booga tribe invite <player_name>",
            },
            new String[]{
                    "If a chief leaves their tribe, it will be",
                    "disbanded unless there is another member",
                    "to take their place.",
                    "",
                    "Remember, only the tribe's chief can invite",
                    "other members, and manage tribe land.",
                    "",
                    "You may learn more about your tribe using",
                    "'/booga tribe info'"
            },
            new String[]{
                    "For more info on commands,",
                    "use the command '/booga help'."
            }
    };

    public static ItemStack getNewcomerBook(Player p) {
        List<String> pages = new ArrayList<String>();
        for (int pageNum = 0; pageNum < newcomerBookPagesTemplate.length; pageNum++) {
            String page = "";
            for (int lineNum = 0; lineNum < newcomerBookPagesTemplate[pageNum].length; lineNum++) {
                page += newcomerBookPagesTemplate[pageNum][lineNum] + "\n";
            }
            page = page.replaceAll("<player_name>", p.getName());
            pages.add(page);
        }

        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
        bookMeta.setTitle("Booga Guide");
        bookMeta.setAuthor("SantaScape");
        bookMeta.setPages(pages);
        writtenBook.setItemMeta(bookMeta);

        return writtenBook;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = e.getEntity().getKiller();

        if(victim != null) {
            if (killer != null) {
                long playerKills = BoogaCore.getPlugin().getDataConfig().getLong("players." + killer.getUniqueId() + ".player-kills");
                playerKills++;

                BoogaCore.getPlugin().getDataConfig().set("players." + killer.getUniqueId() + ".player-kills", playerKills);
            }

            long deaths = BoogaCore.getPlugin().getDataConfig().getLong("players." + victim.getUniqueId() + ".deaths");
            deaths++;

            BoogaCore.getPlugin().getDataConfig().set("players." + victim.getUniqueId() + ".deaths", deaths);
        }
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent e) {
        LivingEntity ent = e.getEntity();
        Player killer = ent.getKiller();

        boolean vaultEnabled = BoogaCore.getEconomy() != null && BoogaCore.getPlugin().getConfig().getBoolean("economy.vault.enabled");

        if (killer != null) {
            if (e.getEntity() instanceof Monster) {
                if(vaultEnabled)
                    BoogaCore.getEconomy().depositPlayer(killer, BoogaCore.getPlugin().getConfig().getDouble("economy.vault.mob-kill-deposit"));

                long mobKills = BoogaCore.getPlugin().getDataConfig().getLong("players." + killer.getUniqueId() + ".mob-kills");
                mobKills++;

                BoogaCore.getPlugin().getDataConfig().set("players." + killer.getUniqueId() + ".mob-kills", mobKills);
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Extension es = BoogaCore.getPlayerExtensions().get(p);
        try {
            BoogaCore.plugin.getDataConfig().set("players." + p.getUniqueId() + ".friends", es.friends);
        } catch(Exception ex) {
            BoogaCore.getPlayerExtensions().put(p, new Extension());
            es = BoogaCore.getPlayerExtensions().get(p);
            BoogaCore.plugin.getDataConfig().set("players." + p.getUniqueId() + ".friends", es.friends);
        }

        BoogaCore.getPlayerExtensions().remove(p);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (!BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId())) {
            // Player core
            BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".total-claims", 0);
            BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".join-time", new Date().getTime());
            BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".friends", new ArrayList<String>());
            // Player statistics
            BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".player-kills", 0L);
            BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".mob-kills", 0L);
            BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".deaths", 0L);

            BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".first-name", p.getDisplayName());
            BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".previous-name", p.getDisplayName());
            BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".most-recent-name", p.getDisplayName());

            p.getInventory().addItem(getNewcomerBook(p));

            p.sendMessage("§eWelcome to BoogaMC!");
        } else {
            p.sendMessage("§eWelcome back to BoogaMC!");

            if(!BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId() + ".first-name"))
                BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".first-name", p.getDisplayName());

            if(!BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId() + ".previous-name"))
                BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".previous-name", p.getDisplayName());

            if(!BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId() + ".most-recent-name"))
                BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".most-recent-name", p.getDisplayName());

            if(p.getDisplayName() != BoogaCore.getPlugin().getDataConfig().getString("players." + p.getUniqueId() + ".first-name")
                    && p.getDisplayName() != BoogaCore.getPlugin().getDataConfig().getString("players." + p.getUniqueId() + ".previous-name"))
                BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".previous-name",
                        BoogaCore.getPlugin().getDataConfig().getString("players." + p.getUniqueId() + ".most-recent-name"));

            if(p.getDisplayName() != BoogaCore.getPlugin().getDataConfig().getString("players." + p.getUniqueId() + ".most-recent-name"))
                BoogaCore.getPlugin().getDataConfig().set("players." + p.getUniqueId() + ".most-recent-name", p.getDisplayName());
        }

        BoogaCore.getPlayerExtensions().put(p, new Extension());
        if (BoogaCore.getPlugin().getDataConfig().contains("players." + p.getUniqueId() + ".friends")) {
            ArrayList<String> friends = (ArrayList<String>) BoogaCore.getPlugin().getDataConfig().getStringList("players." + p.getUniqueId() + ".friends");
            BoogaCore.getPlayerExtensions().get(p).friends = friends;
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        EntityDamageEvent.DamageCause cause = e.getCause();
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            if (!e.getDamager().isOp()) {
                if (BoogaCore.plugin.getConfig().getString("game-settings.current-mode").equalsIgnoreCase("PEACE")) {
                    e.setDamage(0.0);
                    e.setCancelled(true);
                    damager.sendMessage("§cYou cannot attack anyone during peace time.");
                }
            }

            if (e.getEntity() instanceof Player) {
                Player en = (Player) e.getEntity();
                if (BoogaCore.getPlayerExtensions().get(damager).friends.contains(en.getUniqueId().toString())) {
                    e.setDamage(0.0);
                    e.setCancelled(true);
                    damager.sendMessage("§cYou cannot attack your friends!");
                }

                if(BoogaCore.getPlugin().getDataConfig().contains("players." + damager.getUniqueId() + ".tribe")) {
                    String tribeName = BoogaCore.getPlugin().getDataConfig().getString("players." + damager.getUniqueId() + ".tribe");

                    for (String uuid : BoogaCore.getPlugin().getDataConfig().getStringList("tribes." + tribeName + ".members")) {
                        if(uuid.equals(en.getUniqueId())) {
                            e.setDamage(0.0);
                            e.setCancelled(true);
                            damager.sendMessage("§cYou cannot attack your fellow tribe members!");
                        }
                    }
                }
            }
        }
    }
}
