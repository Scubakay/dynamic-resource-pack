package com.scubakay.dynamic_resource_pack.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

/**
 * Handles pushing resource pack versions to players
 */
public class ResourcePackHandler {
    public static void push(ServerPlayNetworkHandler handler, MinecraftServer server) {
        handler.sendPacket(ConfigFileHandler.getInstance().getResourcePackSendS2CPacket());
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
