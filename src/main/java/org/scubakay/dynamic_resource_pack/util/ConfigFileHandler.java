package org.scubakay.dynamic_resource_pack.util;

import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
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

/**
 * Responsible for keeping track of the ServerProperties file
 */
public class ConfigFileHandler {
    private static ConfigFileHandler instance;

    public ServerProperties serverProperties;
    private final MinecraftServer server;
    private ConfigFileWatcher watcher;

    public ConfigFileHandler(MinecraftServer server) {
        this.server = server;
        serverProperties = loadServerProperties();

    }

    public static void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server) -> {
            getInstance(server).startConfigFileWatcher();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register((MinecraftServer server) -> {
            getInstance(server).stopConfigFileWatcher();
        });
    }

    public ResourcePackSendS2CPacket getResourcePackSendS2CPacket() {
        return new ResourcePackSendS2CPacket(
                serverProperties.id.get(),
                serverProperties.url.get(),
                serverProperties.hash.get(),
                serverProperties.required.get(),
                serverProperties.getPrompt(server.getRegistryManager())
        );
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
        ServerProperties newConfig = loadServerProperties();
        if (!newConfig.equals(serverProperties)) {
            DynamicResourcePack.LOGGER.info("{} has changed, reloading resource pack...", getConfigFile(server).getFileName());
            serverProperties = newConfig;

            reloadDatapacks();
            notifyPlayers();
        }
    }

    private void stopConfigFileWatcher() {
        if (watcher != null) {
            watcher.stopThread();
        }
    }

    public ServerProperties loadServerProperties() {
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
        Text message = Text.literal(DynamicResourcePack.modConfig.reloadResourcePackMessage.get()).append(
            Text.literal(DynamicResourcePack.modConfig.reloadResourcePackAction.get()).styled(style -> style.withColor(Formatting.GREEN)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/resourcepack"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(DynamicResourcePack.modConfig.reloadResourcePackTooltip.get())))
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
