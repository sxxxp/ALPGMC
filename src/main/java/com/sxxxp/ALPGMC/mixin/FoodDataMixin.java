package com.sxxxp.ALPGMC.mixin;

import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClassData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    // 음식 자연 회복에서만 회복량을 바꿉니다. 포션과 황금 사과는 여기를 거치지 않고, 배고픔 소모도 그대로입니다.
    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"),
            require = 1
    )
    private void alpgmc$scaleNaturalHealing(Player player, float amount) {
        PlayerClass playerClass = PlayerClassData.get(player);
        if (playerClass == PlayerClass.WARRIOR) {
            amount *= (float) (1.0D - ClassConfig.WARRIOR_FOOD_HEALING_PENALTY.get());
        } else if (playerClass == PlayerClass.HEALER) {
            amount *= ClassConfig.HEALER_FOOD_HEALING_MULTIPLIER.get().floatValue();
        }
        player.heal(amount);
    }
}
