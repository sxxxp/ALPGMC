package com.sxxxp.ALPGMC.core.init;

import com.sxxxp.ALPGMC.common.entity.CustomZombieEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAttributeEvent {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityInit.CUSTOM_ZOMBIE.get(), CustomZombieEntity.createAttributes().build());
    }
}