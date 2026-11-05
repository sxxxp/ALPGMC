package com.sxxxp.ALPGMC.core.init.items;

import com.sxxxp.ALPGMC.core.init.ItemInit;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

// 망자 장비 재질입니다. 철과 다이아몬드 사이의 성능입니다.
public class RevenantArmorMaterial implements ArmorMaterial {
    public static final RevenantArmorMaterial INSTANCE = new RevenantArmorMaterial();

    private RevenantArmorMaterial() {
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> 220;
            case CHESTPLATE -> 320;
            case LEGGINGS -> 300;
            case BOOTS -> 260;
        };
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET, BOOTS -> 2;
            case CHESTPLATE -> 7;
            case LEGGINGS -> 5;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return 12;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_IRON;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ItemInit.REVENANT_ESSENCE.get());
    }

    // 텍스처는 assets/alpgmc/textures/models/armor/revenant_layer_1.png, _layer_2.png 입니다.
    @Override
    public String getName() {
        return "alpgmc:revenant";
    }

    @Override
    public float getToughness() {
        return 1.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }
}
