package com.sxxxp.ALPGMC.common.event;

import com.sxxxp.ALPGMC.common.playerclass.PlayerLevelData;
import com.sxxxp.ALPGMC.core.data.MobXpManager;
import com.sxxxp.ALPGMC.core.network.XpToastPacket;
import com.sxxxp.ALPGMC.core.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.event.entity.living.LivingDeathEvent; // ◀ 포지 순정 이벤트 임포트
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExperienceEvent {

    // 포지가 생명체 사망을 감지하면 이 메서드를 호출합니다.
    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        Entity targetMob = event.getEntity();                 // 죽은 몹
        Entity attacker = event.getSource().getEntity();      // 공격한 자

        // 힐러가 소환수로 사냥해도 경험치를 받도록, 길들인 동물의 처치는 주인의 처치로 봅니다.
        if (attacker instanceof TamableAnimal pet) {
            LivingEntity owner = pet.getOwner();
            attacker = owner;
        }

        // 플레이어가 몹을 죽인 경우에만 작동하도록 필터링
        if (attacker instanceof ServerPlayer serverPlayer) {

            // 1. 죽은 몬스터의 고유 ID(Registry Key)를 가져옵니다. (예: alpgmc:custom_zombie)
            ResourceLocation mobId = ForgeRegistries.ENTITY_TYPES.getKey(targetMob.getType());
            if (mobId == null) return;

            String mobKey = mobId.toString();

            // 표에 없는 동물이나 다른 플레이어로 경험치를 벌지 못하도록, 기본값은 적대 몹에게만 줍니다.
            if (!MobXpManager.MOB_XP_TABLE.containsKey(mobKey) && !(targetMob instanceof Enemy)) return;

            // 2. 앞서 만든 MobXpManager(JSON)에게 이 몬스터의 경험치 보상을 물어봅니다.
            int earnedXp = MobXpManager.INSTANCE.getMobXp(mobKey);

            // 3. 화면 UI에 띄울 몬스터의 이름(예: "정예 언데드 전사")을 추출합니다.
            String mobName = targetMob.getDisplayName().getString();

            // 4. 경험치를 지급하고, 해당 플레이어 클라이언트로 UI 출력 데이터 송신
            PlayerLevelData.addXp(serverPlayer, earnedXp);
            PacketHandler.INSTANCE.sendTo(
                    new XpToastPacket(earnedXp, mobName),
                    serverPlayer.connection.connection,
                    net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT
            );
        }
    }
}
