package com.scubakay.zombies_cant_gather;

import com.scubakay.ScubasServerTools;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ZombiesCantGather {
    public static final TagKey<Item> ZOMBIES_CANT_GATHER = TagKey.of(Registries.ITEM.getKey(), Identifier.of(ScubasServerTools.MOD_ID, "zombies_cant_gather"));
}
