package com.scubakay.zombies_cant_gather.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

//? if >=1.20.5 {
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;
//?}

import static com.scubakay.ScubasServerTools.MOD_ID;

public class ZCGLanguageProvider extends FabricLanguageProvider {
//? if >=1.20.5 {
    public ZCGLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(getTagKey("zombies_cant_gather"), "Zombies Can't Gather");
    }
//?} else {
    /*public ZCGLanguageProvider(FabricDataOutput dataOutput) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(getTagKey("zombies_cant_gather"), "Zombies Can't Gather");
    }
*///?}

    private String getTagKey(String tagID) {
        return String.format("tag.item.%s.%s", MOD_ID, tagID);
    }
}