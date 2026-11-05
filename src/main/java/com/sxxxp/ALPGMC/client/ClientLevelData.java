package com.sxxxp.ALPGMC.client;

import com.sxxxp.ALPGMC.common.playerclass.PlayerBranch;
import com.sxxxp.ALPGMC.common.playerclass.PlayerStat;

public class ClientLevelData {
    private static int level = 1;
    private static int xp = 0;
    private static int xpToNext = 0;
    private static int points = 0;
    private static int[] stats = new int[PlayerStat.values().length];
    private static PlayerBranch branch = null;

    public static void set(int newLevel, int newXp, int newXpToNext, int newPoints, int[] newStats, String newBranch) {
        level = newLevel;
        xp = newXp;
        xpToNext = newXpToNext;
        points = newPoints;
        stats = newStats;
        branch = PlayerBranch.fromId(newBranch);
    }

    public static int getLevel() {
        return level;
    }

    public static int getXp() {
        return xp;
    }

    public static int getXpToNext() {
        return xpToNext;
    }

    public static int getPoints() {
        return points;
    }

    public static int getStat(PlayerStat stat) {
        return stats[stat.ordinal()];
    }

    public static PlayerBranch getBranch() {
        return branch;
    }
}
