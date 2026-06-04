package com.sxxxp.ALPGMC.core.init;

import com.sxxxp.ALPGMC.common.entity.CustomZombieEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "alpgmc");

    // "custom_zombie"라는 ID로 몬스터 등록 (히트박스 크기: 가로 0.6, 세로 1.95)
    public static final RegistryObject<EntityType<CustomZombieEntity>> CUSTOM_ZOMBIE =
            ENTITIES.register("custom_zombie",
                    () -> EntityType.Builder.of(CustomZombieEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .build("custom_zombie"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}

