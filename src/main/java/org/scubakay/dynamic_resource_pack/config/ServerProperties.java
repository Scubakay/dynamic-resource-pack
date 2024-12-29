package org.scubakay.dynamic_resource_pack.config;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.scubakay.dynamic_resource_pack.DynamicResourcePack;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ServerProperties {
    public ConfigEntry<String> id;
    public ConfigEntry<String> url;
    public ConfigEntry<String> hash;
    public ConfigEntry<Boolean> required;
    public ConfigEntry<String> prompt;

    public ServerProperties(ConfigBuilder builder) {
        id = builder.stringEntry("resource-pack-id", "");
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

    public UUID getUUID() {
        if (this.id.get().isEmpty()) {
            return UUID.nameUUIDFromBytes(this.url.get().getBytes(StandardCharsets.UTF_8));
        } else {
            return UUID.fromString(this.id.get());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != ServerProperties.class) return false;

        ServerProperties other = (ServerProperties) o;
        return Objects.equals(id.get(), other.id.get())
            && Objects.equals(url.get(), other.url.get())
            && Objects.equals(hash.get(), other.hash.get())
            && Objects.equals(required.get(), other.required.get())
            && Objects.equals(prompt.get(), other.prompt.get());
    }
}