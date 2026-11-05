package com.sxxxp.ALPGMC.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

// 서버 -> 클라이언트: 스탯 창에 보여줄 레벨, 경험치, 포인트, 스탯, 전투 갈래를 보냅니다.
public class LevelSyncPacket {
    private final int level;
    private final int xp;
    private final int xpToNext;
    private final int points;
    private final int[] stats;
    private final String branch;

    public LevelSyncPacket(int level, int xp, int xpToNext, int points, int[] stats, String branch) {
        this.level = level;
        this.xp = xp;
        this.xpToNext = xpToNext;
        this.points = points;
        this.stats = stats;
        this.branch = branch;
    }

    public static LevelSyncPacket decode(FriendlyByteBuf buf) {
        return new LevelSyncPacket(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readVarIntArray(), buf.readUtf(32));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.level);
        buf.writeInt(this.xp);
        buf.writeInt(this.xpToNext);
        buf.writeInt(this.points);
        buf.writeVarIntArray(this.stats);
        buf.writeUtf(this.branch, 32);
    }

    public static void handle(LevelSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            com.sxxxp.ALPGMC.client.ClientLevelData.set(packet.level, packet.xp, packet.xpToNext, packet.points, packet.stats, packet.branch);
        });
        context.setPacketHandled(true);
    }
}
