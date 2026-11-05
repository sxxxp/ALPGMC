package com.sxxxp.ALPGMC.common.event;

import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClassData;
import com.sxxxp.ALPGMC.common.playerclass.PlayerLevelData;
import com.sxxxp.ALPGMC.common.playerclass.PlayerStat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClassCombatEvent {
    private static final UUID MOUNT_SPEED = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1006");
    private static final double VEHICLE_BASE_SPEED = 0.4D; // 보트와 광산수레의 기본 최고 속도 (블록/틱)

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        float amount = event.getAmount();

        if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
            // 원거리 스탯은 attribute가 없어서 화살 피해에 직접 곱합니다.
            if (event.getSource().getDirectEntity() instanceof AbstractArrow) {
                amount *= (float) (1.0D + PlayerStat.RANGED.bonus(PlayerLevelData.getStat(attacker, PlayerStat.RANGED)));
            }
            // 힐러가 입히는 모든 피해를 줄입니다. 소환수의 공격은 공격자가 소환수라서 여기에 걸리지 않습니다.
            if (PlayerClassData.get(attacker) == PlayerClass.HEALER) {
                amount *= (float) (1.0D - ClassConfig.HEALER_ATTACK_DAMAGE_PENALTY.get());
            }
        }
        if (event.getEntity() instanceof ServerPlayer target && PlayerClassData.get(target) == PlayerClass.TANK) {
            amount *= (float) (1.0D - ClassConfig.TANK_DAMAGE_REDUCTION.get());
        }

        event.setAmount(amount);
    }

    // 말처럼 이동속도 attribute가 있는 탈것은 탱커가 타 있는 동안에만 느려집니다.
    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        if (!(event.getEntityMounting() instanceof ServerPlayer player) || !(event.getEntityBeingMounted() instanceof LivingEntity mount)) return;

        AttributeInstance speed = mount.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        speed.removeModifier(MOUNT_SPEED);
        if (event.isMounting() && PlayerClassData.get(player) == PlayerClass.TANK) {
            speed.addTransientModifier(new AttributeModifier(MOUNT_SPEED, "alpgmc tank mount speed",
                    -ClassConfig.TANK_MOVE_SPEED_PENALTY.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    // 보트와 광산수레는 속도 attribute가 없어서 최고 속도를 직접 제한합니다.
    // 보트는 클라이언트가 움직이므로 양쪽에서 모두 실행하되, 클라이언트에서는 본인만 처리합니다.
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (!(player instanceof ServerPlayer) && !player.isLocalPlayer()) return;
        if (PlayerClassData.get(player) != PlayerClass.TANK) return;

        Entity vehicle = player.getVehicle();
        boolean slowed = vehicle instanceof AbstractMinecart
                || (vehicle instanceof Boat && vehicle.getControllingPassenger() == player);
        if (!slowed) return;

        double limit = VEHICLE_BASE_SPEED * (1.0D - ClassConfig.TANK_MOVE_SPEED_PENALTY.get());
        Vec3 motion = vehicle.getDeltaMovement();
        double speed = motion.horizontalDistance();
        if (speed > limit) {
            double scale = limit / speed;
            vehicle.setDeltaMovement(motion.x * scale, motion.y, motion.z * scale);
        }
    }
}
