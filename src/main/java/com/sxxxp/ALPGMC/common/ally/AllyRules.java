package com.sxxxp.ALPGMC.common.ally;

import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;

// 아군 판정의 기준입니다. 투사체, 범위 공격, 포션, 앞으로 만들 스킬은 모두 이 클래스에 묻습니다.
// 파티 시스템이 생기면 isAlly만 고치면 됩니다.
public class AllyRules {
    private static final int MAX_OWNER_DEPTH = 4; // 소환수가 쏜 화살처럼 주인이 여러 단계일 수 있습니다.

    // 화살, 포션 구름, 소환수는 그것을 만든 주인으로 바꿔서 판단합니다.
    public static boolean isAlly(Entity attacker, Entity target) {
        Entity attackerRoot = controllerOf(attacker);
        Entity targetRoot = controllerOf(target);
        if (attackerRoot == null || targetRoot == null) return false;
        if (attackerRoot == targetRoot) return true;

        return attackerRoot instanceof Player && targetRoot instanceof Player && ClassConfig.ALLY_ALL_PLAYERS.get();
    }

    public static Entity controllerOf(Entity entity) {
        Entity current = entity;
        for (int i = 0; i < MAX_OWNER_DEPTH && current != null; i++) {
            Entity owner = null;
            if (current instanceof TraceableEntity traceable) {
                owner = traceable.getOwner();
            } else if (current instanceof OwnableEntity ownable) {
                owner = ownable.getOwner();
            }
            if (owner == null) break;
            current = owner;
        }
        return current;
    }

    // 이로운 효과는 아군에게만, 해로운 효과는 적에게만 걸립니다. 플레이어가 던진 포션에만 적용합니다.
    // 디스펜서나 마녀의 포션은 바닐라 규칙을 따르고, 중립 효과는 누구에게나 걸립니다.
    public static boolean canReceiveEffect(Entity thrower, LivingEntity target, MobEffect effect) {
        if (!(controllerOf(thrower) instanceof Player)) return true;

        // 즉시 치유는 언데드에게 피해를 주므로 적이어도 걸립니다. 힐러가 언데드를 공격하는 수단입니다.
        if (effect == MobEffects.HEAL && target.isInvertedHealAndHarm()) return true;

        MobEffectCategory category = effect.getCategory();
        if (category == MobEffectCategory.BENEFICIAL) return isAlly(thrower, target);
        if (category == MobEffectCategory.HARMFUL) return !isAlly(thrower, target);
        return true;
    }
}
