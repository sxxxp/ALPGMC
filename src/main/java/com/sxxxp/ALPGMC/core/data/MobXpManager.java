package com.sxxxp.ALPGMC.core.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class MobXpManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    public static final Map<String, Integer> MOB_XP_TABLE = new HashMap<>();

    public static final MobXpManager INSTANCE = new MobXpManager();

    private MobXpManager() {
        super(GSON, "xp_tables");
    }
    public int getMobXp(String mobId) {
        return MOB_XP_TABLE.getOrDefault(mobId, 10);
    }
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        MOB_XP_TABLE.clear();

        ResourceLocation targetFile = new ResourceLocation("alpgmc", "mob_xp");
        if (object.containsKey(targetFile)) {
            JsonElement element = object.get(targetFile);
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    try {
                        String mobId = entry.getKey();
                        int xpValue = entry.getValue().getAsInt();
                        MOB_XP_TABLE.put(mobId, xpValue);
                    } catch (Exception e) {
                        System.err.println("[ALPGMC] Failed to parse XP for: " + entry.getKey());
                    }
                }
            }
        }
        System.out.println("[ALPGMC] Successfully loaded " + MOB_XP_TABLE.size() + " mob XP entries from JSON.");
    }
}