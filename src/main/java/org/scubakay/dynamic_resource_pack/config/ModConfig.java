package org.scubakay.dynamic_resource_pack.config;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;

public class ModConfig {
    public ConfigEntry<String> reloadResourcePackMessage;
    public ConfigEntry<String> reloadResourcePackAction;
    public ConfigEntry<String> reloadResourcePackTooltip;

    public ModConfig(ConfigBuilder builder) {
        reloadResourcePackMessage = builder.entry("reload-resourcepack-message", "A new version of the server resource pack is available: ").comment("The message sent to players notifying them of a new version");
        reloadResourcePackAction = builder.entry("reload-resourcepack-action", "[Reload]").comment("The text of the reload 'button' in the notification message");
        reloadResourcePackTooltip = builder.entry("reload-resourcepack-tooltip", "Reload the server resource pack").comment("The tooltip on the reload 'button' in the notification message");
    }
}
