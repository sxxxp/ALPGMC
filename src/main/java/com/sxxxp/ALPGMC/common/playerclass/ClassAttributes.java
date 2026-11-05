package com.sxxxp.ALPGMC.common.playerclass;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class ClassAttributes {
    private static final UUID ATTACK_DAMAGE = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1001");
    private static final UUID ATTACK_SPEED = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1002");
    private static final UUID KNOCKBACK_RESISTANCE = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1003");
    private static final UUID MOVE_SPEED = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1004");
    private static final UUID MAX_HEALTH = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1005");
    private static final UUID STAT_HEALTH = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1007");
    private static final UUID STAT_MELEE = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1008");
    private static final UUID STAT_DEFENSE = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1009");
    private static final UUID STAT_SPEED = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d100a");

    // 기존 수정치를 지우고 현재 직업과 config 값으로 다시 적용합니다.
    public static void apply(ServerPlayer player) {
        clear(player, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE);
        clear(player, Attributes.ATTACK_SPEED, ATTACK_SPEED);
        clear(player, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE);
        clear(player, Attributes.MOVEMENT_SPEED, MOVE_SPEED);
        clear(player, Attributes.MAX_HEALTH, MAX_HEALTH);

        PlayerClass playerClass = PlayerClassData.get(player);
        if (playerClass != null) {
            switch (playerClass) {
                case WARRIOR -> {
                    add(player, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE, ClassConfig.WARRIOR_ATTACK_DAMAGE.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
                    add(player, Attributes.ATTACK_SPEED, ATTACK_SPEED, ClassConfig.WARRIOR_ATTACK_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
                    add(player, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE, ClassConfig.WARRIOR_KNOCKBACK_RESISTANCE.get(), AttributeModifier.Operation.ADDITION);
                }
                case ARCHER -> add(player, Attributes.MOVEMENT_SPEED, MOVE_SPEED, ClassConfig.ARCHER_MOVE_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
                case TANK -> {
                    add(player, Attributes.MAX_HEALTH, MAX_HEALTH, ClassConfig.TANK_MAX_HEALTH.get(), AttributeModifier.Operation.ADDITION);
                    add(player, Attributes.MOVEMENT_SPEED, MOVE_SPEED, -ClassConfig.TANK_MOVE_SPEED_PENALTY.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
                    add(player, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE, ClassConfig.TANK_KNOCKBACK_RESISTANCE.get(), AttributeModifier.Operation.ADDITION);
                }
                default -> {
                }
            }
        }

        // 스탯 포인트 보너스를 직업 수정치와 별개로 적용합니다. 직업 패널티는 MULTIPLY_TOTAL이라 스탯으로 뚫리지 않습니다.
        applyStat(player, Attributes.MAX_HEALTH, STAT_HEALTH, PlayerStat.HEALTH, AttributeModifier.Operation.ADDITION);
        applyStat(player, Attributes.ATTACK_DAMAGE, STAT_MELEE, PlayerStat.MELEE, AttributeModifier.Operation.MULTIPLY_BASE);
        applyStat(player, Attributes.ARMOR, STAT_DEFENSE, PlayerStat.DEFENSE, AttributeModifier.Operation.ADDITION);
        applyStat(player, Attributes.MOVEMENT_SPEED, STAT_SPEED, PlayerStat.SPEED, AttributeModifier.Operation.MULTIPLY_BASE);

        // 최대 체력이 줄어든 경우 현재 체력도 그에 맞춥니다.
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static void applyStat(ServerPlayer player, Attribute attribute, UUID id, PlayerStat stat, AttributeModifier.Operation operation) {
        clear(player, attribute, id);
        int points = PlayerLevelData.getStat(player, stat);
        if (points > 0) {
            add(player, attribute, id, stat.bonus(points), operation);
        }
    }

    private static void clear(ServerPlayer player, Attribute attribute, UUID id) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(id);
        }
    }

    // 영구 수정치로 저장해야 재접속할 때 탱커의 추가 체력이 잘리지 않습니다.
    private static void add(ServerPlayer player, Attribute attribute, UUID id, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.addPermanentModifier(new AttributeModifier(id, "alpgmc class bonus", amount, operation));
        }
    }
}
