package com.sxxxp.ALPGMC.common.event;

import com.sxxxp.ALPGMC.common.entity.LeveledMob;
import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.common.playerclass.PlayerGold;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GoldRewardEvent {

    // 적대 몹을 처치하면 골드가 바닥에 떨어지지 않고 처치한 플레이어의 잔액에 바로 들어갑니다.
    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        Entity killer = event.getSource().getEntity();

        // 힐러가 소환수로 사냥해도 골드를 받도록, 길들인 동물의 처치는 주인의 처치로 봅니다.
        if (killer instanceof TamableAnimal pet) {
            killer = pet.getOwner();
        }
        if (!(target instanceof Enemy) || !(killer instanceof ServerPlayer player)) return;

        int amount = ClassConfig.GOLD_FROM_VANILLA_ENEMY.get();
        if (target instanceof LeveledMob leveled) {
            amount = ClassConfig.GOLD_FROM_MOD_MONSTER.get()
                    + (int) (ClassConfig.GOLD_PER_MONSTER_LEVEL.get() * (leveled.getMobLevel() - 1));
        }
        if (amount <= 0) return;

        // 궁수의 사냥꾼 드랍 보너스는 골드에도 같은 확률로 적용됩니다.
        if (player.getRandom().nextDouble() < BranchAbilityEvent.hunterDropChance(player)) {
            amount *= 2;
        }

        PlayerGold.add(player, amount);
        player.displayClientMessage(Component.translatable("message.alpgmc.gold_gained", amount), true);
    }
}
