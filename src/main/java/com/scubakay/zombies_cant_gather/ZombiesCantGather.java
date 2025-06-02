package com.scubakay.zombies_cant_gather;

import com.scubakay.ScubasServerTools;
import com.scubakay.zombies_cant_gather.datagen.ZCGLanguageProvider;
import com.scubakay.zombies_cant_gather.datagen.ZCGTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ZombiesCantGather implements DataGeneratorEntrypoint {
    public static final TagKey<Item> ZOMBIES_CANT_GATHER = TagKey.of(Registries.ITEM.getKey(), Identifier.of(ScubasServerTools.MOD_ID, "zombies_cant_gather"));

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(ZCGTagProvider::new);
        pack.addProvider(ZCGLanguageProvider::new);
    }
}
