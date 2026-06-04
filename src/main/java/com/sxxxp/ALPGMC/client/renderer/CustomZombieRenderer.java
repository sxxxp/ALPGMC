package com.sxxxp.ALPGMC.client.renderer;

import com.sxxxp.ALPGMC.common.entity.CustomZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;

// 기존 마인크래프트 좀비의 3D 모델(ZombieModel)을 재활용하는 가장 쉬운 방법입니다.
public class CustomZombieRenderer extends MobRenderer<CustomZombieEntity, ZombieModel<CustomZombieEntity>> {

    // 리소스 폴더에 들어갈 몬스터 스킨(텍스처) 경로 지정
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("alpgmc", "textures/entity/custom_zombie.png");

    public CustomZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F); // 0.5F는 그림자 크기
    }

    @Override
    public ResourceLocation getTextureLocation(CustomZombieEntity entity) {
        return TEXTURE;
    }
    }
