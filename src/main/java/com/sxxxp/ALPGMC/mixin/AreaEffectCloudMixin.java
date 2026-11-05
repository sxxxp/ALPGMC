package com.sxxxp.ALPGMC.mixin;

import com.sxxxp.ALPGMC.common.ally.AllyRules;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// 잔류 포션 구름의 효과를 대상마다 아군 판정으로 거릅니다. ThrownPotionMixin과 같은 이유로 믹스인을 씁니다.
@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffect;applyInstantenousEffect(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/LivingEntity;ID)V"))
    private void alpgmc$filterInstantEffect(MobEffect effect, Entity source, Entity indirectSource, LivingEntity target, int amplifier, double strength) {
        if (AllyRules.canReceiveEffect((AreaEffectCloud) (Object) this, target, effect)) {
            effect.applyInstantenousEffect(source, indirectSource, target, amplifier, strength);
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean alpgmc$filterEffect(LivingEntity target, MobEffectInstance instance, Entity source) {
        return AllyRules.canReceiveEffect((AreaEffectCloud) (Object) this, target, instance.getEffect()) && target.addEffect(instance, source);
    }
}
