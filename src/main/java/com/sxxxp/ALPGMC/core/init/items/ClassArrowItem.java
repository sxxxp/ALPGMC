package com.sxxxp.ALPGMC.core.init.items;

import com.sxxxp.ALPGMC.common.entity.ClassArrowEntity;
import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ClassArrowItem extends ArrowItem {
    private final ArrowTier tier;

    public ClassArrowItem(ArrowTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
        ClassArrowEntity arrow = new ClassArrowEntity(level, shooter, this.tier);
        arrow.setBaseDamage(ClassConfig.arrowDamage(this.tier));
        return arrow;
    }

    // true를 돌려주면 활이 화살을 소모하지 않고, 쏜 화살도 무한 인챈트처럼 주울 수 없게 됩니다.
    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
        return player.getRandom().nextDouble() < ClassConfig.ARCHER_ARROW_SAVE_CHANCE.get();
    }
}
