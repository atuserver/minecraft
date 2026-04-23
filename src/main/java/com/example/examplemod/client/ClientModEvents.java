package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.registry.ModEntities;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.LUNA_VALDIS.get(), LunaValdisRenderer::new);
        event.registerEntityRenderer(ModEntities.RUNE_ARCHER.get(), SkeletonRenderer::new);
        event.registerEntityRenderer(ModEntities.LAMP_SLIME.get(), SlimeRenderer::new);
        event.registerEntityRenderer(ModEntities.TOMB_GUARD.get(), ZombieRenderer::new);
        event.registerEntityRenderer(ModEntities.VICE_KNIGHT.get(), HuskRenderer::new);
    }
}
