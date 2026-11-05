package com.sxxxp.ALPGMC.client;

import com.sxxxp.ALPGMC.api.gear.GearData;
import com.sxxxp.ALPGMC.api.gear.GearDurability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "alpgmc", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GearTooltipEvent {

    // 강화 수치를 아이템 이름 바로 아래에 보여줍니다.
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (GearDurability.isBroken(event.getItemStack())) {
            event.getToolTip().add(1, Component.translatable("tooltip.alpgmc.broken").withStyle(ChatFormatting.RED));
        }

        int enhancement = GearData.getEnhancement(event.getItemStack());
        if (enhancement > 0) {
            event.getToolTip().add(1, Component.translatable("tooltip.alpgmc.enhancement", enhancement).withStyle(ChatFormatting.GOLD));
        }
    }
}
