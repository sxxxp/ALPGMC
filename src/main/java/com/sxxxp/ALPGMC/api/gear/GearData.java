package com.sxxxp.ALPGMC.api.gear;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

// 장비에 붙는 옵션, 감정 여부, 강화 수치를 아이템 NBT로 읽고 씁니다.
public class GearData {
    public static final int MAX_OPTIONS = 3;
    public static final int BASE_MAX_ENHANCEMENT = 5;
    public static final int MAX_ENHANCEMENT = 10;

    private static final String KEY = "alpgmc_gear";
    private static final String UNIDENTIFIED = "Unidentified";
    private static final String ENHANCEMENT = "Enhancement";
    private static final String OPTIONS = "Options";
    private static final String OPTION_ID = "Id";
    private static final String OPTION_TIER = "Tier";

    // 장비에 붙은 옵션 하나입니다.
    public record OptionEntry(ResourceLocation id, int tier) {
    }

    public static boolean isIdentified(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(KEY);
        return tag == null || !tag.getBoolean(UNIDENTIFIED);
    }

    public static void setIdentified(ItemStack stack, boolean identified) {
        stack.getOrCreateTagElement(KEY).putBoolean(UNIDENTIFIED, !identified);
    }

    public static int getEnhancement(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(KEY);
        return tag == null ? 0 : tag.getInt(ENHANCEMENT);
    }

    public static void setEnhancement(ItemStack stack, int enhancement) {
        stack.getOrCreateTagElement(KEY).putInt(ENHANCEMENT, Math.max(0, Math.min(enhancement, MAX_ENHANCEMENT)));
    }

    public static List<OptionEntry> getOptions(ItemStack stack) {
        List<OptionEntry> options = new ArrayList<>();
        CompoundTag tag = stack.getTagElement(KEY);
        if (tag == null) return options;

        ListTag list = tag.getList(OPTIONS, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            ResourceLocation id = ResourceLocation.tryParse(entry.getString(OPTION_ID));
            if (id != null) {
                options.add(new OptionEntry(id, entry.getInt(OPTION_TIER)));
            }
        }
        return options;
    }

    public static void setOptions(ItemStack stack, List<OptionEntry> options) {
        if (options.size() > MAX_OPTIONS) {
            throw new IllegalArgumentException("Gear can hold at most " + MAX_OPTIONS + " options");
        }

        ListTag list = new ListTag();
        for (OptionEntry option : options) {
            CompoundTag entry = new CompoundTag();
            entry.putString(OPTION_ID, option.id().toString());
            entry.putInt(OPTION_TIER, option.tier());
            list.add(entry);
        }
        stack.getOrCreateTagElement(KEY).put(OPTIONS, list);
    }
}
