package com.sxxxp.ALPGMC.core.init.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

// 마법사 로브 재질입니다. 방어력과 내구도는 가죽 갑옷과 같습니다.
public class RobeMaterial implements ArmorMaterial {
    public static final RobeMaterial INSTANCE = new RobeMaterial();

    private RobeMaterial() {
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> 55;
            case CHESTPLATE -> 80;
            case LEGGINGS -> 75;
            case BOOTS -> 65;
        };
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET, BOOTS -> 1;
            case CHESTPLATE -> 3;
            case LEGGINGS -> 2;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ItemTags.WOOL);
    }

    // 텍스처는 assets/alpgmc/textures/models/armor/mage_robe_layer_1.png, _layer_2.png 입니다.
    @Override
    public String getName() {
        return "alpgmc:mage_robe";
    }

    @Override
    public float getToughness() {
        return 0.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }
}
