package com.sxxxp.ALPGMC.common.entity;

import com.sxxxp.ALPGMC.core.init.EntityInit;
import com.sxxxp.ALPGMC.core.init.ItemInit;
import com.sxxxp.ALPGMC.core.init.items.ArrowTier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ClassArrowEntity extends AbstractArrow {
    // 렌더러가 등급별 텍스처를 고를 수 있도록 등급을 클라이언트에 동기화합니다.
    private static final EntityDataAccessor<Integer> TIER =
            SynchedEntityData.defineId(ClassArrowEntity.class, EntityDataSerializers.INT);

    public ClassArrowEntity(EntityType<? extends ClassArrowEntity> type, Level level) {
        super(type, level);
    }

    public ClassArrowEntity(Level level, LivingEntity shooter, ArrowTier tier) {
        super(EntityInit.CLASS_ARROW.get(), shooter, level);
        this.entityData.set(TIER, tier.ordinal());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TIER, ArrowTier.WOOD.ordinal());
    }

    public ArrowTier getTier() {
        return ArrowTier.values()[this.entityData.get(TIER)];
    }

    // 주울 때 쏜 등급 그대로 돌려줍니다.
    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ItemInit.arrowOf(getTier()));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Tier", getTier().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        String id = tag.getString("Tier");
        for (ArrowTier tier : ArrowTier.values()) {
            if (tier.getId().equals(id)) {
                this.entityData.set(TIER, tier.ordinal());
            }
        }
    }
}
