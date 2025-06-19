package org.scubakay.dynamic_resource_pack.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.scubakay.dynamic_resource_pack.util.ResourcePackHandler;

public class ResourceCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess ignoredCommandRegistryAccess, CommandManager.RegistrationEnvironment ignoredRegistrationEnvironment) {
        dispatcher.register(CommandManager
            .literal("resourcepack")
            .executes(ResourceCommand::reloadResourcepack)
        );
    }

    public static int reloadResourcepack(CommandContext<ServerCommandSource> context) {
        ResourcePackHandler.push(context.getSource().getPlayer().networkHandler, context.getSource().getServer());
        return 1;
    }
}
