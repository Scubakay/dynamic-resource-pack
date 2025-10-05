package org.scubakay.dynamic_resource_pack.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.scubakay.dynamic_resource_pack.DynamicResourcePack;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Responsible for keeping track of the ServerProperties file
 */
public class ConfigFileHandler {
    private static ConfigFileHandler instance;

    private final MinecraftServer server;
    private ConfigFileWatcher watcher;
    private final Path file;
    private MinecraftServer.ServerResourcePackProperties packProperties;
    private boolean started;

    private ConfigFileHandler(MinecraftServer server) {
        this.server = server;
        this.file = getConfigFile(server);
    }

    public static void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register((MinecraftServer server) ->
                getInstance(server).startConfigFileWatcher());
        ServerLifecycleEvents.SERVER_STOPPING.register((MinecraftServer server) ->
                getInstance(server).stopConfigFileWatcher());
    }

    public ResourcePackSendS2CPacket getResourcePackSendS2CPacket() {
        return new ResourcePackSendS2CPacket(
                this.packProperties.id(),
                this.packProperties.url(),
                this.packProperties.hash(),
                this.packProperties.isRequired(),
                Optional.ofNullable(this.packProperties.prompt())
        );
    }

    public static ConfigFileHandler getInstance(MinecraftServer server) {
        if (instance == null) {
            instance = new ConfigFileHandler(server);
        }
        return instance;
    }

    private void startConfigFileWatcher() {
        onConfigFileChange();
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
        ServerPropertiesHandler.load(file).serverResourcePackProperties.ifPresentOrElse(prop -> {
            this.packProperties = prop;
            if (!started) {
                started = true;
            } else {
                if (DynamicResourcePack.modConfig.runReloadOnResourcePackUpdate.get()) {
                    reloadDatapacks();
                }
                notifyPlayers();
            }
        }, () -> DynamicResourcePack.LOGGER.error("Something went wrong trying to load the new server pack properties"));
    }

    private void stopConfigFileWatcher() {
        if (watcher != null) {
            watcher.stopThread();
        }
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
                                //? if >= 1.21.5 {
                                .withClickEvent(new ClickEvent.RunCommand("/resourcepack"))
                                .withHoverEvent(new HoverEvent.ShowText(Text.literal(DynamicResourcePack.modConfig.reloadResourcePackTooltip.get())))
                        //?} else {
                /*.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/resourcepack"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(DynamicResourcePack.modConfig.reloadResourcePackTooltip.get())))
                *///?}
                ));
        server.getPlayerManager().broadcast(message, false);
    }

    public static Path getConfigDirectory(MinecraftServer server) {
        return server.getRunDirectory();
    }

    public static Path getConfigFile(MinecraftServer server) {
        return getConfigDirectory(server).resolve("server.properties");
    }

    /**
     * Returns the current ServerResourcePackProperties, or null if not loaded yet.
     */
    public static MinecraftServer.ServerResourcePackProperties getResourcePackProperties(MinecraftServer server) {
        ConfigFileHandler handler = getInstance(server);
        return handler.packProperties;
    }
}
