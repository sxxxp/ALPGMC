package com.sxxxp.ALPGMC.api.alchemy;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// 포션 효과 목록입니다. 새 효과는 직업 코드를 고치지 않고 여기에 등록만 하면 됩니다.
public class AlchemyEffectRegistry {
    private static final Map<ResourceLocation, AlchemyEffect> EFFECTS = new LinkedHashMap<>();

    public static AlchemyEffect register(AlchemyEffect effect) {
        if (EFFECTS.putIfAbsent(effect.id(), effect) != null) {
            throw new IllegalStateException("Duplicate alchemy effect " + effect.id());
        }
        return effect;
    }

    // 없는 ID면 null을 반환합니다.
    public static AlchemyEffect get(ResourceLocation id) {
        return EFFECTS.get(id);
    }

    public static Collection<AlchemyEffect> all() {
        return Collections.unmodifiableCollection(EFFECTS.values());
    }
}
