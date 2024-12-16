package org.scubakay.dynamic_resource_pack.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.scubakay.dynamic_resource_pack.DynamicResourcePack;
import org.scubakay.dynamic_resource_pack.config.ResourcePackConfig;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class ResourcePackHandler {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> push(handler, server));
    }

    public static void push(ServerPlayNetworkHandler handler, MinecraftServer server) {
        ResourcePackConfig config = ConfigFileHandler.getInstance(server).config;
        handler.sendPacket(new ResourcePackSendS2CPacket(
                config.id.get(),
                config.url.get(),
                config.hash.get(),
                config.required.get(),
                config.getPrompt(server.getRegistryManager())
        ));
    }

    public static int pushTo(MinecraftServer server) {
        return pushTo(server.getPlayerManager(), server);
    }
    public static int pushTo(PlayerManager players, MinecraftServer server) {
        return pushTo(players.getPlayerList(), server);
    }
    public static int pushTo(Collection<ServerPlayerEntity> players, MinecraftServer server) {
        for (ServerPlayerEntity player : players) push(player.networkHandler, server);
        return players.size();
    }
}
