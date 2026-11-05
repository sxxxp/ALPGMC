package com.sxxxp.ALPGMC.client;

import com.sxxxp.ALPGMC.api.ProductionAction;
import com.sxxxp.ALPGMC.api.gear.GearData;
import com.sxxxp.ALPGMC.api.gear.GearEnhancement;
import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import com.sxxxp.ALPGMC.common.workstation.EnhancementAnvilMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

// 강화 모루 화면입니다. 여기서 보여주는 값은 미리보기이고, 실제 결과는 서버가 다시 계산합니다.
public class EnhancementAnvilScreen extends AbstractContainerScreen<EnhancementAnvilMenu> {
    private static final int PANEL_COLOR = 0xFFC6C6C6;
    private static final int SLOT_COLOR = 0xFF8B8B8B;
    private static final int TEXT_COLOR = 0x404040;
    private static final int WARNING_COLOR = 0xAA0000;
    private static final int GOLD_COLOR = 0x8A6D00;

    private Button repairButton;
    private Button enhanceButton;
    private Button disassembleButton;

    public EnhancementAnvilScreen(EnhancementAnvilMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 192;
        this.inventoryLabelY = 99;
    }

    @Override
    protected void init() {
        super.init();
        this.repairButton = addActionButton("repair", EnhancementAnvilMenu.BUTTON_REPAIR, 58);
        this.enhanceButton = addActionButton("enhance", EnhancementAnvilMenu.BUTTON_ENHANCE, 96);
        this.disassembleButton = addActionButton("disassemble", EnhancementAnvilMenu.BUTTON_DISASSEMBLE, 134);
        refreshButtons();
    }

    private Button addActionButton(String name, int id, int x) {
        return this.addRenderableWidget(Button.builder(Component.translatable("screen.alpgmc.anvil." + name),
                        button -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id))
                .bounds(this.leftPos + x, this.topPos + 18, 36, 20)
                .build());
    }

    private double repairDiscount() {
        return ProductionAction.REPAIR_DISCOUNT.isAvailable(ClientClassData.get(), ClientLevelData.getLevel())
                ? ClassConfig.TANK_ANVIL_REPAIR_DISCOUNT.get()
                : 0.0D;
    }

    private boolean can(ProductionAction action) {
        return action.isAvailable(ClientClassData.get(), ClientLevelData.getLevel());
    }

    private boolean canEnhance() {
        ItemStack gear = this.menu.getGear();
        if (!can(ProductionAction.ENHANCE) || !GearEnhancement.isEnhanceable(gear)) return false;

        int target = GearData.getEnhancement(gear) + 1;
        return target <= GearEnhancement.maxEnhancement(can(ProductionAction.ENHANCE_LIMIT_BREAK))
                && ClientLevelData.getGold() >= GearEnhancement.goldCost(target)
                && this.menu.getScrollCount() >= ClassConfig.ENHANCE_SCROLL_COST.get();
    }

    private void refreshButtons() {
        ItemStack gear = this.menu.getGear();
        this.repairButton.active = GearEnhancement.planRepair(gear, ClientLevelData.getGold(), repairDiscount()) != null;
        this.enhanceButton.active = canEnhance();
        this.disassembleButton.active = can(ProductionAction.DISASSEMBLE) && GearEnhancement.disassembleYield(gear) > 0;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        refreshButtons();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, PANEL_COLOR);
        for (Slot slot : this.menu.slots) {
            int x = this.leftPos + slot.x;
            int y = this.topPos + slot.y;
            guiGraphics.fill(x - 1, y - 1, x + 17, y + 17, SLOT_COLOR);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        int y = drawLine(guiGraphics, Component.translatable("screen.alpgmc.anvil.gold", ClientLevelData.getGold()), 42, GOLD_COLOR);
        y = drawLine(guiGraphics, repairText(), y, TEXT_COLOR);
        y = drawLine(guiGraphics, enhanceText(), y, TEXT_COLOR);
        if (enhanceDestroysOnFail()) {
            y = drawLine(guiGraphics, Component.translatable("screen.alpgmc.anvil.destroy_warning"), y, WARNING_COLOR);
        }
        drawLine(guiGraphics, disassembleText(), y, TEXT_COLOR);
    }

    // 다음 줄의 y를 돌려줍니다.
    private int drawLine(GuiGraphics guiGraphics, Component text, int y, int color) {
        for (FormattedCharSequence line : this.font.split(text, this.imageWidth - 16)) {
            guiGraphics.drawString(this.font, line, 8, y, color, false);
            y += 9;
        }
        return y;
    }

    private Component repairText() {
        GearEnhancement.RepairPlan plan = GearEnhancement.planRepair(this.menu.getGear(), ClientLevelData.getGold(), repairDiscount());
        return plan == null
                ? Component.translatable("screen.alpgmc.anvil.repair_idle")
                : Component.translatable("screen.alpgmc.anvil.repair_preview", plan.gold(), plan.restored());
    }

    private Component enhanceText() {
        if (!can(ProductionAction.ENHANCE)) {
            return Component.translatable("screen.alpgmc.anvil.enhance_locked", ProductionAction.ENHANCE.requiredLevel());
        }

        ItemStack gear = this.menu.getGear();
        if (!GearEnhancement.isEnhanceable(gear)) {
            return Component.translatable("screen.alpgmc.anvil.enhance_idle");
        }

        int current = GearData.getEnhancement(gear);
        int target = current + 1;
        if (target > GearData.MAX_ENHANCEMENT) {
            return Component.translatable("screen.alpgmc.anvil.enhance_max");
        }
        if (target > GearEnhancement.maxEnhancement(can(ProductionAction.ENHANCE_LIMIT_BREAK))) {
            return Component.translatable("screen.alpgmc.anvil.enhance_limit", ProductionAction.ENHANCE_LIMIT_BREAK.requiredLevel());
        }

        int chance = (int) Math.round(GearEnhancement.successChance(target, can(ProductionAction.ENHANCE_SAFE)) * 100.0D);
        return Component.translatable("screen.alpgmc.anvil.enhance_preview", current, target, chance, GearEnhancement.goldCost(target), ClassConfig.ENHANCE_SCROLL_COST.get());
    }

    private boolean enhanceDestroysOnFail() {
        ItemStack gear = this.menu.getGear();
        if (!can(ProductionAction.ENHANCE) || !GearEnhancement.isEnhanceable(gear) || can(ProductionAction.ENHANCE_SAFE)) return false;

        int target = GearData.getEnhancement(gear) + 1;
        return target <= GearData.BASE_MAX_ENHANCEMENT
                && GearEnhancement.successChance(target, false) < 1.0D
                && ClassConfig.ENHANCE_FAIL_DESTROY_CHANCE.get() > 0.0D;
    }

    private Component disassembleText() {
        if (!can(ProductionAction.DISASSEMBLE)) {
            return Component.translatable("screen.alpgmc.anvil.disassemble_locked", ProductionAction.DISASSEMBLE.requiredLevel());
        }
        int amount = GearEnhancement.disassembleYield(this.menu.getGear());
        return amount > 0
                ? Component.translatable("screen.alpgmc.anvil.disassemble_preview", amount)
                : Component.translatable("screen.alpgmc.anvil.disassemble_idle");
    }
}
