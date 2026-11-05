package com.sxxxp.ALPGMC.core.init.items;

import com.sxxxp.ALPGMC.common.playerclass.ClassConfig;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClassData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class ArcherBowItem extends BowItem {
    public ArcherBowItem(Properties properties) {
        super(properties);
    }

    // 궁수만 활을 당길 수 있습니다.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (PlayerClassData.get(player) != PlayerClass.ARCHER) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.alpgmc.class_restricted"), true);
            }
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    // 이 활에는 전용 화살만 맞습니다.
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() instanceof ClassArrowItem;
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow) {
        arrow.setBaseDamage(arrow.getBaseDamage() * (1.0D + ClassConfig.ARCHER_BOW_DAMAGE.get()));
        return arrow;
    }
}
