package com.sxxxp.ALPGMC.core.init.items;

public enum WeaponCategory {
    // === 스태프 ===
    STAFF(MainCategory.STAFF),
    ARCANE_STAFF(MainCategory.STAFF),
    HOLY_STAFF(MainCategory.STAFF),
    // === 완드 ===
    WAND(MainCategory.WAND),
    ELEMENTAL_WAND(MainCategory.WAND),
    SKULL_WAND(MainCategory.WAND),
    // === 검 ===
    LONG_SWORD(MainCategory.SWORD),
    SHORT_SWORD(MainCategory.SWORD),
    TWIN_SWORD(MainCategory.SWORD),
    // === 단검 ===
    TWIN_DAGGER(MainCategory.DAGGER),
    ONE_HAND_DAGGER(MainCategory.DAGGER),
    // === 도끼 ===
    ONE_HAND_AXE(MainCategory.AXE),
    TWO_HAND_AXE(MainCategory.AXE),
    TWIN_AXE(MainCategory.AXE),
    // === 마법서 ===
    MAGIC_BOOK(MainCategory.MAGIC_BOOK),
    HOLY_MAGIC_BOOK(MainCategory.MAGIC_BOOK),
    FORBIDDEN_MAGIC_BOOK(MainCategory.MAGIC_BOOK),
    // === 활 ===
    SHORT_BOW(MainCategory.BOW),
    LONG_BOW(MainCategory.BOW),
    CROSS_BOW(MainCategory.BOW),
    BIG_BOW(MainCategory.BOW),
    // === 인파이팅 ===
    CLAW(MainCategory.INFIGHTING),
    GAUNTLET(MainCategory.INFIGHTING),
    // === 횃불 ===
    TORCH(MainCategory.TORCH),
    LANTERN(MainCategory.TORCH),
    // === 방패 ===
    SMALL_SHIELD(MainCategory.SHIELD),
    SHIELD(MainCategory.SHIELD),
    // === 화살통 ===
    IRON_QUIVER(MainCategory.QUIVER),
    SILVER_QUIVER(MainCategory.QUIVER),
    POISON_QUIVER(MainCategory.QUIVER);
    private final MainCategory main;
    WeaponCategory(MainCategory main) { this.main = main; }
    public MainCategory getMain() { return main; }
}
