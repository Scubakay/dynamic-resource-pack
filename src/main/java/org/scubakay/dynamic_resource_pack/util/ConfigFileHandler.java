package org.scubakay.dynamic_resource_pack.util;

import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.scubakay.dynamic_resource_pack.DynamicResourcePack;
import org.scubakay.dynamic_resource_pack.config.ServerProperties;

import java.nio.file.Path;

public class ConfigFileHandler {
    private static ConfigFileHandler instance;

    public ServerProperties config;
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
        ServerProperties newConfig = loadConfigFile();
        if (!newConfig.equals(config)) {
            DynamicResourcePack.LOGGER.info("{} has changed, reloading resource pack...", getConfigFile(server).getFileName());
            config = newConfig;

            reloadDatapacks();
            notifyPlayers();
        }
    }

    private void stopConfigFileWatcher() {
        if (watcher != null) {
            watcher.stopThread();
        }
    }

    public ServerProperties loadConfigFile() {
        return ConfigBuilder.builder(ServerProperties::new)
            .path(getConfigFile(server))
            .strict(true)
            .saveAfterBuild(false)
            .keepOrder(true)
            .build();
    }

    /**
     * Just executes the /reload command
     */
    private void reloadDatapacks() {
        CommandManager manager = server.getCommandManager();
        ServerCommandSource source = server.getCommandSource();
        manager.executeWithPrefix(source, "reload");
    }

    private void notifyPlayers() {
        Text message = Text.translatable("dynamicresourcepacks.confighandler.newversionavailable").append(
            Text.translatable("dynamicresourcepacks.confighandler.newversionreload").styled(style -> style.withColor(Formatting.GREEN)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/resourcepack"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("dynamicresourcepacks.confighandler.newversiontooltip")))
            ));

        server.getPlayerManager().broadcast(message, false);
    }

    public Path getConfigDirectory(MinecraftServer server) {
        return server.getRunDirectory();
    }
    public Path getConfigFile(MinecraftServer server) {
        return getConfigDirectory(server).resolve("server.properties");
    }
}
