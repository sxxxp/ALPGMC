package com.sxxxp.ALPGMC.mixin;

import com.sxxxp.ALPGMC.client.ClientClassData;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin {

    // 탱커에게는 모루 화면에도 "너무 비쌈" 문구를 띄우지 않습니다.
    @ModifyConstant(method = "renderLabels", constant = @Constant(intValue = 40))
    private int alpgmc$hideTooExpensive(int limit) {
        return ClientClassData.get() == PlayerClass.TANK ? Integer.MAX_VALUE : limit;
    }
}
