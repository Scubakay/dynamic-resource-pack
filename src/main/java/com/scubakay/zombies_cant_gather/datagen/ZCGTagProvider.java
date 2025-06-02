package com.scubakay.zombies_cant_gather.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static com.scubakay.zombies_cant_gather.ZombiesCantGather.ZOMBIES_CANT_GATHER;

public class ZCGTagProvider extends FabricTagProvider<Item> {
    public ZCGTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ZOMBIES_CANT_GATHER)
                .add(Items.GLOW_INK_SAC);
    }
}
