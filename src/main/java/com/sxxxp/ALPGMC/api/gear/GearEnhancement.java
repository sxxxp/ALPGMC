package com.sxxxp.ALPGMC.api.gear;

import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

// 수리, 강화, 분해의 계산입니다. 직업은 모르며, 직업에 따른 값은 호출하는 쪽에서 인자로 넘깁니다.
public class GearEnhancement {

    // 수리에 들어갈 골드와 회복할 내구도입니다.
    public record RepairPlan(int gold, int restored) {
    }

    public static boolean isEnhanceable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() instanceof BowItem
                || stack.getItem() instanceof CrossbowItem
                || (stack.isDamageableItem() && stack.getItem().getAttributeModifiers(EquipmentSlot.MAINHAND, stack).containsKey(Attributes.ATTACK_DAMAGE));
    }

    public static int maxEnhancement(boolean limitBreak) {
        return limitBreak ? GearData.MAX_ENHANCEMENT : GearData.BASE_MAX_ENHANCEMENT;
    }

    public static int goldCost(int targetLevel) {
        return ClassConfig.ENHANCE_COST_BASE.get() + ClassConfig.ENHANCE_COST_PER_LEVEL.get() * (targetLevel - 1);
    }

    public static double successChance(int targetLevel, boolean upgraded) {
        double chance = ClassConfig.ENHANCE_SUCCESS_BASE.get() - ClassConfig.ENHANCE_SUCCESS_LOSS_PER_LEVEL.get() * (targetLevel - 1);
        if (upgraded) {
            chance += ClassConfig.ENHANCE_SUCCESS_SAFE_BONUS.get();
        }
        return Math.max(ClassConfig.ENHANCE_SUCCESS_MIN.get(), Math.min(1.0D, chance));
    }

    // 수리할 수 없으면 null을 반환합니다. goldCount는 보유 골드, discount는 비용 할인율입니다. (0.5 = 절반)
    // 골드가 모자라면 낸 만큼만 수리합니다.
    public static RepairPlan planRepair(ItemStack gear, int goldCount, double discount) {
        if (gear.isEmpty() || goldCount <= 0 || !gear.isDamageableItem() || gear.getDamageValue() <= 0) return null;

        int damage = gear.getDamageValue();
        double durabilityPerGold = gear.getMaxDamage() / (ClassConfig.REPAIR_FULL_COST.get() * (1.0D - discount));
        int needed = (int) Math.ceil(damage / durabilityPerGold);
        int gold = Math.min(goldCount, needed);
        return new RepairPlan(gold, Math.min(damage, (int) Math.ceil(gold * durabilityPerGold)));
    }

    // 옵션 티어가 높을수록 재료를 많이 돌려줍니다. T1이 가장 많고 T5가 가장 적습니다.
    public static int disassembleYield(ItemStack gear) {
        int total = 0;
        for (GearData.OptionEntry option : GearData.getOptions(gear)) {
            int tier = Math.max(GearOption.BEST_TIER, Math.min(option.tier(), GearOption.WORST_TIER));
            total += (GearOption.WORST_TIER + 1 - tier) * ClassConfig.DISASSEMBLE_YIELD_PER_TIER.get();
        }
        return total;
    }
}
