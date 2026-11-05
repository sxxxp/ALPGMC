package com.sxxxp.ALPGMC.common.event;

import com.sxxxp.ALPGMC.api.gear.GearData;
import com.sxxxp.ALPGMC.api.gear.GearDurability;
import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;

// 강화 수치를 장비의 능력치로 바꿉니다. 활의 강화는 attribute가 없어서 ClassCombatEvent에서 적용합니다.
@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GearEvent {
    private static final UUID WEAPON_DAMAGE = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1010");
    private static final UUID HELMET_ARMOR = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1011");
    private static final UUID CHEST_ARMOR = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1012");
    private static final UUID LEGS_ARMOR = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1013");
    private static final UUID FEET_ARMOR = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1014");

    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();

        // 부서진 장비는 능력치를 전혀 주지 않습니다.
        if (GearDurability.isBroken(stack)) {
            event.clearModifiers();
            return;
        }

        int enhancement = GearData.getEnhancement(stack);
        if (enhancement <= 0) return;

        if (stack.getItem() instanceof ArmorItem armor) {
            if (event.getSlotType() == armor.getEquipmentSlot()) {
                event.addModifier(Attributes.ARMOR, new AttributeModifier(armorId(event.getSlotType()), "alpgmc enhancement armor",
                        ClassConfig.ENHANCE_ARMOR_PER_LEVEL.get() * enhancement, AttributeModifier.Operation.ADDITION));
            }
        } else if (event.getSlotType() == EquipmentSlot.MAINHAND && event.getOriginalModifiers().containsKey(Attributes.ATTACK_DAMAGE)) {
            event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(WEAPON_DAMAGE, "alpgmc enhancement damage",
                    ClassConfig.ENHANCE_WEAPON_DAMAGE_PER_LEVEL.get() * enhancement, AttributeModifier.Operation.ADDITION));
        }
    }

    // 수리는 골드로만 하므로 수선 인챈트는 남기지 않습니다. 상자, 낚시, 거래로 얻은 장비까지 막도록 인벤토리를 1초마다 확인합니다.
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player) || player.tickCount % 20 != 0) return;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isDamageableItem()) continue;

            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            if (enchantments.remove(Enchantments.MENDING) != null) {
                EnchantmentHelper.setEnchantments(enchantments, stack);
                player.displayClientMessage(Component.translatable("message.alpgmc.mending_removed"), true);
            }
        }
    }

    // 같은 attribute에 붙는 수정자라서 부위마다 UUID가 달라야 합산됩니다.
    private static UUID armorId(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> HELMET_ARMOR;
            case CHEST -> CHEST_ARMOR;
            case LEGS -> LEGS_ARMOR;
            default -> FEET_ARMOR;
        };
    }
}
