package com.sxxxp.ALPGMC.core.network;

import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClassData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

// 클라이언트 -> 서버: 선택 화면에서 고른 직업을 보냅니다.
public class SelectClassPacket {
    private final String classId;

    public SelectClassPacket(String classId) {
        this.classId = classId;
    }

    public static SelectClassPacket decode(FriendlyByteBuf buf) {
        return new SelectClassPacket(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.classId);
    }

    public static void handle(SelectClassPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            PlayerClass playerClass = PlayerClass.fromId(packet.classId);
            // 직업 변경은 불가능하므로, 이미 직업이 있으면 패킷을 무시합니다.
            if (player != null && playerClass != null && PlayerClassData.get(player) == null) {
                PlayerClassData.set(player, playerClass);
            }
        });
        context.setPacketHandled(true);
    }
}
