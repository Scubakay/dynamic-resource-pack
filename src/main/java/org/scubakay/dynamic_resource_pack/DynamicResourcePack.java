package org.scubakay.dynamic_resource_pack;

import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.scubakay.dynamic_resource_pack.config.ResourcePackConfig;
import org.scubakay.dynamic_resource_pack.event.CloseConfigFileWatcherEvent;
import org.scubakay.dynamic_resource_pack.event.WatchConfigFileOnServerStartEvent;
import org.scubakay.dynamic_resource_pack.util.ResourcePackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class DynamicResourcePack implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("dynamicresourcepack");
    public static final String MOD_ID = "DynamicResourcePack";
    public static ResourcePackConfig config;

    @Override
    public void onInitialize() {
        loadConfigFile();
        registerEvents();
        ResourcePackHandler.register();
    }

    public static void loadConfigFile() {
        config = ConfigBuilder.builder(ResourcePackConfig::new)
                .path(getModConfigFile())
                .strict(true)
                .saveAfterBuild(false)
                .build();
    }

    private void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(new WatchConfigFileOnServerStartEvent());
        ServerLifecycleEvents.SERVER_STOPPING.register(new CloseConfigFileWatcherEvent());
    }

    public static Path getModConfigFolder() {
        return Path.of(".").resolve("config");
    }
    public static Path getModConfigFile() {
        return getModConfigFolder().resolve("resourcepack.properties");
    }
}
