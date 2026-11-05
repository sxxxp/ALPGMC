package com.sxxxp.ALPGMC.client;

import com.sxxxp.ALPGMC.client.renderer.ClassArrowRenderer;
import com.sxxxp.ALPGMC.client.renderer.CustomZombieRenderer;
import com.sxxxp.ALPGMC.core.init.EntityInit;
import com.sxxxp.ALPGMC.core.init.ItemInit;
import com.sxxxp.ALPGMC.core.init.MenuInit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEntityEvent {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.CUSTOM_ZOMBIE.get(), CustomZombieRenderer::new);
        event.registerEntityRenderer(EntityInit.CLASS_ARROW.get(), ClassArrowRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(ModKeys.OPEN_STATS);
    }

    // 활을 당기는 정도에 따라 모델이 바뀌도록 바닐라 활과 같은 속성을 등록합니다.
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(MenuInit.ENHANCEMENT_ANVIL.get(), EnhancementAnvilScreen::new);
            ItemProperties.register(ItemInit.ARCHER_BOW.get(), new ResourceLocation("pull"), (stack, level, entity, seed) -> {
                if (entity == null || entity.getUseItem() != stack) return 0.0F;
                return (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
            });
            ItemProperties.register(ItemInit.ARCHER_BOW.get(), new ResourceLocation("pulling"),
                    (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
        });
    }
}
