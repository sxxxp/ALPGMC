package com.sxxxp.ALPGMC.core.init;

import com.sxxxp.ALPGMC.core.init.items.ArcherBowItem;
import com.sxxxp.ALPGMC.core.init.items.ArrowTier;
import com.sxxxp.ALPGMC.core.init.items.ClassArrowItem;
import com.sxxxp.ALPGMC.core.init.items.RevenantArmorMaterial;
import com.sxxxp.ALPGMC.core.init.items.RevenantTier;
import com.sxxxp.ALPGMC.core.init.items.RobeMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "alpgmc");

    // 마법사 지팡이 (한 칸에 1개만 들어갑니다)
    public static final RegistryObject<Item> MAGE_STAFF =
            ITEMS.register("mage_staff", () -> new Item(new Item.Properties().stacksTo(1)));

    // 마법사가 착용할 수 있는 유일한 방어구입니다.
    public static final RegistryObject<Item> MAGE_ROBE_HOOD = robe("mage_robe_hood", ArmorItem.Type.HELMET);
    public static final RegistryObject<Item> MAGE_ROBE_TOP = robe("mage_robe_top", ArmorItem.Type.CHESTPLATE);
    public static final RegistryObject<Item> MAGE_ROBE_BOTTOM = robe("mage_robe_bottom", ArmorItem.Type.LEGGINGS);
    public static final RegistryObject<Item> MAGE_ROBE_SHOES = robe("mage_robe_shoes", ArmorItem.Type.BOOTS);

    // 궁수 전용 활 (내구도는 바닐라 활과 같습니다)
    public static final RegistryObject<Item> ARCHER_BOW =
            ITEMS.register("archer_bow", () -> new ArcherBowItem(new Item.Properties().durability(384)));

    public static final RegistryObject<Item> WOOD_ARROW = arrow(ArrowTier.WOOD);
    public static final RegistryObject<Item> FLINT_ARROW = arrow(ArrowTier.FLINT);
    public static final RegistryObject<Item> IRON_ARROW = arrow(ArrowTier.IRON);
    public static final RegistryObject<Item> DIAMOND_ARROW = arrow(ArrowTier.DIAMOND);

    // 정예 좀비가 떨어뜨리는 제작 재료
    public static final RegistryObject<Item> REVENANT_ESSENCE =
            ITEMS.register("revenant_essence", () -> new Item(new Item.Properties()));

    // 망자 장비 세트
    public static final RegistryObject<Item> REVENANT_HELMET = revenantArmor("revenant_helmet", ArmorItem.Type.HELMET);
    public static final RegistryObject<Item> REVENANT_CHESTPLATE = revenantArmor("revenant_chestplate", ArmorItem.Type.CHESTPLATE);
    public static final RegistryObject<Item> REVENANT_LEGGINGS = revenantArmor("revenant_leggings", ArmorItem.Type.LEGGINGS);
    public static final RegistryObject<Item> REVENANT_BOOTS = revenantArmor("revenant_boots", ArmorItem.Type.BOOTS);
    public static final RegistryObject<Item> REVENANT_SWORD =
            ITEMS.register("revenant_sword", () -> new SwordItem(RevenantTier.INSTANCE, 3, -2.4F, new Item.Properties()));

    public static final RegistryObject<Item> CUSTOM_ZOMBIE_SPAWN_EGG =
            ITEMS.register("custom_zombie_spawn_egg", () -> new ForgeSpawnEggItem(EntityInit.CUSTOM_ZOMBIE, 0x2E2440, 0xB03A48, new Item.Properties()));

    private static RegistryObject<Item> revenantArmor(String name, ArmorItem.Type type) {
        return ITEMS.register(name, () -> new ArmorItem(RevenantArmorMaterial.INSTANCE, type, new Item.Properties()));
    }

    private static RegistryObject<Item> robe(String name, ArmorItem.Type type) {
        return ITEMS.register(name, () -> new ArmorItem(RobeMaterial.INSTANCE, type, new Item.Properties()));
    }

    private static RegistryObject<Item> arrow(ArrowTier tier) {
        return ITEMS.register(tier.getId() + "_arrow", () -> new ClassArrowItem(tier, new Item.Properties()));
    }

    public static Item arrowOf(ArrowTier tier) {
        return switch (tier) {
            case WOOD -> WOOD_ARROW.get();
            case FLINT -> FLINT_ARROW.get();
            case IRON -> IRON_ARROW.get();
            case DIAMOND -> DIAMOND_ARROW.get();
        };
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
