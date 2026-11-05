package com.sxxxp.ALPGMC.api.alchemy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

// 연금술 작업대에서 만들 수 있는 포션 효과의 정의입니다.
// 레지스트리 로딩 순서 문제를 피하려고 MobEffect는 Supplier로 받습니다.
public record AlchemyEffect(ResourceLocation id, Supplier<MobEffect> effect, int baseDurationTicks) {
}
