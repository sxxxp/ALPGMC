package com.sxxxp.ALPGMC.api.gear;

import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.core.init.ItemInit;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

// 플레이어 장비의 내구도 규칙입니다. 내구도가 0이면 사라지지 않고 부서진 상태로 남습니다.
public class GearDurability {

    public static boolean isBroken(ItemStack stack) {
        return stack.isDamageableItem() && stack.getDamageValue() >= stack.getMaxDamage();
    }

    // 사망해도 떨어지지 않는 장비입니다. 지팡이는 내구도가 없지만 무기라서 포함합니다.
    public static boolean isKeptOnDeath(ItemStack stack) {
        return stack.getItem().canBeDepleted() || stack.is(ItemInit.MAGE_STAFF.get());
    }

    // 남은 내구도에서 설정된 비율만큼 무작위로 깎습니다. 남은 내구도가 있으면 최소 1은 깎입니다.
    public static void applyDeathLoss(ItemStack stack, RandomSource random) {
        if (!stack.isDamageableItem()) return;

        int remaining = stack.getMaxDamage() - stack.getDamageValue();
        if (remaining <= 0) return;

        double min = ClassConfig.DEATH_DURABILITY_LOSS_MIN.get();
        double max = Math.max(min, ClassConfig.DEATH_DURABILITY_LOSS_MAX.get());
        int loss = Math.max(1, (int) Math.round(remaining * (min + random.nextDouble() * (max - min))));
        stack.setDamageValue(stack.getDamageValue() + Math.min(loss, remaining));
    }
}
