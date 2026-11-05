package com.sxxxp.ALPGMC.common.entity;

import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.common.playerclass.PlayerLevelData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.UUID;

public class CustomZombieEntity extends Zombie implements LeveledMob {
    private static final UUID LEVEL_HEALTH = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1010");
    private static final UUID LEVEL_DAMAGE = UUID.fromString("7f3a1c10-5b1e-4a60-9f01-1c0a7e2d1011");
    private static final double PLAYER_SEARCH_RANGE = 128.0D;

    // 머리 위 이름에 레벨을 보여주기 위해 클라이언트에 동기화합니다.
    private static final EntityDataAccessor<Integer> LEVEL =
            SynchedEntityData.defineId(CustomZombieEntity.class, EntityDataSerializers.INT);

    public CustomZombieEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LEVEL, 1);
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
    // 좀비는 스폰할 때 전용 속성을 읽으므로 반드시 Zombie.createAttributes()에서 시작해야 합니다.
    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)       // 체력 30 (하트 15개)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)   // 이동 속도
                .add(Attributes.ATTACK_DAMAGE, 5.0D)     // 기본 대미지 5
                .add(Attributes.ARMOR, 4.0D);            // 기본 방어력 4
    }

    // C. 스폰할 때 가장 가까운 플레이어의 레벨을 따라갑니다.
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnData, CompoundTag dataTag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);

        Player nearest = level.getNearestPlayer(this, PLAYER_SEARCH_RANGE);
        setMobLevel(nearest == null ? 1 : PlayerLevelData.getLevel(nearest));
        return result;
    }

    private void setMobLevel(int mobLevel) {
        this.entityData.set(LEVEL, mobLevel);
        scale(Attributes.MAX_HEALTH, LEVEL_HEALTH, ClassConfig.MONSTER_HEALTH_PER_LEVEL.get() * (mobLevel - 1));
        scale(Attributes.ATTACK_DAMAGE, LEVEL_DAMAGE, ClassConfig.MONSTER_DAMAGE_PER_LEVEL.get() * (mobLevel - 1));
        this.setHealth(this.getMaxHealth());
    }

    private void scale(Attribute attribute, UUID id, double amount) {
        AttributeInstance instance = this.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(id);
            instance.addPermanentModifier(new AttributeModifier(id, "alpgmc mob level", amount, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    @Override
    public int getMobLevel() {
        return this.entityData.get(LEVEL);
    }

    // 강화 수치는 속성에 함께 저장되므로 레벨 숫자만 저장하면 됩니다.
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MobLevel", getMobLevel());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(LEVEL, Math.max(1, tag.getInt("MobLevel")));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("entity.alpgmc.leveled_name", getMobLevel(), super.getDisplayName());
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    // 정예 좀비는 아기로 태어나거나 물에 빠져 드라운드로 바뀌지 않습니다.
    @Override
    public void setBaby(boolean baby) {
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }
}
