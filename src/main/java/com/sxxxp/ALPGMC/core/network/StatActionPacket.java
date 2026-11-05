package com.sxxxp.ALPGMC.core.network;

import com.sxxxp.ALPGMC.common.playerclass.PlayerLevelData;
import com.sxxxp.ALPGMC.common.playerclass.PlayerStat;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

// 클라이언트 -> 서버: 스탯 창의 요청을 보냅니다. 값이 RESET이면 초기화, 그 외에는 해당 번호의 스탯에 포인트를 찍습니다.
public class StatActionPacket {
    public static final int RESET = -1;

    private final int action;

    public StatActionPacket(int action) {
        this.action = action;
    }

    public static StatActionPacket decode(FriendlyByteBuf buf) {
        return new StatActionPacket(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.action);
    }

    public static void handle(StatActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            if (packet.action == RESET) {
                PlayerLevelData.reset(player);
            } else if (packet.action >= 0 && packet.action < PlayerStat.values().length) {
                PlayerLevelData.spend(player, PlayerStat.values()[packet.action]);
            }
        });
        context.setPacketHandled(true);
    }
}
