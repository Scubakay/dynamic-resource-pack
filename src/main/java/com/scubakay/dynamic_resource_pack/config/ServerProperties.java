package com.scubakay.dynamic_resource_pack.config;

import com.scubakay.ScubasServerTools;
import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import net.minecraft.text.Text;

// if >=1.20.5 {
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
// }

import java.nio.charset.StandardCharsets;
import java.util.Objects;
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

    //? if >=1.20.5 {
    public Optional<Text> getPrompt(@NotNull RegistryWrapper.WrapperLookup registries) {
     //?} else {
    /*public Text getPrompt() {
    *///?}
        String promptString = this.prompt.get();
        if (!promptString.isBlank()) {
            try {
                // This nesting is a bit scuffed, but I think it's the best solution
                //? if >=1.20.5 {
                return Optional.of(Optional.ofNullable(Text.Serialization.fromJson(promptString, registries)).orElseThrow());
                 //?} else if >=1.20.3 {
                /*return Text.Serialization.fromJson(promptString);
                *///?}
            } catch (Exception e) {
                // Need to use concatenation to log the exception
                ScubasServerTools.LOGGER.error("Failed to parse prompt text " + promptString, e);
            }
        }
        //? if >=1.20.5 {
        return Optional.empty();
         //?} else {
        /*return Text.empty();
        *///?}
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