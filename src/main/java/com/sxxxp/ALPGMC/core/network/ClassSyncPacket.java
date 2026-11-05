package com.sxxxp.ALPGMC.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

// 서버 -> 클라이언트: 현재 직업을 알려줍니다. 직업이 없으면 빈 문자열을 보냅니다.
public class ClassSyncPacket {
    private final String classId;

    public ClassSyncPacket(String classId) {
        this.classId = classId;
    }

    public static ClassSyncPacket decode(FriendlyByteBuf buf) {
        return new ClassSyncPacket(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.classId);
    }

    public static void handle(ClassSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            com.sxxxp.ALPGMC.client.ClientClassData.set(packet.classId);
        });
        context.setPacketHandled(true);
    }
}
