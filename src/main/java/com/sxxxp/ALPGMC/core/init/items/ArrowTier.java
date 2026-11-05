package com.sxxxp.ALPGMC.core.init.items;

// 궁수 전용 화살 등급입니다. 등급을 추가하려면 여기에 값을 넣고 ItemInit, ClassConfig, 텍스처, 레시피를 함께 추가합니다.
public enum ArrowTier {
    WOOD("wood"),
    FLINT("flint"),
    IRON("iron"),
    DIAMOND("diamond");

    private final String id;

    ArrowTier(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
