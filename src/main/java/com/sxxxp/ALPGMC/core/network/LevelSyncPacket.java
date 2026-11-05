package com.sxxxp.ALPGMC.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

// 서버 -> 클라이언트: 화면에 보여줄 레벨, 경험치, 포인트, 스탯, 전투 갈래, 골드를 보냅니다.
public class LevelSyncPacket {
    private final int level;
    private final int xp;
    private final int xpToNext;
    private final int points;
    private final int[] stats;
    private final String branch;
    private final int gold;

    public LevelSyncPacket(int level, int xp, int xpToNext, int points, int[] stats, String branch, int gold) {
        this.level = level;
        this.xp = xp;
        this.xpToNext = xpToNext;
        this.points = points;
        this.stats = stats;
        this.branch = branch;
        this.gold = gold;
    }

    public static LevelSyncPacket decode(FriendlyByteBuf buf) {
        return new LevelSyncPacket(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readVarIntArray(), buf.readUtf(32), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.level);
        buf.writeInt(this.xp);
        buf.writeInt(this.xpToNext);
        buf.writeInt(this.points);
        buf.writeVarIntArray(this.stats);
        buf.writeUtf(this.branch, 32);
        buf.writeInt(this.gold);
    }

    public static void handle(LevelSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            com.sxxxp.ALPGMC.client.ClientLevelData.set(packet.level, packet.xp, packet.xpToNext, packet.points, packet.stats, packet.branch, packet.gold);
        });
        context.setPacketHandled(true);
    }
}
