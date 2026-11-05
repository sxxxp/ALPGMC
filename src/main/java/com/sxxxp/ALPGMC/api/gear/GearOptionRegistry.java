package com.sxxxp.ALPGMC.api.gear;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// 장비 옵션 목록입니다. 새 옵션은 직업 코드를 고치지 않고 여기에 등록만 하면 됩니다.
public class GearOptionRegistry {
    private static final Map<ResourceLocation, GearOption> OPTIONS = new LinkedHashMap<>();

    public static GearOption register(GearOption option) {
        if (OPTIONS.putIfAbsent(option.id(), option) != null) {
            throw new IllegalStateException("Duplicate gear option " + option.id());
        }
        return option;
    }

    // 없는 ID면 null을 반환합니다. 저장된 장비에 삭제된 옵션이 남아 있을 수 있어서 호출하는 쪽에서 확인해야 합니다.
    public static GearOption get(ResourceLocation id) {
        return OPTIONS.get(id);
    }

    public static Collection<GearOption> all() {
        return Collections.unmodifiableCollection(OPTIONS.values());
    }
}
