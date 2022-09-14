package com.turikhay.mc.mapmodcompanion.spigot;

import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CompanionSpigot extends JavaPlugin implements Listener {
    public static final boolean ENABLE_LOGGING = Boolean.parseBoolean(
            System.getProperty(CompanionSpigot.class.getPackage().getName() + ".debug", "false")
    );

    public static final boolean DISABLE_DEFAULT_WORLD_ID = Boolean.parseBoolean(
            System.getProperty(CompanionSpigot.class.getPackage().getName() + ".defaultId", "false")
    );

    public static final boolean USE_TEXTUAL_WORLD_ID = Boolean.parseBoolean(
            System.getProperty(CompanionSpigot.class.getPackage().getName() + ".useTextualId", "false")
    );

    Handler<?, ?> legacyWorldIdHandler = new WorldIdHandler(this, false);

    List<Handler<?, ?>> handlers = Arrays.asList(
            new XaerosMinimapHandler(this),
            new XaerosWorldMapHandler(this),
            legacyWorldIdHandler,
            new WorldIdHandler(this, true)
    );

    DefaultWorld defaultWorld;

    @Override
    public void onEnable() {
        if (DISABLE_DEFAULT_WORLD_ID) {
            getLogger().info("Plugin will not use default world ID for every world");
            defaultWorld = DefaultWorld.empty();
        } else {
            defaultWorld = DefaultWorld.detectDefaultWorld(this);
        }
        handlers.forEach(handler -> {
            try {
                handler.init();
            } catch (Handler.InitializationException e) {
                if (ENABLE_LOGGING) {
                    getLogger().info(String.format(Locale.ROOT,
                            "\"%s\" channel handler will not be available: %s",
                            handler.channelName, e
                    ));
                    e.printStackTrace();
                }
            }
        });
    }

    Optional<World> getDefaultWorld() {
        return defaultWorld.optional();
    }
}
