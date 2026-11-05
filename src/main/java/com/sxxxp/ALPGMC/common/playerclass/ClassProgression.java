package com.sxxxp.ALPGMC.common.playerclass;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

// 레벨에 따른 갈래와 생산 트리 해금을 판단합니다. 레벨을 읽으므로 서버에서만 호출합니다.
// 단계 0은 1레벨부터 쓸 수 있는 생산 능력이고, 단계 1~4는 갈래 선택 후 순서대로 열리는 능력입니다.
public class ClassProgression {
    private static final String BRANCH_KEY = "alpgmc_branch";

    public static int tierLevel(int tier) {
        return switch (tier) {
            case 0 -> 1;
            case 1 -> ClassConfig.TIER_1_LEVEL.get();
            case 2 -> ClassConfig.TIER_2_LEVEL.get();
            case 3 -> ClassConfig.TIER_3_LEVEL.get();
            default -> ClassConfig.TIER_4_LEVEL.get();
        };
    }

    public static PlayerBranch getBranch(Player player) {
        return PlayerBranch.fromId(player.getPersistentData().getString(BRANCH_KEY));
    }

    public static boolean hasBranchTier(Player player, PlayerBranch branch, int tier) {
        return getBranch(player) == branch && PlayerLevelData.getLevel(player) >= tierLevel(tier);
    }

    public static boolean hasProductionTier(Player player, PlayerClass playerClass, int tier) {
        return PlayerClassData.get(player) == playerClass && PlayerLevelData.getLevel(player) >= tierLevel(tier);
    }

    // 플레이어의 선택 요청입니다. 서버에서 다시 검사하므로 클라이언트가 보낸 요청을 그대로 믿지 않습니다.
    public static void choose(ServerPlayer player, PlayerBranch branch) {
        PlayerBranch current = getBranch(player);
        if (current == branch || branch.getPlayerClass() != PlayerClassData.get(player)) return;
        if (PlayerLevelData.getLevel(player) < tierLevel(1)) return;

        // 처음 고를 때는 무료이고, 바꿀 때만 바닐라 경험치 레벨을 냅니다.
        if (current != null) {
            int cost = ClassConfig.BRANCH_CHANGE_COST.get();
            if (player.experienceLevel < cost) {
                player.displayClientMessage(Component.translatable("message.alpgmc.branch_no_xp", cost), true);
                return;
            }
            player.giveExperienceLevels(-cost);
        }
        set(player, branch);
    }

    // 관리자 명령어처럼 비용과 레벨을 따지지 않고 바로 지정합니다.
    public static void set(ServerPlayer player, PlayerBranch branch) {
        player.getPersistentData().putString(BRANCH_KEY, branch.getId());
        PlayerLevelData.sync(player);
    }

    // /class set으로 직업이 바뀌면 다른 직업의 갈래가 남지 않도록 지웁니다.
    public static void clearIfMismatch(ServerPlayer player, PlayerClass playerClass) {
        PlayerBranch branch = getBranch(player);
        if (branch != null && branch.getPlayerClass() != playerClass) {
            player.getPersistentData().remove(BRANCH_KEY);
            PlayerLevelData.sync(player);
        }
    }

    public static void copy(Player original, Player clone) {
        clone.getPersistentData().putString(BRANCH_KEY, original.getPersistentData().getString(BRANCH_KEY));
    }
}
