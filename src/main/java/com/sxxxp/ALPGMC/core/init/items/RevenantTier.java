package com.sxxxp.ALPGMC.core.init.items;

import com.sxxxp.ALPGMC.core.init.ItemInit;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

// 망자의 검 재질입니다. 철(공격력 보너스 2)과 다이아몬드(3) 사이의 성능입니다.
public class RevenantTier implements Tier {
    public static final RevenantTier INSTANCE = new RevenantTier();

    private RevenantTier() {
    }

    @Override
    public int getUses() {
        return 700;
    }

    @Override
    public float getSpeed() {
        return 7.0F;
    }

    @Override
    public float getAttackDamageBonus() {
        return 2.5F;
    }

    @Override
    public int getLevel() {
        return 2;
    }

    @Override
    public int getEnchantmentValue() {
        return 12;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ItemInit.REVENANT_ESSENCE.get());
    }
}
