package com.example.examplemod.registry;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.LampSlime;
import com.example.examplemod.entity.LunaValdisBoss;
import com.example.examplemod.entity.RuneArcher;
import com.example.examplemod.entity.TombGuard;
import com.example.examplemod.entity.ViceKnight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExampleMod.MOD_ID);

    public static final RegistryObject<EntityType<LunaValdisBoss>> LUNA_VALDIS = ENTITY_TYPES.register("luna_valdis",
            () -> EntityType.Builder.of(LunaValdisBoss::new, MobCategory.MONSTER).sized(0.85f, 2.5f).clientTrackingRange(12).build("luna_valdis"));

    public static final RegistryObject<EntityType<RuneArcher>> RUNE_ARCHER = ENTITY_TYPES.register("rune_archer",
            () -> EntityType.Builder.of(RuneArcher::new, MobCategory.MONSTER).sized(0.6f, 1.99f).clientTrackingRange(10).build("rune_archer"));

    public static final RegistryObject<EntityType<LampSlime>> LAMP_SLIME = ENTITY_TYPES.register("lamp_slime",
            () -> EntityType.Builder.of(LampSlime::new, MobCategory.MONSTER).sized(0.9f, 0.9f).clientTrackingRange(8).build("lamp_slime"));

    public static final RegistryObject<EntityType<TombGuard>> TOMB_GUARD = ENTITY_TYPES.register("tomb_guard",
            () -> EntityType.Builder.of(TombGuard::new, MobCategory.MONSTER).sized(0.7f, 2.1f).clientTrackingRange(10).build("tomb_guard"));

    public static final RegistryObject<EntityType<ViceKnight>> VICE_KNIGHT = ENTITY_TYPES.register("vice_knight",
            () -> EntityType.Builder.of(ViceKnight::new, MobCategory.MONSTER).sized(0.75f, 2.2f).clientTrackingRange(10).build("vice_knight"));

    @Mod.EventBusSubscriber(modid = ExampleMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(LUNA_VALDIS.get(), LunaValdisBoss.createAttributes().build());
            event.put(RUNE_ARCHER.get(), RuneArcher.createAttributes().build());
            event.put(LAMP_SLIME.get(), LampSlime.createAttributes().build());
            event.put(TOMB_GUARD.get(), TombGuard.createAttributes().build());
            event.put(VICE_KNIGHT.get(), ViceKnight.createAttributes().build());
        }
    }
}
