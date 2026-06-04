package com.sxxxp.ALPGMC.client;

import com.sxxxp.ALPGMC.client.renderer.CustomZombieRenderer;
import com.sxxxp.ALPGMC.core.init.EntityInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEntityEvent {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.CUSTOM_ZOMBIE.get(), CustomZombieRenderer::new);
    }
}