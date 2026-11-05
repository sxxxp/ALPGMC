package com.sxxxp.ALPGMC.common.workstation;

import com.sxxxp.ALPGMC.api.ProductionAction;
import com.sxxxp.ALPGMC.api.gear.GearData;
import com.sxxxp.ALPGMC.api.gear.GearEnhancement;
import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.common.playerclass.PlayerGold;
import com.sxxxp.ALPGMC.core.init.BlockInit;
import com.sxxxp.ALPGMC.core.init.ItemInit;
import com.sxxxp.ALPGMC.core.init.MenuInit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// 강화 모루의 메뉴입니다. 수리, 강화, 분해는 화면 버튼으로 요청하고, 모든 검사와 결과 계산은 서버에서 합니다.
public class EnhancementAnvilMenu extends AbstractContainerMenu {
    public static final int BUTTON_REPAIR = 0;
    public static final int BUTTON_ENHANCE = 1;
    public static final int BUTTON_DISASSEMBLE = 2;

    private static final int GEAR_SLOT = 0;
    private static final int SCROLL_SLOT = 1;
    private static final int INVENTORY_START = 2;
    private static final int INVENTORY_END = INVENTORY_START + 36;

    // 바닐라 레벨 이벤트 번호입니다: 모루 사용, 모루 파괴, 모루 착지 소리
    private static final int SOUND_SUCCESS = 1030;
    private static final int SOUND_DESTROYED = 1029;
    private static final int SOUND_FAILED = 1031;

    private final Container container = new SimpleContainer(2);
    private final ContainerLevelAccess access;

    public EnhancementAnvilMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public EnhancementAnvilMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(MenuInit.ENHANCEMENT_ANVIL.get(), containerId);
        this.access = access;

        this.addSlot(new Slot(this.container, GEAR_SLOT, 14, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.isDamageableItem();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.container, SCROLL_SLOT, 36, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ItemInit.ENHANCEMENT_SCROLL.get());
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 110 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, 8 + column * 18, 168));
        }
    }

    public ItemStack getGear() {
        return this.slots.get(GEAR_SLOT).getItem();
    }

    public int getScrollCount() {
        return this.slots.get(SCROLL_SLOT).getItem().getCount();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!(player instanceof ServerPlayer serverPlayer)) return false;

        this.access.execute((level, pos) -> {
            switch (id) {
                case BUTTON_REPAIR -> repair(serverPlayer, level, pos);
                case BUTTON_ENHANCE -> enhance(serverPlayer, level, pos);
                case BUTTON_DISASSEMBLE -> disassemble(serverPlayer, level, pos);
                default -> {
                }
            }
        });
        return true;
    }

    private void repair(ServerPlayer player, Level level, BlockPos pos) {
        double discount = ProductionAction.REPAIR_DISCOUNT.canUse(player) ? ClassConfig.TANK_ANVIL_REPAIR_DISCOUNT.get() : 0.0D;
        GearEnhancement.RepairPlan plan = GearEnhancement.planRepair(getGear(), PlayerGold.get(player), discount);
        if (plan == null || !PlayerGold.spend(player, plan.gold())) return;

        ItemStack gear = getGear();
        gear.setDamageValue(gear.getDamageValue() - plan.restored());
        this.container.setChanged();
        level.levelEvent(SOUND_SUCCESS, pos, 0);
    }

    private void enhance(ServerPlayer player, Level level, BlockPos pos) {
        ItemStack gear = getGear();
        if (!ProductionAction.ENHANCE.canUse(player) || !GearEnhancement.isEnhanceable(gear)) return;

        int target = GearData.getEnhancement(gear) + 1;
        if (target > GearEnhancement.maxEnhancement(ProductionAction.ENHANCE_LIMIT_BREAK.canUse(player))) return;

        // 골드를 먼저 차감하면 주문서가 모자랄 때 골드만 사라지므로 주문서부터 확인합니다.
        int scrollCost = ClassConfig.ENHANCE_SCROLL_COST.get();
        if (getScrollCount() < scrollCost || !PlayerGold.spend(player, GearEnhancement.goldCost(target))) return;

        this.slots.get(SCROLL_SLOT).getItem().shrink(scrollCost);
        boolean safe = ProductionAction.ENHANCE_SAFE.canUse(player);

        if (player.getRandom().nextDouble() < GearEnhancement.successChance(target, safe)) {
            GearData.setEnhancement(gear, target);
            level.levelEvent(SOUND_SUCCESS, pos, 0);
            player.displayClientMessage(Component.translatable("message.alpgmc.enhance_success", target), true);
        } else if (!safe && player.getRandom().nextDouble() < ClassConfig.ENHANCE_FAIL_DESTROY_CHANCE.get()) {
            this.container.setItem(GEAR_SLOT, ItemStack.EMPTY);
            level.levelEvent(SOUND_DESTROYED, pos, 0);
            player.displayClientMessage(Component.translatable("message.alpgmc.enhance_destroyed"), true);
        } else {
            level.levelEvent(SOUND_FAILED, pos, 0);
            player.displayClientMessage(Component.translatable("message.alpgmc.enhance_failed"), true);
        }
        this.container.setChanged();
    }

    private void disassemble(ServerPlayer player, Level level, BlockPos pos) {
        ItemStack gear = getGear();
        int amount = GearEnhancement.disassembleYield(gear);
        if (!ProductionAction.DISASSEMBLE.canUse(player) || amount <= 0) return;

        this.container.setItem(GEAR_SLOT, ItemStack.EMPTY);
        this.container.setChanged();
        player.getInventory().placeItemBackInInventory(new ItemStack(ItemInit.OPTION_DUST.get(), amount));
        level.levelEvent(SOUND_DESTROYED, pos, 0);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < INVENTORY_START) {
            if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END, true)) return ItemStack.EMPTY;
        } else if (stack.is(ItemInit.ENHANCEMENT_SCROLL.get())) {
            if (!this.moveItemStackTo(stack, SCROLL_SLOT, SCROLL_SLOT + 1, false)) return ItemStack.EMPTY;
        } else if (!stack.isDamageableItem() || !this.moveItemStackTo(stack, GEAR_SLOT, GEAR_SLOT + 1, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, BlockInit.ENHANCEMENT_ANVIL.get());
    }

    // 창을 닫으면 칸에 남은 아이템을 돌려줍니다.
    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.container));
    }
}
