package com.sxxxp.ALPGMC.api;

import com.sxxxp.ALPGMC.common.playerclass.ClassProgression;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import net.minecraft.world.entity.player.Player;

// 작업대가 직업을 몰라도 되도록, 생산 기능의 사용 가능 여부는 이 enum에게만 묻습니다.
public enum ProductionAction {
    IDENTIFY(PlayerClass.MAGE, 0),
    ADD_OPTION(PlayerClass.MAGE, 1),
    REROLL_OPTION(PlayerClass.MAGE, 2),
    LOCK_OPTION(PlayerClass.MAGE, 3),
    UPGRADE_OPTION_TIER(PlayerClass.MAGE, 4),

    BREW_BASIC(PlayerClass.HEALER, 0),
    BREW_BOOSTED(PlayerClass.HEALER, 1),
    BREW_COMBINE(PlayerClass.HEALER, 2),
    BREW_SIDE_EFFECT(PlayerClass.HEALER, 3),
    BREW_SPECIAL(PlayerClass.HEALER, 4),

    REPAIR_DISCOUNT(PlayerClass.TANK, 0),
    ENHANCE(PlayerClass.TANK, 1),
    ENHANCE_SAFE(PlayerClass.TANK, 2),
    DISASSEMBLE(PlayerClass.TANK, 3),
    ENHANCE_LIMIT_BREAK(PlayerClass.TANK, 4);

    private final PlayerClass playerClass;
    private final int tier;

    ProductionAction(PlayerClass playerClass, int tier) {
        this.playerClass = playerClass;
        this.tier = tier;
    }

    // 레벨을 읽으므로 서버에서만 호출합니다.
    public boolean canUse(Player player) {
        return ClassProgression.hasProductionTier(player, this.playerClass, this.tier);
    }

    // 클라이언트 화면은 동기화된 직업과 레벨로 미리 보여줄 때 이것을 씁니다. 실제 판단은 서버의 canUse가 합니다.
    public boolean isAvailable(PlayerClass playerClass, int level) {
        return playerClass == this.playerClass && level >= requiredLevel();
    }

    public int requiredLevel() {
        return ClassProgression.tierLevel(this.tier);
    }
}
