package com.sxxxp.ALPGMC.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class XpToastPacket {
    private final int xpAmount;
    private final String mobName;

    public XpToastPacket(int xpAmount, String mobName) {
        this.xpAmount = xpAmount;
        this.mobName = mobName;
    }

    // 디코더 (바이트 데이터를 객체로 환원)
    public static XpToastPacket decode(FriendlyByteBuf buf) {
        return new XpToastPacket(buf.readInt(), buf.readUtf());
    }

    // 인코더 (객체를 바이트 데이터로 변환)
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.xpAmount);
        buf.writeUtf(this.mobName);
    }

    // 클라이언트가 이 패킷을 받았을 때 실행할 로직
    public static void handle(XpToastPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            com.sxxxp.ALPGMC.client.XpUiManager.showNotification(packet.xpAmount, packet.mobName);
        });
        context.setPacketHandled(true);
    }
}