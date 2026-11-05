package com.sxxxp.ALPGMC.common.playerclass;

import java.util.ArrayList;
import java.util.List;

// 직업마다 두 개씩 있는 전투 갈래입니다.
public enum PlayerBranch {
    BERSERKER("berserker", PlayerClass.WARRIOR),
    DUAL_WIELD("dual_wield", PlayerClass.WARRIOR),
    SNIPER("sniper", PlayerClass.ARCHER),
    CROSSBOW("crossbow", PlayerClass.ARCHER),
    ELEMENTALIST("elementalist", PlayerClass.MAGE),
    DEBUFFER("debuffer", PlayerClass.MAGE),
    SUMMONER("summoner", PlayerClass.HEALER),
    CLERIC("cleric", PlayerClass.HEALER),
    GUARDIAN("guardian", PlayerClass.TANK),
    GLADIATOR("gladiator", PlayerClass.TANK);

    private final String id;
    private final PlayerClass playerClass;

    PlayerBranch(String id, PlayerClass playerClass) {
        this.id = id;
        this.playerClass = playerClass;
    }

    public String getId() {
        return id;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    // 저장값이나 패킷으로 받은 ID를 갈래로 변환합니다. 없는 ID면 null을 반환합니다.
    public static PlayerBranch fromId(String id) {
        for (PlayerBranch branch : values()) {
            if (branch.id.equals(id)) {
                return branch;
            }
        }
        return null;
    }

    public static List<PlayerBranch> forClass(PlayerClass playerClass) {
        List<PlayerBranch> branches = new ArrayList<>();
        for (PlayerBranch branch : values()) {
            if (branch.playerClass == playerClass) {
                branches.add(branch);
            }
        }
        return branches;
    }
}
