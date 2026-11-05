package com.sxxxp.ALPGMC.common.event;

import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.common.playerclass.ClassProgression;
import com.sxxxp.ALPGMC.common.playerclass.PlayerBranch;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

// 레벨로 해금되는 전투 갈래와 생산 트리의 패시브 효과입니다.
@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BranchAbilityEvent {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) return;

        float amount = event.getAmount();

        // 광전사: 잃은 체력 비율만큼 근접 공격력이 오릅니다. 체력이 0에 가까울수록 최대치에 가깝습니다.
        if (event.getSource().getDirectEntity() == attacker && ClassProgression.hasBranchTier(attacker, PlayerBranch.BERSERKER, 1)) {
            float missing = 1.0F - attacker.getHealth() / attacker.getMaxHealth();
            amount *= (float) (1.0D + ClassConfig.BERSERKER_MAX_DAMAGE_BONUS.get() * missing);
        }

        // 저격수: 바닐라 활은 끝까지 당긴 화살에 치명타 표시를 붙이므로 그것으로 최대 충전을 판단합니다.
        if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow && arrow.isCritArrow()
                && ClassProgression.hasBranchTier(attacker, PlayerBranch.SNIPER, 1)) {
            amount *= (float) (1.0D + ClassConfig.SNIPER_FULL_CHARGE_BONUS.get());
        }

        event.setAmount(amount);
    }

    // 광전사: 처치하면 체력을 회복합니다.
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer killer
                && ClassProgression.hasBranchTier(killer, PlayerBranch.BERSERKER, 3)) {
            killer.heal(ClassConfig.BERSERKER_KILL_HEAL.get().floatValue());
        }
    }

    // 궁수 사냥꾼: 드랍 묶음마다 확률로 한 묶음을 더 떨어뜨립니다.
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)) return;
        // 플레이어나 길들인 동물이 지닌 물건까지 복사되지 않도록 야생 몹만 대상으로 합니다.
        if (!(event.getEntity() instanceof Mob) || event.getEntity() instanceof OwnableEntity) return;
        if (!ClassProgression.hasProductionTier(killer, PlayerClass.ARCHER, 0)) return;

        double chance = ClassProgression.hasProductionTier(killer, PlayerClass.ARCHER, 1)
                ? ClassConfig.HUNTER_DROP_CHANCE_UPGRADED.get()
                : ClassConfig.HUNTER_DROP_CHANCE.get();

        for (ItemEntity drop : new ArrayList<>(event.getDrops())) {
            // 몹이 주워 든 장비가 복사되지 않도록 내구도가 있는 아이템은 제외합니다.
            if (drop.getItem().isDamageableItem() || killer.getRandom().nextDouble() >= chance) continue;

            event.getDrops().add(new ItemEntity(drop.level(), drop.getX(), drop.getY(), drop.getZ(), drop.getItem().copy()));
        }
    }
}
