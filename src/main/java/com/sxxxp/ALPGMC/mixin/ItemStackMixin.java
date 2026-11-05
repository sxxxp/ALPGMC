package com.sxxxp.ALPGMC.mixin;

import com.sxxxp.ALPGMC.api.gear.GearDurability;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

// 플레이어 장비는 내구도가 0이 되어도 사라지지 않게 합니다. 바닐라가 아이템을 지우는 것을 막는 이벤트가 없어서 믹스인을 씁니다.
// 몹의 장비와 크리에이티브 모드는 바닐라 동작을 그대로 따릅니다.
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "hurtAndBreak", at = @At("HEAD"), cancellable = true)
    private <T extends LivingEntity> void alpgmc$keepBrokenGear(int amount, T entity, Consumer<T> onBroken, CallbackInfo ci) {
        ItemStack self = (ItemStack) (Object) this;
        if (!(entity instanceof ServerPlayer player) || player.getAbilities().instabuild || !self.isDamageableItem()) return;

        ci.cancel();
        if (GearDurability.isBroken(self)) return;

        int damage = self.getItem().damageItem(self, amount, entity, onBroken);
        if (self.hurt(damage, player.getRandom(), player)) {
            self.setDamageValue(self.getMaxDamage());
            player.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 0.8F, 1.0F);
            player.displayClientMessage(Component.translatable("message.alpgmc.gear_broken"), true);
        }
    }
}
