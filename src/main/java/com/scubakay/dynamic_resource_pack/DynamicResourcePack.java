package com.scubakay.dynamic_resource_pack;

import com.scubakay.dynamic_resource_pack.command.ResourceCommand;
import com.scubakay.dynamic_resource_pack.util.ConfigFileHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class DynamicResourcePack {
    public static void initialize() {
        ConfigFileHandler.registerEvents();
        CommandRegistrationCallback.EVENT.register(ResourceCommand::register);
    }
}
