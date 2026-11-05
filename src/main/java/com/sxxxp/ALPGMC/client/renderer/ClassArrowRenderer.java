package com.sxxxp.ALPGMC.client.renderer;

import com.sxxxp.ALPGMC.common.entity.ClassArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ClassArrowRenderer extends ArrowRenderer<ClassArrowEntity> {
    public ClassArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ClassArrowEntity entity) {
        return new ResourceLocation("alpgmc", "textures/entity/projectiles/" + entity.getTier().getId() + "_arrow.png");
    }
}
