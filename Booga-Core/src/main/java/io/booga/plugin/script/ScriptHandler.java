package io.booga.plugin.script;

import io.booga.plugin.BoogaCore;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class ScriptHandler {
    private static final Map<String, Script> scripts = new HashMap<>();

    public static Map<String, Script> getScripts() {
        return scripts;
    }

    public static void processor() {
        Bukkit.getScheduler().runTaskAsynchronously(BoogaCore.plugin, new Runnable() {
            @Override
            public void run() {
                for (Script s : getScripts().values()) {
                    s.run();
                }

                while (BoogaCore.plugin.isEnabled()) {
                    for (Script s : getScripts().values()) {
                        s.process();
                    }
                }
            }
        });
    }

    public static void shutdown() {
        for (Script s : getScripts().values()) {
            s.stop();
        }
        getScripts().clear();
    }
}
