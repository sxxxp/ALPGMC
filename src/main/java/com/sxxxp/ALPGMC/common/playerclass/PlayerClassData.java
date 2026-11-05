package com.sxxxp.ALPGMC.common.playerclass;

import com.sxxxp.ALPGMC.client.ClientClassData;
import com.sxxxp.ALPGMC.core.init.ItemInit;
import com.sxxxp.ALPGMC.core.network.ClassSyncPacket;
import com.sxxxp.ALPGMC.core.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;

public class PlayerClassData {
    private static final String KEY = "alpgmc_class";
    private static final String STARTER_KEY = "alpgmc_archer_starter";

    // 플레이어의 직업을 읽습니다. 아직 선택하지 않았으면 null을 반환합니다.
    // 클라이언트는 본인 직업만 알기 때문에, 클라이언트에서는 본인에 대해서만 호출해야 합니다.
    public static PlayerClass get(Player player) {
        if (player.level().isClientSide) {
            return ClientClassData.get();
        }
        return PlayerClass.fromId(player.getPersistentData().getString(KEY));
    }

    // 직업을 저장하고 해당 플레이어의 클라이언트에 바로 알려줍니다.
    public static void set(ServerPlayer player, PlayerClass playerClass) {
        player.getPersistentData().putString(KEY, playerClass.getId());
        ClassAttributes.apply(player);
        if (playerClass == PlayerClass.ARCHER) {
            giveArcherStarter(player);
        }
        sync(player);
    }

    // 시작 아이템은 플레이어당 한 번만 지급합니다. /class set으로 다시 궁수가 돼도 받지 않습니다.
    private static void giveArcherStarter(ServerPlayer player) {
        if (player.getPersistentData().getBoolean(STARTER_KEY)) return;

        player.getPersistentData().putBoolean(STARTER_KEY, true);
        player.getInventory().placeItemBackInInventory(new ItemStack(ItemInit.ARCHER_BOW.get()));
        player.getInventory().placeItemBackInInventory(new ItemStack(ItemInit.WOOD_ARROW.get(), ClassConfig.ARCHER_STARTER_ARROWS.get()));
    }

    public static void sync(ServerPlayer player) {
        PlayerClass playerClass = get(player);
        PacketHandler.INSTANCE.sendTo(
                new ClassSyncPacket(playerClass == null ? "" : playerClass.getId()),
                player.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
        );
    }

    // 사망이나 엔드 귀환으로 플레이어 객체가 새로 만들어질 때 직업을 옮겨 적습니다.
    public static void copy(Player original, Player clone) {
        clone.getPersistentData().putString(KEY, original.getPersistentData().getString(KEY));
        clone.getPersistentData().putBoolean(STARTER_KEY, original.getPersistentData().getBoolean(STARTER_KEY));
        PlayerLevelData.copy(original, clone);
    }
}
