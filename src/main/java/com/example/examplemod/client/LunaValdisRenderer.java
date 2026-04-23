package com.example.examplemod.client;

import com.example.examplemod.entity.LunaValdisBoss;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class LunaValdisRenderer extends MobRenderer<LunaValdisBoss, ZombieModel<LunaValdisBoss>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/entity/illager/vindicator.png");

    public LunaValdisRenderer(EntityRendererProvider.Context context) {
        super(context, new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.6f);
    }

    @Override
    public ResourceLocation getTextureLocation(LunaValdisBoss entity) {
        return TEXTURE;
    }
}
