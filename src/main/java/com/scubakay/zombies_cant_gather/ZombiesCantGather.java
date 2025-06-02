package com.scubakay.zombies_cant_gather;

import com.scubakay.ScubasServerTools;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ZombiesCantGather implements DataGeneratorEntrypoint {
    public static final TagKey<Item> ZOMBIES_CANT_GATHER = TagKey.of(Registries.ITEM.getKey(), Identifier.of(ScubasServerTools.MOD_ID, "zombies_cant_gather"));

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(FabricDocsReferenceItemTagProvider::new);
    }

    public class FabricDocsReferenceItemTagProvider extends FabricTagProvider<Item> {
        public FabricDocsReferenceItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.ITEM, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(ZOMBIES_CANT_GATHER)
                .add(Items.GLOW_INK_SAC);
        }
    }
}
