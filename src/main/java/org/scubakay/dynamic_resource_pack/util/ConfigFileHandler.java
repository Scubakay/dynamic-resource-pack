package org.scubakay.dynamic_resource_pack.util;

import com.google.common.collect.Lists;
import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandSource;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.SaveProperties;
import org.scubakay.dynamic_resource_pack.DynamicResourcePack;
import org.scubakay.dynamic_resource_pack.config.ResourcePackConfig;

import java.nio.file.Path;
import java.util.Collection;

public class ConfigFileHandler {
    private static ConfigFileHandler instance;

    public ResourcePackConfig config;
    private final MinecraftServer server;
    private ConfigFileWatcher watcher;

    public ConfigFileHandler(MinecraftServer server) {
        this.server = server;
        config = loadConfigFile();
    }

    public MinecraftServer getServer() {
        return server;
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
    public static ConfigFileHandler getInstance() {
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
        ResourcePackConfig newConfig = loadConfigFile();
        if (!newConfig.equals(config)) {
            DynamicResourcePack.LOGGER.info("{} has changed, reloading resource pack...", getConfigFile(server).getFileName());
            config = newConfig;

            reloadDatapacks();
            // TODO: Push after clicking command thingy in chat
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
            .keepOrder(true)
            .build();
    }

    /**
     * Just executes the /reload command
     */
    public void reloadDatapacks() {
        CommandManager manager = server.getCommandManager();
        ServerCommandSource source = server.getCommandSource();
        manager.executeWithPrefix(source, "reload");
    }

    public Path getConfigDirectory(MinecraftServer server) {
        //return server.getRunDirectory().resolve("config");
        return server.getRunDirectory();
    }
    public Path getConfigFile(MinecraftServer server) {
        return getConfigDirectory(server).resolve("server.properties");
    }
}
