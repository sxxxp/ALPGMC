package com.sxxxp.ALPGMC.core.init;

import com.sxxxp.ALPGMC.common.workstation.EnhancementAnvilMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuInit {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, "alpgmc");

    public static final RegistryObject<MenuType<EnhancementAnvilMenu>> ENHANCEMENT_ANVIL =
            MENUS.register("enhancement_anvil", () -> new MenuType<>(EnhancementAnvilMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
