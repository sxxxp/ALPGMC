package com.sxxxp.ALPGMC.common.playerclass;

import java.util.function.Supplier;

// 레벨업 포인트로 찍는 스탯입니다. 포인트당 수치는 ClassConfig에서 조절합니다.
public enum PlayerStat {
    HEALTH("health", false, ClassConfig.STAT_HEALTH_PER_POINT::get),
    MELEE("melee", true, ClassConfig.STAT_MELEE_PER_POINT::get),
    RANGED("ranged", true, ClassConfig.STAT_RANGED_PER_POINT::get),
    DEFENSE("defense", false, ClassConfig.STAT_DEFENSE_PER_POINT::get),
    SPEED("speed", true, ClassConfig.STAT_SPEED_PER_POINT::get),
    MANA("mana", false, ClassConfig.STAT_MANA_PER_POINT::get);

    private final String id;
    private final boolean percent;
    private final Supplier<Double> perPoint;

    PlayerStat(String id, boolean percent, Supplier<Double> perPoint) {
        this.id = id;
        this.percent = percent;
        this.perPoint = perPoint;
    }

    public String getId() {
        return id;
    }

    public double bonus(int points) {
        return points * this.perPoint.get();
    }

    // 스탯 창에 보여줄 현재 보너스입니다. (예: +6%, +1.5)
    public String bonusText(int points) {
        double bonus = bonus(points);
        return this.percent ? String.format("+%.0f%%", bonus * 100.0D) : String.format("+%.1f", bonus);
    }

    // 마나는 마법사만 쓰므로 다른 직업에게는 보여주지 않습니다.
    public boolean isAvailableFor(PlayerClass playerClass) {
        return this != MANA || playerClass == PlayerClass.MAGE;
    }
}
