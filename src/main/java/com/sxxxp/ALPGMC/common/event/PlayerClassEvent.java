package com.sxxxp.ALPGMC.common.event;

import com.sxxxp.ALPGMC.common.command.ClassCommand;
import com.sxxxp.ALPGMC.common.playerclass.ClassAttributes;
import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClassData;
import com.sxxxp.ALPGMC.common.playerclass.PlayerLevelData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerClassEvent {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ClassCommand.register(event.getDispatcher());
    }

    // 접속할 때 저장된 직업을 클라이언트에 보냅니다. 직업이 없으면 클라이언트가 선택 화면을 엽니다.
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ClassAttributes.apply(player);
            PlayerClassData.sync(player);
            PlayerLevelData.sync(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerClassData.copy(event.getOriginal(), event.getEntity());

        if (event.getEntity() instanceof ServerPlayer player) {
            ClassAttributes.apply(player);
            // 엔드 귀환은 죽은 게 아니므로 수정치를 적용한 뒤 체력을 그대로 되돌립니다.
            if (!event.isWasDeath()) {
                player.setHealth(event.getOriginal().getHealth());
            }
        }
    }

    // 사망 후 부활하면 추가 체력까지 가득 채웁니다.
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.isEndConquered()) {
            event.getEntity().setHealth(event.getEntity().getMaxHealth());
        }
    }

    // 선택 화면이 떠 있는 동안 몹에게 죽지 않도록, 직업을 고르기 전에는 피해를 막습니다.
    @SubscribeEvent
    public static void onPlayerAttacked(LivingAttackEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && PlayerClassData.get(player) == null) {
            event.setCanceled(true);
        }
    }

    // 1.20.1에는 낙하 피해 attribute가 없어서 궁수의 낙하 피해는 이벤트로 줄입니다.
    @SubscribeEvent
    public static void onPlayerFall(LivingFallEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && PlayerClassData.get(player) == PlayerClass.ARCHER) {
            float remaining = (float) (1.0D - ClassConfig.ARCHER_FALL_DAMAGE_REDUCTION.get());
            event.setDamageMultiplier(event.getDamageMultiplier() * remaining);
        }
    }
}
