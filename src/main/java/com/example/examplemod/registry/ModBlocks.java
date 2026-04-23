package com.example.examplemod.registry;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExampleMod.MOD_ID);

    public static final RegistryObject<Block> MOONLAMP_PILLAR = BLOCKS.register("moonlamp_pillar",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(4.0f, 8.0f).sound(SoundType.METAL).lightLevel(s -> 11)));

    public static final RegistryObject<Item> MOONLAMP_PILLAR_ITEM = ModItems.ITEMS.register("moonlamp_pillar",
            () -> new BlockItem(MOONLAMP_PILLAR.get(), new Item.Properties()));
}
