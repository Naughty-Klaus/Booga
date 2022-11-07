package io.booga.plugin;

import io.booga.plugin.command.impl.*;
import io.booga.plugin.script.ScriptHandler;
import io.booga.plugin.script.impl.LoopedLogic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class BoogaGame extends JavaPlugin {

    public static final PotionEffectType[] curses = new PotionEffectType[]{
            PotionEffectType.UNLUCK, PotionEffectType.HUNGER,
            PotionEffectType.SLOW, PotionEffectType.WEAKNESS,
            PotionEffectType.POISON, PotionEffectType.GLOWING,
            PotionEffectType.SLOW_DIGGING, PotionEffectType.DARKNESS
    };

    public static final PotionEffectType[] boosts = new PotionEffectType[]{
            PotionEffectType.FAST_DIGGING, PotionEffectType.NIGHT_VISION,
            PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.REGENERATION,
            PotionEffectType.WATER_BREATHING, PotionEffectType.SPEED,
            PotionEffectType.LUCK, PotionEffectType.INCREASE_DAMAGE
    };

    public static BoogaGame plugin;

    public static BoogaGame getPlugin() {
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

        BoogaCore.getCommands().put("status", Status.class);
        BoogaCore.getCommands().put("offer", Offer.class);
        BoogaCore.getCommands().put("help", Help.class);
        BoogaCore.getCommands().put("declare", Declare.class);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PluginEventListener(), plugin);
        activate();
    }

    @Override
    public void onDisable() {}

    public void activate() {
        ScriptHandler.getScripts().put("LoopedLogic", new LoopedLogic());

        getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (plugin.isEnabled()) {
                if (BoogaCore.getPlugin().getConfig().getString("game-settings.current-mode").equalsIgnoreCase("WAR")) {
                    getServer().getOnlinePlayers().forEach(player -> {

                        PotionEffectType[] effects = new PotionEffectType[]{
                                curses[ThreadLocalRandom.current().nextInt(curses.length)],
                                curses[ThreadLocalRandom.current().nextInt(curses.length)],
                                curses[ThreadLocalRandom.current().nextInt(curses.length)]
                        };

                        for (PotionEffectType type : effects) {
                            player.addPotionEffect(new PotionEffect(type, 3000, 0));
                            player.sendMessage("The Gods have cursed you with " + type.getName().toLowerCase() + ".");
                        }

                        effects = null;
                    });
                }
            }
        }, 0l, 12000);
    }

    public FileConfiguration getDataConfig() {
        return BoogaCore.getPlugin().getDataConfig();
    }
}
