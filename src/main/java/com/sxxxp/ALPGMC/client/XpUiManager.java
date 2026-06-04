package com.sxxxp.ALPGMC.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class XpUiManager {

    private static int displayTicks = 0; // 화면에 머무를 타이머 (20틱 = 1초)
    private static int currentXp = 0;
    private static String currentMobName = "";

    // 패킷을 받으면 호출될 메서드
    public static void showNotification(int xp, String mobName) {
        currentXp = xp;
        currentMobName = mobName;
        displayTicks = 50; // 약 2.5초 동안 화면에 표시
    }

    // 포지 시스템에 우리의 커스텀 HUD 레이어를 등록합니다.
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("xp_notification", (gui, guiGraphics, partialTick, width, height) -> {

            // 타이머가 작동 중일 때만 화면에 그리기
            if (displayTicks > 0) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null || mc.options.hideGui) return;

                // 타이머 틱 감소 계산 (렌더링 틱에 맞춰 부드럽게 감소)
                displayTicks--;

                // 출력할 문자열 포맷팅
                String text = "§e+" + currentXp + " XP §7(" + currentMobName + ")";

                // 화면 중앙 하단(핫바 위쪽) 좌표 계산
                int x = width / 2 - mc.font.width(text) / 2;
                int y = height - 65; // 핫바(Hotbar) 바로 위 배치

                // 텍스트 렌더링 (그림자 포함)
                guiGraphics.drawString(mc.font, text, x, y, 0xFFFFFF, true);
            }
        });
    }
}