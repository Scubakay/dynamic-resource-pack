package org.scubakay.dynamic_resource_pack;

import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import org.scubakay.dynamic_resource_pack.command.ResourceCommand;
import org.scubakay.dynamic_resource_pack.config.ModConfig;
import org.scubakay.dynamic_resource_pack.util.ConfigFileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class DynamicResourcePack implements ModInitializer {
    public static final String MOD_ID = "DynamicResourcePack";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ModConfig modConfig;

    @Override
    public void onInitialize() {
        ConfigFileHandler.registerEvents();
        CommandRegistrationCallback.EVENT.register(ResourceCommand::register);

        modConfig = ConfigBuilder.builder(ModConfig::new)
                .path(getConfigFile())
                .strict(true)
                .saveAfterBuild(true)
                .build();
    }

    public Path getConfigDirectory() {
        return Path.of(".").resolve("config").resolve(MOD_ID);
    }
    public Path getConfigFile() {
        return getConfigDirectory().resolve("mod.properties");
    }
}
