package com.sxxxp.ALPGMC.common.playerclass;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

// 플레이어의 골드 잔액을 NBT에 저장합니다. 아이템이 아니므로 사망해도 잃지 않습니다.
public class PlayerGold {
    private static final String KEY = "alpgmc_gold";

    // 서버 값을 읽습니다. 클라이언트 화면은 ClientLevelData.getGold를 씁니다.
    public static int get(Player player) {
        return player.getPersistentData().getInt(KEY);
    }

    public static void add(ServerPlayer player, int amount) {
        // int 범위를 넘어 음수가 되지 않도록 합을 long으로 계산합니다.
        long total = (long) get(player) + amount;
        player.getPersistentData().putInt(KEY, (int) Math.min(total, Integer.MAX_VALUE));
        PlayerLevelData.sync(player);
    }

    // 잔액이 충분할 때만 차감하고 성공 여부를 돌려줍니다.
    public static boolean spend(ServerPlayer player, int amount) {
        int gold = get(player);
        if (amount < 0 || gold < amount) return false;

        player.getPersistentData().putInt(KEY, gold - amount);
        PlayerLevelData.sync(player);
        return true;
    }

    public static void copy(Player original, Player clone) {
        clone.getPersistentData().putInt(KEY, get(original));
    }
}
