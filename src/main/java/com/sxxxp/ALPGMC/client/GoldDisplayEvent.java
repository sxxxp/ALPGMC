package com.sxxxp.ALPGMC.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GoldDisplayEvent {
    private static final ResourceLocation ICON = new ResourceLocation("alpgmc", "textures/gui/gold.png");
    private static final int ICON_SIZE = 10;
    private static final int TEXTURE_SIZE = 16;
    // 인벤토리 창에서 왼손 칸 오른쪽의 빈 자리입니다.
    private static final int OFFSET_X = 98;
    private static final int OFFSET_Y = 66;

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int x = screen.getGuiLeft() + OFFSET_X;
        int y = screen.getGuiTop() + OFFSET_Y;
        graphics.blit(ICON, x, y, ICON_SIZE, ICON_SIZE, 0.0F, 0.0F, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
        graphics.drawString(Minecraft.getInstance().font, String.valueOf(ClientLevelData.getGold()), x + ICON_SIZE + 2, y + 1, 0x404040, false);
    }
}
