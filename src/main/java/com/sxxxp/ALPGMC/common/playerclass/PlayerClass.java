package com.sxxxp.ALPGMC.common.playerclass;

public enum PlayerClass {
    WARRIOR("warrior"),
    ARCHER("archer"),
    MAGE("mage"),
    HEALER("healer"),
    TANK("tank");

    private final String id;

    PlayerClass(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    // 저장값이나 패킷으로 받은 ID를 직업으로 변환합니다. 없는 ID면 null을 반환합니다.
    public static PlayerClass fromId(String id) {
        for (PlayerClass playerClass : values()) {
            if (playerClass.id.equals(id)) {
                return playerClass;
            }
        }
        return null;
    }
}
