package com.sxxxp.ALPGMC.common.event;

import com.sxxxp.ALPGMC.common.ally.AllyRules;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AllyEvent {

    // 투사체는 아군을 맞히지 않고 통과해서 뒤의 적까지 날아갑니다.
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getRayTraceResult() instanceof EntityHitResult hit)) return;

        Projectile projectile = event.getProjectile();
        if (isBeneficialPotion(projectile)) return; // 아군에게 던진 치유 포션은 통과하면 안 됩니다.

        if (AllyRules.isAlly(projectile, hit.getEntity())) {
            event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
        }
    }

    private static boolean isBeneficialPotion(Projectile projectile) {
        if (!(projectile instanceof ThrownPotion potion)) return false;

        List<MobEffectInstance> effects = PotionUtils.getMobEffects(potion.getItem());
        return !effects.isEmpty() && effects.stream().allMatch(effect -> effect.getEffect().getCategory() == MobEffectCategory.BENEFICIAL);
    }

    // 휩쓸기, 폭발, 앞으로 만들 범위 스킬까지 공격자가 있는 모든 피해를 여기서 막습니다.
    // 자기 폭발에 자기가 맞는 경우는 아군 피해가 아니므로 그대로 둡니다.
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker == null || AllyRules.controllerOf(attacker) == event.getEntity()) return;

        if (AllyRules.isAlly(attacker, event.getEntity())) {
            event.setCanceled(true);
        }
    }
}
