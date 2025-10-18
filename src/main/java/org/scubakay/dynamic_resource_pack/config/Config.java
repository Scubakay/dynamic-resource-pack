package org.scubakay.dynamic_resource_pack.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class Config extends MidnightConfig {
    @Entry() public static String reloadResourcePackMessage = "A new version of the server resource pack is available: ";
    @Entry() public static String reloadResourcePackAction = "[Reload]";
    @Entry() public static String reloadResourcePackTooltip = "Reload the server resource pack";
    @Entry() public static Boolean runReloadOnResourcePackUpdate = true;
}
