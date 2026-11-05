package com.sxxxp.ALPGMC.client;

import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.common.playerclass.ClassProgression;
import com.sxxxp.ALPGMC.common.playerclass.PlayerBranch;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import com.sxxxp.ALPGMC.core.network.BranchSelectPacket;
import com.sxxxp.ALPGMC.core.network.PacketHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

// 전투 갈래를 고르고, 갈래와 생산 트리의 해금 내용을 보여주는 화면입니다.
public class BranchScreen extends Screen {
    private static final int TOP = 30;
    private static final int COLUMN_GAP = 12;
    private static final int MAX_COLUMN_WIDTH = 200;
    private static final int PRODUCTION_WIDTH = 320;
    private static final int LINE_HEIGHT = 10;

    private final Map<PlayerBranch, Button> selectButtons = new EnumMap<>(PlayerBranch.class);
    private List<PlayerBranch> branches = List.of();
    private Button tabButton;
    private boolean productionTab = false;
    private PlayerBranch confirming = null; // 경험치를 쓰는 선택이라 두 번 눌러야 합니다.

    public BranchScreen() {
        super(Component.translatable("screen.alpgmc.branch.title"));
    }

    private int columnWidth() {
        return Math.min(MAX_COLUMN_WIDTH, (this.width - COLUMN_GAP - 16) / 2);
    }

    private int columnLeft(int index) {
        int left = (this.width - columnWidth() * 2 - COLUMN_GAP) / 2;
        return left + index * (columnWidth() + COLUMN_GAP);
    }

    @Override
    protected void init() {
        this.selectButtons.clear();
        PlayerClass playerClass = ClientClassData.get();
        this.branches = playerClass == null ? List.of() : PlayerBranch.forClass(playerClass);

        for (int i = 0; i < this.branches.size(); i++) {
            PlayerBranch branch = this.branches.get(i);
            this.selectButtons.put(branch, this.addRenderableWidget(Button.builder(Component.empty(), button -> pressSelect(branch))
                    .bounds(columnLeft(i), this.height - 52, columnWidth(), 20)
                    .build()));
        }

        this.tabButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
                    this.productionTab = !this.productionTab;
                    this.confirming = null;
                    refreshButtons();
                })
                .bounds(this.width / 2 - 122, this.height - 28, 120, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), button -> this.minecraft.setScreen(new StatScreen()))
                .bounds(this.width / 2 + 2, this.height - 28, 120, 20)
                .build());

        refreshButtons();
    }

    private void pressSelect(PlayerBranch branch) {
        if (this.confirming == branch) {
            PacketHandler.INSTANCE.sendToServer(new BranchSelectPacket(branch.getId()));
            this.confirming = null;
        } else {
            this.confirming = branch;
        }
        refreshButtons();
    }

    // 서버에서 받은 값이 바뀔 수 있으므로 매 틱 버튼 상태를 맞춥니다.
    private void refreshButtons() {
        this.tabButton.setMessage(Component.translatable(this.productionTab
                ? "screen.alpgmc.branch.tab_combat"
                : "screen.alpgmc.branch.tab_production"));

        PlayerBranch current = ClientLevelData.getBranch();
        int unlockLevel = ClassProgression.tierLevel(1);
        int cost = ClassConfig.BRANCH_CHANGE_COST.get();
        boolean levelOk = ClientLevelData.getLevel() >= unlockLevel;
        boolean canPay = current == null || (this.minecraft.player != null && this.minecraft.player.experienceLevel >= cost);

        for (Map.Entry<PlayerBranch, Button> entry : this.selectButtons.entrySet()) {
            PlayerBranch branch = entry.getKey();
            Button button = entry.getValue();
            button.visible = !this.productionTab;
            button.active = levelOk && branch != current && canPay;

            if (branch == current) {
                button.setMessage(Component.translatable("screen.alpgmc.branch.selected"));
            } else if (!levelOk) {
                button.setMessage(Component.translatable("screen.alpgmc.branch.need_level", unlockLevel));
            } else if (branch == this.confirming) {
                button.setMessage(Component.translatable("screen.alpgmc.branch.confirm"));
            } else if (current == null) {
                button.setMessage(Component.translatable("screen.alpgmc.branch.select"));
            } else {
                button.setMessage(Component.translatable("screen.alpgmc.branch.change", cost));
            }
        }
    }

    @Override
    public void tick() {
        refreshButtons();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

        if (this.productionTab) {
            renderProduction(guiGraphics);
        } else {
            for (int i = 0; i < this.branches.size(); i++) {
                renderBranch(guiGraphics, this.branches.get(i), columnLeft(i));
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderBranch(GuiGraphics guiGraphics, PlayerBranch branch, int left) {
        boolean selected = branch == ClientLevelData.getBranch();
        guiGraphics.drawString(this.font, Component.translatable("branch.alpgmc." + branch.getId()), left, TOP, selected ? 0x55FF55 : 0xFFFF55);

        int y = TOP + LINE_HEIGHT + 4;
        for (int tier = 1; tier <= 4; tier++) {
            boolean unlocked = selected && ClientLevelData.getLevel() >= ClassProgression.tierLevel(tier);
            y = drawEntry(guiGraphics, "branch.alpgmc." + branch.getId() + "." + tier, tier, left, y, columnWidth(), unlocked);
        }
    }

    private void renderProduction(GuiGraphics guiGraphics) {
        PlayerClass playerClass = ClientClassData.get();
        if (playerClass == null) return;

        int width = Math.min(PRODUCTION_WIDTH, this.width - 16);
        int left = (this.width - width) / 2;
        guiGraphics.drawString(this.font, Component.translatable("production.alpgmc." + playerClass.getId()), left, TOP, 0xFFFF55);

        int y = TOP + LINE_HEIGHT + 4;
        for (int tier = 0; tier <= 4; tier++) {
            boolean unlocked = ClientLevelData.getLevel() >= ClassProgression.tierLevel(tier);
            y = drawEntry(guiGraphics, "production.alpgmc." + playerClass.getId() + "." + tier, tier, left, y, width, unlocked);
        }
    }

    // 해금 레벨은 config를 따르므로 번역문에 적지 않고 여기서 붙입니다. 다음 줄의 y를 돌려줍니다.
    private int drawEntry(GuiGraphics guiGraphics, String key, int tier, int left, int y, int width, boolean unlocked) {
        Component line = Component.translatable("screen.alpgmc.branch.line", ClassProgression.tierLevel(tier), Component.translatable(key));
        for (FormattedCharSequence wrapped : this.font.split(line, width)) {
            guiGraphics.drawString(this.font, wrapped, left, y, unlocked ? 0xFFFFFF : 0x909090);
            y += LINE_HEIGHT;
        }
        return y + 3;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
