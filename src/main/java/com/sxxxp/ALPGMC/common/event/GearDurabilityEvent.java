package com.sxxxp.ALPGMC.common.event;

import com.sxxxp.ALPGMC.api.gear.GearDurability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// 사망 시 장비 보존과 부서진 장비의 사용 금지를 처리합니다.
@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GearDurabilityEvent {
    private static final String KEPT_GEAR = "alpgmc_kept_gear";
    private static final String SLOT = "alpgmc_slot";

    // 바닐라가 인벤토리를 떨어뜨리기 전에 장비를 빼 둡니다.
    // 죽은 채로 접속을 끊거나 서버가 꺼져도 잃지 않도록 플레이어 NBT에 저장합니다.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Inventory inventory = player.getInventory();
        ListTag kept = new ListTag();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            // 소실 저주가 붙은 장비는 바닐라 규칙대로 사라지게 둡니다.
            if (stack.isEmpty() || !GearDurability.isKeptOnDeath(stack) || EnchantmentHelper.hasVanishingCurse(stack)) continue;

            GearDurability.applyDeathLoss(stack, player.getRandom());
            CompoundTag tag = stack.save(new CompoundTag());
            tag.putInt(SLOT, i);
            kept.add(tag);
            inventory.setItem(i, ItemStack.EMPTY);
        }
        if (!kept.isEmpty()) {
            player.getPersistentData().put(KEPT_GEAR, kept);
        }
    }

    // 부활하면 장비를 원래 칸으로 돌려줍니다.
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag oldData = event.getOriginal().getPersistentData();
        if (!event.isWasDeath() || !oldData.contains(KEPT_GEAR)) return;

        Inventory inventory = event.getEntity().getInventory();
        ListTag kept = oldData.getList(KEPT_GEAR, Tag.TAG_COMPOUND);
        for (int i = 0; i < kept.size(); i++) {
            CompoundTag tag = kept.getCompound(i);
            ItemStack stack = ItemStack.of(tag);
            int slot = tag.getInt(SLOT);
            if (inventory.getItem(slot).isEmpty()) {
                inventory.setItem(slot, stack);
            } else {
                inventory.placeItemBackInInventory(stack);
            }
        }
        oldData.remove(KEPT_GEAR);
    }

    // 부서진 갑옷은 보호 인챈트까지 막기 위해 벗겨서 인벤토리로 돌려줍니다. 장착 경로가 여러 개라 매 틱 확인합니다.
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;

            ItemStack worn = player.getItemBySlot(slot);
            if (GearDurability.isBroken(worn)) {
                player.setItemSlot(slot, ItemStack.EMPTY);
                player.getInventory().placeItemBackInInventory(worn);
                deny(player);
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (GearDurability.isBroken(event.getEntity().getMainHandItem())) {
            event.setCanceled(true);
            deny(event.getEntity());
        }
    }

    // 부서진 도구로는 블록을 캘 수 없습니다.
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (GearDurability.isBroken(event.getEntity().getMainHandItem())) {
            event.setCanceled(true);
        }
    }

    // 활 당기기와 방패 막기 등 누르고 있는 동작입니다.
    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (GearDurability.isBroken(event.getItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (GearDurability.isBroken(event.getItemStack())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            deny(event.getEntity());
        }
    }

    // 문이나 상자는 그대로 열 수 있도록 아이템 사용만 막습니다.
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (GearDurability.isBroken(event.getItemStack())) {
            event.setUseItem(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (GearDurability.isBroken(event.getItemStack())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            deny(event.getEntity());
        }
    }

    private static void deny(Player player) {
        if (player instanceof ServerPlayer) {
            player.displayClientMessage(Component.translatable("message.alpgmc.gear_broken"), true);
        }
    }
}
