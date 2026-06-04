package com.sxxxp.ALPGMC.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CustomZombieEntity extends Zombie {

    public CustomZombieEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }
    // A. 몬스터의 AI 행동 방식 정의 (Goal 시스템)
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this)); // 물에 뜨기
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false)); // 근접 공격 AI (추적 속도 배율 1.2)
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D)); // 평소 방황하기
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F)); // 유저 쳐다보기

        // 타겟팅 AI (누구를 공격할 것인가)
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this)); // 선공한 대상 반격
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true)); // 눈앞의 플레이어 타겟팅
    }

    // B. 몬스터 기본 기본 스탯 정의 (최대 체력, 공격력 등)
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)       // 체력 30 (하트 15개)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)   // 이동 속도
                .add(Attributes.ATTACK_DAMAGE, 5.0D)     // 기본 대미지 5
                .add(Attributes.ARMOR, 4.0D);            // 기본 방어력 4
    }
}