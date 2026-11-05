package com.sxxxp.ALPGMC.client;

import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.common.playerclass.PlayerStat;
import com.sxxxp.ALPGMC.core.network.PacketHandler;
import com.sxxxp.ALPGMC.core.network.StatActionPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class StatScreen extends Screen {
    private static final int ROW_WIDTH = 240;
    private static final int ROW_HEIGHT = 22;
    private static final int ROWS_TOP = 58;

    private final List<PlayerStat> stats = new ArrayList<>();
    private final Map<PlayerStat, Button> plusButtons = new EnumMap<>(PlayerStat.class);
    private Button resetButton;
    private boolean confirmingReset = false; // 실수로 초기화하지 않도록 두 번 눌러야 합니다.

    public StatScreen() {
        super(Component.translatable("screen.alpgmc.stats.title"));
    }

    @Override
    protected void init() {
        this.stats.clear();
        this.plusButtons.clear();
        for (PlayerStat stat : PlayerStat.values()) {
            if (stat.isAvailableFor(ClientClassData.get())) {
                this.stats.add(stat);
            }
        }

        int left = (this.width - ROW_WIDTH) / 2;
        int y = ROWS_TOP;
        for (PlayerStat stat : this.stats) {
            this.plusButtons.put(stat, this.addRenderableWidget(Button.builder(
                            Component.literal("+"),
                            button -> PacketHandler.INSTANCE.sendToServer(new StatActionPacket(stat.ordinal())))
                    .bounds(left + ROW_WIDTH - 20, y - 5, 20, 20)
                    .build()));
            y += ROW_HEIGHT;
        }

        this.resetButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> pressReset())
                .bounds(this.width / 2 - 122, this.height - 28, 120, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> this.onClose())
                .bounds(this.width / 2 + 2, this.height - 28, 120, 20)
                .build());

        refreshButtons();
    }

    private void pressReset() {
        if (this.confirmingReset) {
            PacketHandler.INSTANCE.sendToServer(new StatActionPacket(StatActionPacket.RESET));
        }
        this.confirmingReset = !this.confirmingReset;
        refreshButtons();
    }

    // 서버에서 받은 값이 바뀔 수 있으므로 매 틱 버튼 상태를 맞춥니다.
    private void refreshButtons() {
        int max = ClassConfig.LEVEL_MAX_STAT_POINTS.get();
        for (Map.Entry<PlayerStat, Button> entry : this.plusButtons.entrySet()) {
            entry.getValue().active = ClientLevelData.getPoints() > 0 && ClientLevelData.getStat(entry.getKey()) < max;
        }

        int cost = ClassConfig.LEVEL_RESET_COST.get();
        this.resetButton.active = this.minecraft.player != null && this.minecraft.player.experienceLevel >= cost;
        this.resetButton.setMessage(this.confirmingReset
                ? Component.translatable("screen.alpgmc.stats.reset_confirm")
                : Component.translatable("screen.alpgmc.stats.reset", cost));
    }

    @Override
    public void tick() {
        refreshButtons();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

        Component xpText = ClientLevelData.getXpToNext() > 0
                ? Component.translatable("screen.alpgmc.stats.level", ClientLevelData.getLevel(), ClientLevelData.getXp(), ClientLevelData.getXpToNext())
                : Component.translatable("screen.alpgmc.stats.level_max", ClientLevelData.getLevel());
        guiGraphics.drawCenteredString(this.font, xpText, this.width / 2, 24, 0xFFFF55);
        guiGraphics.drawCenteredString(this.font,
                Component.translatable("screen.alpgmc.stats.points", ClientLevelData.getPoints()), this.width / 2, 38, 0x55FF55);

        int left = (this.width - ROW_WIDTH) / 2;
        int y = ROWS_TOP;
        for (PlayerStat stat : this.stats) {
            int points = ClientLevelData.getStat(stat);
            guiGraphics.drawString(this.font, Component.translatable("stat.alpgmc." + stat.getId()), left, y, 0xFFFFFF);
            guiGraphics.drawString(this.font, String.valueOf(points), left + 120, y, 0xFFFFFF);
            guiGraphics.drawString(this.font, stat.bonusText(points), left + 150, y, 0xAAAAAA);
            y += ROW_HEIGHT;
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
