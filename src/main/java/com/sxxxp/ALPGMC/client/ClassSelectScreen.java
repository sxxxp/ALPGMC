package com.sxxxp.ALPGMC.client;

import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import com.sxxxp.ALPGMC.core.init.ItemInit;
import com.sxxxp.ALPGMC.core.network.PacketHandler;
import com.sxxxp.ALPGMC.core.network.SelectClassPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public class ClassSelectScreen extends Screen {
    private static final int ARROW_SIZE = 20;
    private static final int ICON_SCALE = 4;
    private static final int ICON_SIZE = 16 * ICON_SCALE;
    private static final int TEXT_WIDTH = 180;
    private static final int GAP = 8;
    private static final int PANEL_WIDTH = ARROW_SIZE * 2 + ICON_SIZE + TEXT_WIDTH + GAP * 3;
    private static final int PANEL_TOP = 44;

    private int index = 0;
    private boolean waiting = false; // 확정을 보낸 뒤 서버 응답을 기다리는 중인지 여부
    private ItemStack icon = ItemStack.EMPTY;
    private MultiLineLabel penaltyLabel = MultiLineLabel.EMPTY;
    private MultiLineLabel bonusLabel = MultiLineLabel.EMPTY;

    public ClassSelectScreen() {
        super(Component.translatable("screen.alpgmc.class_select.title"));
    }

    @Override
    protected void init() {
        int left = (this.width - PANEL_WIDTH) / 2;
        int arrowY = PANEL_TOP + (ICON_SIZE - ARROW_SIZE) / 2;

        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> move(-1))
                .bounds(left, arrowY, ARROW_SIZE, ARROW_SIZE)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> move(1))
                .bounds(left + PANEL_WIDTH - ARROW_SIZE, arrowY, ARROW_SIZE, ARROW_SIZE)
                .build());

        Button confirmButton = this.addRenderableWidget(Button.builder(
                        Component.translatable("screen.alpgmc.class_select.confirm"),
                        button -> confirm(button))
                .bounds(this.width / 2 - 102, this.height - 28, 100, 20)
                .build());
        confirmButton.active = !this.waiting;

        this.addRenderableWidget(Button.builder(
                        Component.translatable("menu.disconnect"),
                        button -> disconnect())
                .bounds(this.width / 2 + 2, this.height - 28, 100, 20)
                .build());

        refresh();
    }

    // < > 버튼으로 이전/다음 직업으로 넘어갑니다. 양 끝에서는 반대쪽으로 돌아갑니다.
    private void move(int direction) {
        int count = PlayerClass.values().length;
        this.index = (this.index + direction + count) % count;
        refresh();
    }

    private void refresh() {
        PlayerClass playerClass = PlayerClass.values()[this.index];
        this.icon = iconOf(playerClass);
        this.penaltyLabel = MultiLineLabel.create(this.font,
                Component.translatable("class.alpgmc." + playerClass.getId() + ".penalty"), TEXT_WIDTH);
        this.bonusLabel = MultiLineLabel.create(this.font,
                Component.translatable("class.alpgmc." + playerClass.getId() + ".bonus"), TEXT_WIDTH);
    }

    // 직업을 한눈에 알아볼 수 있도록 대표 아이템을 정합니다.
    private static ItemStack iconOf(PlayerClass playerClass) {
        return switch (playerClass) {
            case WARRIOR -> new ItemStack(Items.IRON_SWORD);
            case ARCHER -> new ItemStack(ItemInit.ARCHER_BOW.get());
            case MAGE -> new ItemStack(ItemInit.MAGE_STAFF.get());
            case HEALER -> PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING);
            case TANK -> new ItemStack(Items.SHIELD);
        };
    }

    // 서버 확인을 기다리는 동안 다시 누를 수 없게 하고, 화면은 ClassClientEvent에서 닫습니다.
    private void confirm(Button button) {
        this.waiting = true;
        button.active = false;
        PacketHandler.INSTANCE.sendToServer(new SelectClassPacket(PlayerClass.values()[this.index].getId()));
    }

    // Esc로 닫을 수 없으므로 게임을 나갈 방법을 따로 둡니다. 일시정지 메뉴의 연결 끊기와 같은 순서입니다.
    private void disconnect() {
        boolean localServer = this.minecraft.isLocalServer();
        ClientLevel level = this.minecraft.level;
        if (level != null) {
            level.disconnect();
        }
        if (localServer) {
            this.minecraft.clearLevel(new GenericDirtMessageScreen(Component.translatable("menu.savingLevel")));
        } else {
            this.minecraft.clearLevel();
        }
        this.minecraft.setScreen(new TitleScreen());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                Component.translatable("screen.alpgmc.class_select.warning"), this.width / 2, 25, 0xFFAA00);

        int left = (this.width - PANEL_WIDTH) / 2;
        int iconX = left + ARROW_SIZE + GAP;
        int textX = iconX + ICON_SIZE + GAP;

        // 아이템은 16x16으로 그려지므로 크게 보이도록 확대합니다.
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(iconX, PANEL_TOP, 0);
        guiGraphics.pose().scale(ICON_SCALE, ICON_SCALE, 1);
        guiGraphics.renderItem(this.icon, 0, 0);
        guiGraphics.pose().popPose();

        PlayerClass playerClass = PlayerClass.values()[this.index];
        int y = PANEL_TOP;
        guiGraphics.drawString(this.font, Component.translatable("class.alpgmc." + playerClass.getId()), textX, y, 0xFFFF55);
        y += 14;
        guiGraphics.drawString(this.font, Component.translatable("screen.alpgmc.class_select.penalty"), textX, y, 0xFF5555);
        y = this.penaltyLabel.renderLeftAligned(guiGraphics, textX, y + 11, 10, 0xFFFFFF) + 6;
        guiGraphics.drawString(this.font, Component.translatable("screen.alpgmc.class_select.bonus"), textX, y, 0x55FF55);
        this.bonusLabel.renderLeftAligned(guiGraphics, textX, y + 11, 10, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    // 직업은 반드시 골라야 하므로 Esc로 닫지 못하게 합니다.
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
