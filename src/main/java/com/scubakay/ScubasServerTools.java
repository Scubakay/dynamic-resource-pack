package com.scubakay;

import com.scubakay.config.ModConfig;
import com.scubakay.dynamic_resource_pack.DynamicResourcePack;
import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class ScubasServerTools implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "DynamicResourcePack";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String VERSION = /*$ mod_version*/ "0.2.0";

    public static ModConfig modConfig;


    @Override
    public void onInitialize() {
        //? if !release
        /*LOGGER.warn("I'm still a template!");*/

        //? if fapi: <0.95 {
        /*LOGGER.info("Fabric API is old on this version");
        LOGGER.info("Please update!");
        *///?}
        modConfig = ConfigBuilder.builder(ModConfig::new)
                .path(getConfigFile())
                .strict(true)
                .saveAfterBuild(true)
                .build();

        DynamicResourcePack.initialize();
    }

    public Path getConfigDirectory() {
        return Path.of(".").resolve("config").resolve(MOD_ID);
    }
    public Path getConfigFile() {
        return getConfigDirectory().resolve("mod.properties");
    }
}