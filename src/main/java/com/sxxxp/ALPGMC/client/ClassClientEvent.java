package com.sxxxp.ALPGMC.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClassClientEvent {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !ClientClassData.isSynced()) return;

        if (ClientClassData.get() != null) {
            // 서버가 직업 저장을 확인해 주면 선택 화면을 닫습니다.
            if (mc.screen instanceof ClassSelectScreen) {
                mc.setScreen(null);
            }
            while (ModKeys.OPEN_STATS.consumeClick()) {
                if (mc.screen == null) {
                    mc.setScreen(new StatScreen());
                }
            }
        } else if (mc.screen == null) {
            // 직업이 없으면 다른 화면이 닫힐 때마다 선택 화면을 다시 엽니다.
            mc.setScreen(new ClassSelectScreen());
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientClassData.reset();
    }
}
