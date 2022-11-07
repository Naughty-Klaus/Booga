package io.booga.plugin.script.impl;

import io.booga.plugin.BoogaCore;
import io.booga.plugin.BoogaGame;
import io.booga.plugin.script.Script;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Date;

public class LoopedLogic extends Script {

    public Date date;

    ///summon lightning_bolt 279.0 71 -232.0
    @Override
    public void run() {
        date = new Date();
        switch (BoogaCore.getPlugin().getConfig().getString("game-settings.current-mode")) {
            case "WAR":
                long warTime = BoogaCore.getPlugin().getConfig().getLong("game-settings.war-time");
                long currentTime = date.getTime();

                if (warTime <= currentTime) {
                    long gracePeriod = BoogaCore.getPlugin().getConfig().getLong("timers.grace-period");
                    long graceTime = currentTime + gracePeriod;

                    BoogaCore.getPlugin().getConfig().set("game-settings.current-mode", "PEACE");
                    BoogaCore.getPlugin().getConfig().set("game-settings.peace-time", graceTime);

                    BoogaCore.getPlugin().getConfig().set("game-settings.war-time", (long) 0);

                    try {
                        World w = Bukkit.getWorld("world");
                        Location locale = new Location(w, 279.0, 71, -232.0);
                        w.strikeLightningEffect(locale);
                    } catch (Exception e) {
                    }

                    for (Player p : BoogaGame.getPlugin().getServer().getOnlinePlayers()) {
                        p.sendMessage("§eThe Gods: §9Our anger has subsided.. for now.");
                    }
                }
                break;
            case "PEACE":
                long peaceTime = BoogaCore.getPlugin().getConfig().getLong("game-settings.peace-time");
                currentTime = date.getTime();

                if (peaceTime <= currentTime) {
                    long warPeriod = BoogaCore.getPlugin().getConfig().getLong("timers.war-period");
                    warTime = currentTime + warPeriod;

                    BoogaCore.getPlugin().getConfig().set("game-settings.current-mode", "WAR");
                    BoogaCore.getPlugin().getConfig().set("game-settings.war-time", warTime);

                    BoogaCore.getPlugin().getConfig().set("game-settings.peace-time", (long) 0);

                    try {
                        World w = Bukkit.getWorld("world");
                        Location locale = new Location(w, 279.0, 71, -232.0);
                        w.strikeLightningEffect(locale);
                    } catch (Exception e) {
                    }

                    for (Player p : BoogaGame.plugin.getServer().getOnlinePlayers()) {
                        p.sendMessage("§eThe Gods: §cYou have angered us, so we curse you!");
                        p.sendMessage("§eThe Gods: §cWe no longer offer you protection from others.");
                    }
                }
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public void process() {
        run();
    }
}
