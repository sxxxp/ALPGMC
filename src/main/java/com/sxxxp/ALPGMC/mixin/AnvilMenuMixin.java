package com.sxxxp.ALPGMC.mixin;

import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClassData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    @Shadow
    @Final
    private DataSlot cost;

    private AnvilMenuMixin(MenuType<?> type, int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(type, containerId, inventory, access);
    }

    // 탱커는 "너무 비쌈" 제한(40레벨)을 받지 않습니다.
    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40))
    private int alpgmc$removeCostLimit(int limit) {
        return PlayerClassData.get(this.player) == PlayerClass.TANK ? Integer.MAX_VALUE : limit;
    }

    // 결과물의 내구도가 회복되는 작업(수리)이면 탱커의 비용을 깎습니다. 이름 변경만 할 때는 깎지 않습니다.
    @Inject(method = "createResult", at = @At("TAIL"))
    private void alpgmc$discountRepair(CallbackInfo ci) {
        if (PlayerClassData.get(this.player) != PlayerClass.TANK) return;

        ItemStack input = this.inputSlots.getItem(0);
        ItemStack result = this.resultSlots.getItem(0);
        if (!result.isEmpty() && result.isDamageableItem() && result.getDamageValue() < input.getDamageValue()) {
            double remaining = 1.0D - ClassConfig.TANK_ANVIL_REPAIR_DISCOUNT.get();
            this.cost.set(Math.max(1, (int) Math.ceil(this.cost.get() * remaining)));
        }
    }
}
