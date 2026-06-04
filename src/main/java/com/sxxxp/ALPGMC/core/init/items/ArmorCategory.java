package com.sxxxp.ALPGMC.core.init.items;

public enum ArmorCategory {
    // === 천 ===
    CLOTH(MainCategory.CLOTH),
    SCHOLAR(MainCategory.CLOTH),
    CLERIC(MainCategory.CLOTH),
    FIEND(MainCategory.CLOTH),
    // === 가죽 ===
    LEATHER(MainCategory.LEATHER),
    HUNTER(MainCategory.LEATHER),
    ASSASSIN(MainCategory.LEATHER),
    MERCENARY(MainCategory.LEATHER),
    // === 경갑옷 ===
    LIGHT(MainCategory.LIGHT),
    SCOUT(MainCategory.LIGHT),
    SHADOW(MainCategory.LIGHT),
    WARDEN(MainCategory.LIGHT),
    // === 중갑옷 ===
    HEAVY(MainCategory.HEAVY),
    KNIGHT(MainCategory.HEAVY),
    VANGUARD(MainCategory.HEAVY),
    JUGGERNAUT(MainCategory.HEAVY),
    // === 판금갑옷 ===
    PLATE(MainCategory.PLATE),
    GUARDIAN(MainCategory.PLATE),
    PALADIN(MainCategory.PLATE),
    RUNE(MainCategory.PLATE);
    private final MainCategory main;
    ArmorCategory(MainCategory main) { this.main = main; }
    public MainCategory getMain() { return main; }

}
