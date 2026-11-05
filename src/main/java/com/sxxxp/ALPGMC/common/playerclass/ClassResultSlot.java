package com.sxxxp.ALPGMC.common.playerclass;

import com.sxxxp.ALPGMC.core.init.items.ClassArrowItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

// 크래프팅 결과 슬롯을 감싸서 직업별 제작 제한과 전사의 화살 배수를 적용합니다.
// 서버에서만 교체하며, 여기서 돌려준 결과가 클라이언트에 그대로 동기화됩니다.
public class ClassResultSlot extends Slot {
    private final Slot original;
    private final AbstractContainerMenu menu;
    private final Player player;
    private ItemStack multiplied = ItemStack.EMPTY; // 이미 배수를 적용한 결과물

    private ClassResultSlot(Slot original, AbstractContainerMenu menu, Player player) {
        super(original.container, original.getContainerSlot(), original.x, original.y);
        this.original = original;
        this.menu = menu;
        this.player = player;
        this.index = original.index;
    }

    // 모든 크래프팅 메뉴의 결과 슬롯은 0번입니다.
    public static void install(AbstractContainerMenu menu, Player player) {
        if (!(menu.slots.get(0) instanceof ClassResultSlot)) {
            menu.slots.set(0, new ClassResultSlot(menu.slots.get(0), menu, player));
        }
    }

    @Override
    public ItemStack getItem() {
        ItemStack result = this.original.getItem();
        if (result.isEmpty()) return result;

        PlayerClass playerClass = PlayerClassData.get(this.player);
        // 금지된 결과물은 빈 칸으로 보여서 꺼낼 수 없고 재료도 소모되지 않습니다.
        if (isForbidden(playerClass, result)) return ItemStack.EMPTY;

        // 결과물이 새로 만들어질 때마다 한 번만 개수를 늘립니다.
        if (playerClass == PlayerClass.WARRIOR && isArrow(result) && result != this.multiplied) {
            int multiplier = ClassProgression.hasProductionTier(this.player, PlayerClass.WARRIOR, 2)
                    ? ClassConfig.WARRIOR_ARROW_CRAFT_MULTIPLIER_UPGRADED.get()
                    : ClassConfig.WARRIOR_ARROW_CRAFT_MULTIPLIER.get();
            int count = result.getCount() * multiplier;
            result.setCount(Math.min(count, result.getMaxStackSize()));
            this.multiplied = result;
        }
        return result;
    }

    private boolean isForbidden(PlayerClass playerClass, ItemStack result) {
        if (playerClass == PlayerClass.ARCHER) {
            return isArrow(result);
        }
        if (playerClass == PlayerClass.TANK) {
            return result.getItem() instanceof ArmorItem && !isRepair(result);
        }
        return false;
    }

    // 전용 화살은 바닐라 활에 들어가지 않도록 minecraft:arrows 태그에 넣지 않아서 따로 확인합니다.
    private static boolean isArrow(ItemStack stack) {
        return stack.is(ItemTags.ARROWS) || stack.getItem() instanceof ClassArrowItem;
    }

    // 제작대에서 같은 갑옷 두 개를 합치는 수리는 탱커도 할 수 있습니다.
    private boolean isRepair(ItemStack result) {
        for (Slot slot : this.menu.slots) {
            if (slot.container instanceof CraftingContainer && slot.getItem().is(result.getItem())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return !getItem().isEmpty() && this.original.mayPickup(player);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.original.mayPlace(stack);
    }

    @Override
    public ItemStack remove(int amount) {
        return this.original.remove(amount);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.original.onTake(player, stack);
    }

    @Override
    public void onQuickCraft(ItemStack oldStack, ItemStack newStack) {
        this.original.onQuickCraft(oldStack, newStack);
    }

    @Override
    public void set(ItemStack stack) {
        this.original.set(stack);
    }

    @Override
    public void setChanged() {
        this.original.setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return this.original.getMaxStackSize();
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return this.original.getMaxStackSize(stack);
    }
}
