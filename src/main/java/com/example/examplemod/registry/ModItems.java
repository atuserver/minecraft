package com.example.examplemod.registry;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MOD_ID);

    public static final RegistryObject<Item> LUNA_VALDIS_SPAWN_EGG = ITEMS.register("luna_valdis_spawn_egg",
            () -> new SpawnEggItem(ModEntities.LUNA_VALDIS, 0x31446A, 0xAAB7D4, new Item.Properties()));
    public static final RegistryObject<Item> RUNE_ARCHER_SPAWN_EGG = ITEMS.register("rune_archer_spawn_egg",
            () -> new SpawnEggItem(ModEntities.RUNE_ARCHER, 0x2A2F45, 0x8AB8FF, new Item.Properties()));
    public static final RegistryObject<Item> LAMP_SLIME_SPAWN_EGG = ITEMS.register("lamp_slime_spawn_egg",
            () -> new SpawnEggItem(ModEntities.LAMP_SLIME, 0x55D8D3, 0x1B8A88, new Item.Properties()));
    public static final RegistryObject<Item> TOMB_GUARD_SPAWN_EGG = ITEMS.register("tomb_guard_spawn_egg",
            () -> new SpawnEggItem(ModEntities.TOMB_GUARD, 0x5A5A63, 0xC7C9D6, new Item.Properties()));
    public static final RegistryObject<Item> VICE_KNIGHT_SPAWN_EGG = ITEMS.register("vice_knight_spawn_egg",
            () -> new SpawnEggItem(ModEntities.VICE_KNIGHT, 0x3F3F59, 0xD2C68E, new Item.Properties()));

    public static final RegistryObject<Item> MOON_SHARD = ITEMS.register("moon_shard",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> LUNA_BLADE = ITEMS.register("luna_blade",
            () -> new SwordItem(Tiers.NETHERITE, 2, -2.2f, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> AZURE_MOON_MANTLE = ITEMS.register("azure_moon_mantle",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    @Mod.EventBusSubscriber(modid = ExampleMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void addCreative(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                event.accept(LUNA_VALDIS_SPAWN_EGG);
                event.accept(RUNE_ARCHER_SPAWN_EGG);
                event.accept(LAMP_SLIME_SPAWN_EGG);
                event.accept(TOMB_GUARD_SPAWN_EGG);
                event.accept(VICE_KNIGHT_SPAWN_EGG);
            }
            if (event.getTabKey() == CreativeModeTabs.COMBAT) {
                event.accept(LUNA_BLADE);
                event.accept(AZURE_MOON_MANTLE);
            }
            if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
                event.accept(MOON_SHARD);
            }
            if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
                event.accept(ModBlocks.MOONLAMP_PILLAR_ITEM);
            }
        }
    }
}
