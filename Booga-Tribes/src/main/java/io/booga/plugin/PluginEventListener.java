package io.booga.plugin;

import io.booga.plugin.player.Extension;
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
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public class PluginEventListener implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        if (e.getPlayer() != null) {
            if (BoogaCore.getPlugin().getConfig().getString("game-settings.current-mode").equalsIgnoreCase("PEACE")) {
                Block block = e.getClickedBlock();

                if (block == null)
                    return;

                String local = new StringBuilder().append(block.getChunk().getX())
                        .append("_")
                        .append(block.getChunk().getZ())
                        .toString();

                if(BoogaCore.getPlugin().getDataConfig().contains("chunks." + local + ".owner"))
                    if(!BoogaCore.getPlugin().getDataConfig().getString("chunks." + local + ".owner").startsWith("tribes."))
                        return;

                if (BoogaCore.getPlugin().getDataConfig().contains("chunks." + local)) {
                    String uuid = BoogaCore.getPlugin().getDataConfig().getString("chunks." + local + ".owner");

                    if (e.getPlayer().isOp()) {
                        return;
                    }

                    String pTribe = "tribes." + BoogaCore.getPlugin().getDataConfig().getString("players." + e.getPlayer().getUniqueId() + ".tribe");
                    String chunkOwner = BoogaCore.getPlugin().getDataConfig().getString("chunks." + local + ".owner");

                    if (!e.getPlayer().isOp() && !chunkOwner.equals(pTribe)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage("§cYou are not allowed to interact with anything here!");
                        return;
                    } else if (e.getPlayer().isOp()) {
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        if (e.getPlayer() != null) {
            if (BoogaCore.getPlugin().getConfig().getString("game-settings.current-mode").equalsIgnoreCase("PEACE")) {
                Block block = e.getBlock();
                if (block == null)
                    return;

                String local = new StringBuilder().append(block.getChunk().getX())
                        .append("_")
                        .append(block.getChunk().getZ())
                        .toString();

                if(BoogaCore.getPlugin().getDataConfig().contains("chunks." + local + ".owner"))
                    if(!BoogaCore.getPlugin().getDataConfig().getString("chunks." + local + ".owner").startsWith("tribes."))
                        return;

                if (BoogaCore.getPlugin().getDataConfig().contains("chunks." + local)) {
                    String uuid = BoogaCore.getPlugin().getDataConfig().getString("chunks." + local + ".owner");

                    if (!e.getPlayer().isOp() && uuid.equalsIgnoreCase("server")) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage("§cYou are not allowed to place anything here!");
                        return;
                    } else if (e.getPlayer().isOp()) {
                        return;
                    }

                    String pTribe = "tribes." + BoogaCore.getPlugin().getDataConfig().getString("players." + e.getPlayer().getUniqueId() + ".tribe");
                    String chunkOwner = BoogaCore.getPlugin().getDataConfig().getString("chunks." + local + ".owner");

                    if (!e.getPlayer().isOp() && !chunkOwner.equals(pTribe)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage("§cYou are not allowed to place anything here!");
                        return;
                    } else if (e.getPlayer().isOp()) {
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        if (e.getPlayer() != null) {
            if (BoogaCore.getPlugin().getConfig().getString("game-settings.current-mode").equalsIgnoreCase("PEACE")) {
                Block block = e.getBlock();

                if (block == null)
                    return;

                String local = new StringBuilder().append(block.getChunk().getX())
                        .append("_")
                        .append(block.getChunk().getZ())
                        .toString();

                if(BoogaCore.getPlugin().getDataConfig().contains("chunks." + local + ".owner"))
                    if(!BoogaCore.getPlugin().getDataConfig().getString("chunks." + local + ".owner").startsWith("tribes."))
                        return;

                if (BoogaCore.getPlugin().getDataConfig().contains("chunks." + local)) {
                    String uuid = BoogaCore.getPlugin().getDataConfig().getString("chunks." + local + ".owner");

                    if (!e.getPlayer().isOp() && uuid.equalsIgnoreCase("server")) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage("§cYou are not allowed to break anything here!");
                        return;
                    } else if (e.getPlayer().isOp()) {
                        return;
                    }

                    String pTribe = "tribes." + BoogaCore.getPlugin().getDataConfig().getString("players." + e.getPlayer().getUniqueId() + ".tribe");
                    String chunkOwner = BoogaCore.getPlugin().getDataConfig().getString("chunks." + local + ".owner");

                    if (!e.getPlayer().isOp() && !chunkOwner.equals(pTribe)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage("§cYou are not allowed to break anything here!");
                        return;
                    } else if (e.getPlayer().isOp()) {
                        return;
                    }
                }
            }
        }
    }

    /*@EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplodeEvent(EntityExplodeEvent e) {
        if (BoogaCore.getPlugin().getConfig().getString("game-settings.current-mode").equalsIgnoreCase("PEACE")) {
            String global = new StringBuilder().append(e.getLocation().getChunk().getX())
                    .append("_")
                    .append(e.getLocation().getChunk().getZ()).toString();

            boolean explosivesDisabled = BoogaCore.getPlugin().getDataConfig().getBoolean("chunks." + global + ".tnt-disabled");
            if (explosivesDisabled) {
                e.blockList().clear();
                e.setCancelled(true);
                e.setYield(0.0f);
                return;
            }

            for (Iterator<Block> iterator = e.blockList().iterator(); iterator.hasNext(); ) {
                Block block = iterator.next();

                String local = new StringBuilder().append(block.getChunk().getX())
                        .append("_")
                        .append(block.getChunk().getZ()).toString();

                if (BoogaCore.getPlugin().getDataConfig().contains("chunks." + local)) {
                    if (BoogaCore.getPlugin().getDataConfig().getBoolean("chunks." + local + ".tnt-disabled")) {
                        iterator.remove();
                    }
                }
            }
        }
    }*/
}
