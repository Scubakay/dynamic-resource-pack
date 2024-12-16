package org.scubakay.dynamic_resource_pack.util;

import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.scubakay.dynamic_resource_pack.DynamicResourcePack;
import org.scubakay.dynamic_resource_pack.config.ResourcePackConfig;

import java.nio.file.Path;

public class ConfigFileHandler {
    public static ConfigFileHandler instance;

    public ResourcePackConfig config;
    private final MinecraftServer server;
    private ConfigFileWatcher watcher;

    public ConfigFileHandler(MinecraftServer server) {
        this.server = server;
        config = loadConfigFile();
    }

    public static void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server) -> {
            getInstance(server).startConfigFileWatcher();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register((MinecraftServer server) -> {
            getInstance(server).stopConfigFileWatcher();
        });
    }

    public static ConfigFileHandler getInstance(MinecraftServer server) {
        if (instance == null) {
            instance = new ConfigFileHandler(server);
        }
        return instance;
    }

    private void startConfigFileWatcher() {
        if (watcher == null) {
            watcher = new ConfigFileWatcher(
                getConfigDirectory(server),
                getConfigFile(server),
                this::onConfigFileChange
            );
            watcher.setDaemon(true);
            watcher.start();
        }
    }

    private void onConfigFileChange() {
        DynamicResourcePack.LOGGER.info("{} has changed, reloading resource pack...", getConfigFile(server).getFileName());
        ResourcePackConfig newConfig = loadConfigFile();
        if (!newConfig.equals(config)) {
            config = newConfig;
            ResourcePackHandler.pushTo(server);
        }
    }

    private void stopConfigFileWatcher() {
        if (watcher != null) {
            watcher.stopThread();
        }
    }

    public ResourcePackConfig loadConfigFile() {
        return ConfigBuilder.builder(ResourcePackConfig::new)
                .path(getConfigFile(server))
                .strict(true)
                .saveAfterBuild(false)
                .build();
    }

    public Path getConfigDirectory(MinecraftServer server) {
        return server.getRunDirectory();
    }
    public Path getConfigFile(MinecraftServer server) {
        return getConfigDirectory(server).resolve("server.properties");
    }
}
