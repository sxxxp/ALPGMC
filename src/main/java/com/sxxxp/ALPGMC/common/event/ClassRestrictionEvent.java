package com.sxxxp.ALPGMC.common.event;

import com.sxxxp.ALPGMC.common.playerclass.ClassResultSlot;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClassData;
import com.sxxxp.ALPGMC.common.workstation.EnhancementAnvilBlock;
import com.sxxxp.ALPGMC.core.init.items.RobeMaterial;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.SmithingTableBlock;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClassRestrictionEvent {

    // 인벤토리 2x2 제작칸은 메뉴를 여는 이벤트가 없어서 플레이어가 만들어질 때 슬롯을 교체합니다.
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ClassResultSlot.install(player.inventoryMenu, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ClassResultSlot.install(player.inventoryMenu, player);
        }
    }

    // 제작대를 열 때 결과 슬롯을 교체합니다.
    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getContainer() instanceof CraftingMenu && event.getEntity() instanceof ServerPlayer player) {
            ClassResultSlot.install(event.getContainer(), player);
        }
    }

    // 궁수는 공격력이 붙은 무기나 도구로 때릴 수 없습니다. 채굴과 맨손 공격은 그대로 됩니다.
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && PlayerClassData.get(player) == PlayerClass.ARCHER
                && player.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE)) {
            event.setCanceled(true);
            deny(player);
        }
    }

    // 궁수는 삼지창을 던질 수 없습니다. 클라이언트에서도 막아서 창을 드는 동작이 보이지 않게 합니다.
    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player
                && PlayerClassData.get(player) == PlayerClass.ARCHER
                && event.getItem().getItem() instanceof TridentItem) {
            event.setCanceled(true);
            if (player instanceof ServerPlayer serverPlayer) {
                deny(serverPlayer);
            }
        }
    }

    // 바닐라 인챈트대, 양조대, 모루는 모드 작업대로 대체되어 누구도 쓸 수 없습니다. 마법사는 강화 모루와 대장장이 작업대도 열 수 없습니다.
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
            boolean vanillaReplaced = block instanceof EnchantmentTableBlock || block instanceof BrewingStandBlock || block instanceof AnvilBlock;
            boolean forbidden = vanillaReplaced
                    || (PlayerClassData.get(player) == PlayerClass.MAGE && (block instanceof EnhancementAnvilBlock || block instanceof SmithingTableBlock));
            if (forbidden) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
                player.displayClientMessage(Component.translatable(vanillaReplaced
                        ? "message.alpgmc.vanilla_replaced"
                        : "message.alpgmc.class_restricted"), true);
            }
        }
    }

    // 호퍼로 재료를 넣으면 창을 열지 않고도 양조되므로 양조 자체도 막습니다.
    @SubscribeEvent
    public static void onPotionBrew(PotionBrewEvent.Pre event) {
        event.setCanceled(true);
    }

    // 마법사의 갑옷 칸에 로브와 겉날개가 아닌 것이 있으면 벗겨서 인벤토리로 돌려줍니다. 장착 경로가 여러 개라 매 틱 확인합니다.
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;
        if (PlayerClassData.get(player) != PlayerClass.MAGE) return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;

            ItemStack worn = player.getItemBySlot(slot);
            if (!worn.isEmpty() && !isRobe(worn) && !(worn.getItem() instanceof ElytraItem)) {
                player.setItemSlot(slot, ItemStack.EMPTY);
                player.getInventory().placeItemBackInInventory(worn);
                deny(player);
            }
        }
    }

    private static boolean isRobe(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armor && armor.getMaterial() == RobeMaterial.INSTANCE;
    }

    // 막힌 이유를 핫바 위 메시지로 알려줍니다.
    private static void deny(ServerPlayer player) {
        player.displayClientMessage(Component.translatable("message.alpgmc.class_restricted"), true);
    }
}
