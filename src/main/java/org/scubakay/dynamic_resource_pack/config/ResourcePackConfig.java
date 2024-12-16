package org.scubakay.dynamic_resource_pack.config;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.scubakay.dynamic_resource_pack.DynamicResourcePack;

import java.util.Optional;
import java.util.UUID;

public class ResourcePackConfig {
    public ConfigEntry<UUID> id;
    public ConfigEntry<String> url;
    public ConfigEntry<String> hash;
    public ConfigEntry<Boolean> required;
    public ConfigEntry<String> prompt;

    public ResourcePackConfig(ConfigBuilder builder) {
        id = builder.entry("resource-pack-id", UUID.randomUUID());
        url = builder.stringEntry("resource-pack", "");
        hash = builder.stringEntry("resource-pack-sha1", "");
        required = builder.booleanEntry("require-resource-pack", false);
        prompt = builder.stringEntry("resource-pack-prompt", "");
    }

    public Optional<Text> getPrompt(@NotNull RegistryWrapper.WrapperLookup registries) {
        String promptString = this.prompt.get();
        if (promptString.isBlank()) {
            return Optional.empty();
        } else {
            try {
                // This nesting is a bit scuffed, but I think it's the best solution
                return Optional.of(
                        Optional.ofNullable(Text.Serialization.fromJson(promptString, registries))
                                .orElseThrow()
                );
            } catch (Exception e) {
                // Need to use concatenation to log the exception
                DynamicResourcePack.LOGGER.error("Failed to parse prompt text " + promptString, e);
                return Optional.empty();
            }
        }
    }
}