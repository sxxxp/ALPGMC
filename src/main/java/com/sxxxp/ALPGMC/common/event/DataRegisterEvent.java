package com.sxxxp.ALPGMC.common.event;

import com.sxxxp.ALPGMC.core.data.MobXpManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataRegisterEvent {

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {

        event.addListener(MobXpManager.INSTANCE);

        System.out.println("[ALPGMC] Registered MobXpManager to Minecraft Resource Reload Listener.");
    }
}