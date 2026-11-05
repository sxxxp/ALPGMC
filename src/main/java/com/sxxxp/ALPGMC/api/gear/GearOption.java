package com.sxxxp.ALPGMC.api.gear;

import net.minecraft.resources.ResourceLocation;

// 장비 옵션의 정의입니다. tierValues는 T1(최상)부터 T5(최하)까지의 수치입니다.
public record GearOption(ResourceLocation id, double[] tierValues) {
    public static final int BEST_TIER = 1;
    public static final int WORST_TIER = 5;

    public GearOption {
        if (tierValues.length != WORST_TIER) {
            throw new IllegalArgumentException("Gear option " + id + " needs " + WORST_TIER + " tier values");
        }
    }

    public double value(int tier) {
        return this.tierValues[tier - BEST_TIER];
    }
}
