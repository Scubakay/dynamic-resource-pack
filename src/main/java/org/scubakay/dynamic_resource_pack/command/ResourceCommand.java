package org.scubakay.dynamic_resource_pack.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.scubakay.dynamic_resource_pack.util.ConfigFileHandler;

import java.util.Objects;

public class ResourceCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess ignoredCommandRegistryAccess, CommandManager.RegistrationEnvironment ignoredRegistrationEnvironment) {
        dispatcher.register(CommandManager
            .literal("resourcepack")
            .executes(ResourceCommand::reloadResourcepack)
        );
    }

    public static int reloadResourcepack(CommandContext<ServerCommandSource> context) {
        final ServerPlayNetworkHandler handler = Objects.requireNonNull(context.getSource().getPlayer()).networkHandler;
        MinecraftServer server = context.getSource()./*? if >=1.21.9 { */getWorld()./*? } */getServer();
        handler.sendPacket(ConfigFileHandler.getInstance(server).getResourcePackSendS2CPacket());
        return Command.SINGLE_SUCCESS;
    }
}
