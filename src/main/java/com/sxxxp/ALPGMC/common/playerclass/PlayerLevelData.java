package com.sxxxp.ALPGMC.common.playerclass;

import com.sxxxp.ALPGMC.core.network.LevelSyncPacket;
import com.sxxxp.ALPGMC.core.network.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;

// 바닐라 경험치와 별개인 모드 레벨과 스탯 포인트를 플레이어 NBT에 저장합니다.
public class PlayerLevelData {
    private static final String KEY = "alpgmc_level";
    private static final String LEVEL = "Level";
    private static final String XP = "Xp";
    private static final String POINTS = "Points";
    private static final String STAT_PREFIX = "Stat_";

    private static CompoundTag data(Player player) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(KEY)) {
            CompoundTag tag = new CompoundTag();
            tag.putInt(LEVEL, 1);
            root.put(KEY, tag);
        }
        return root.getCompound(KEY);
    }

    public static int getLevel(Player player) {
        return data(player).getInt(LEVEL);
    }

    public static int getStat(Player player, PlayerStat stat) {
        return data(player).getInt(STAT_PREFIX + stat.getId());
    }

    // 다음 레벨까지 필요한 경험치입니다. 최고 레벨이면 0을 반환합니다.
    public static int xpToNext(int level) {
        if (level >= ClassConfig.LEVEL_MAX.get()) return 0;
        return ClassConfig.LEVEL_XP_BASE.get() + ClassConfig.LEVEL_XP_PER_LEVEL.get() * (level - 1);
    }

    public static void addXp(ServerPlayer player, int amount) {
        CompoundTag tag = data(player);
        int level = tag.getInt(LEVEL);
        int xp = tag.getInt(XP) + amount;
        int gained = 0;

        // 한 번에 여러 레벨이 오를 수 있으므로 반복해서 확인합니다.
        while (xpToNext(level) > 0 && xp >= xpToNext(level)) {
            xp -= xpToNext(level);
            level++;
            gained++;
        }
        // 최고 레벨에서는 경험치를 더 모으지 않습니다.
        if (xpToNext(level) == 0) {
            xp = 0;
        }

        tag.putInt(LEVEL, level);
        tag.putInt(XP, xp);
        if (gained > 0) {
            tag.putInt(POINTS, tag.getInt(POINTS) + gained * ClassConfig.LEVEL_STAT_POINTS.get());
            player.displayClientMessage(Component.translatable("message.alpgmc.level_up", level), true);
            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        sync(player);
    }

    // 서버에서 다시 검사하므로 클라이언트가 보낸 요청을 그대로 믿지 않습니다.
    public static void spend(ServerPlayer player, PlayerStat stat) {
        CompoundTag tag = data(player);
        int current = getStat(player, stat);
        if (tag.getInt(POINTS) <= 0 || current >= ClassConfig.LEVEL_MAX_STAT_POINTS.get()) return;
        if (!stat.isAvailableFor(PlayerClassData.get(player))) return;

        tag.putInt(POINTS, tag.getInt(POINTS) - 1);
        tag.putInt(STAT_PREFIX + stat.getId(), current + 1);
        ClassAttributes.apply(player);
        sync(player);
    }

    // 바닐라 경험치 레벨을 비용으로 내고 찍은 포인트를 전부 돌려받습니다.
    public static void reset(ServerPlayer player) {
        int cost = ClassConfig.LEVEL_RESET_COST.get();
        if (player.experienceLevel < cost) {
            player.displayClientMessage(Component.translatable("message.alpgmc.reset_no_xp", cost), true);
            return;
        }

        CompoundTag tag = data(player);
        int refund = 0;
        for (PlayerStat stat : PlayerStat.values()) {
            refund += getStat(player, stat);
            tag.remove(STAT_PREFIX + stat.getId());
        }
        if (refund == 0) return;

        player.giveExperienceLevels(-cost);
        tag.putInt(POINTS, tag.getInt(POINTS) + refund);
        ClassAttributes.apply(player);
        sync(player);
    }

    public static void sync(ServerPlayer player) {
        CompoundTag tag = data(player);
        int[] stats = new int[PlayerStat.values().length];
        for (PlayerStat stat : PlayerStat.values()) {
            stats[stat.ordinal()] = getStat(player, stat);
        }
        PacketHandler.INSTANCE.sendTo(
                new LevelSyncPacket(tag.getInt(LEVEL), tag.getInt(XP), xpToNext(tag.getInt(LEVEL)), tag.getInt(POINTS), stats),
                player.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
        );
    }

    public static void copy(Player original, Player clone) {
        clone.getPersistentData().put(KEY, data(original).copy());
    }
}
