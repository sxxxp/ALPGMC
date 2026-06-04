package com.sxxxp.ALPGMC.common.event;

import com.sxxxp.ALPGMC.core.data.MobXpManager;
import com.sxxxp.ALPGMC.core.network.XpToastPacket;
import com.sxxxp.ALPGMC.core.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

        // 플레이어가 몹을 죽인 경우에만 작동하도록 필터링
        if (attacker instanceof Player player) {

            // 1. 죽은 몬스터의 고유 ID(Registry Key)를 가져옵니다. (예: alpgmc:custom_zombie)
            ResourceLocation mobId = ForgeRegistries.ENTITY_TYPES.getKey(targetMob.getType());
            if (mobId == null) return;

            String mobKey = mobId.toString();

            // 2. 앞서 만든 MobXpManager(JSON)에게 이 몬스터의 경험치 보상을 물어봅니다.
            int earnedXp = MobXpManager.INSTANCE.getMobXp(mobKey);

            // 3. 화면 UI에 띄울 몬스터의 이름(예: "정예 언데드 전사")을 추출합니다.
            String mobName = targetMob.getDisplayName().getString();

            // 4. 경험치 계산 및 지급은 물리 서버에서만 처리하므로, 서버 플레이어인지 검증 후 패킷 전송
            if (player instanceof ServerPlayer serverPlayer) {

                // 패킷 채널을 통해 해당 플레이어 클라이언트로 UI 출력 데이터 송신
                PacketHandler.INSTANCE.sendTo(
                        new XpToastPacket(earnedXp, mobName),
                        serverPlayer.connection.connection,
                        net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT
                );
            }
        }
    }
}