package com.sxxxp.ALPGMC.core.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static int packetId = 0;

    // ◀ 외부에서 호출할 중앙 네트워크 통신 채널 인스턴스
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("alpgmc", "main_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    // 고유 ID 생성을 위한 시퀀스 메서드
    private static int nextId() {
        return packetId++;
    }

    // ◀ 패킷들을 채널에 등록하는 초기화 메서드
    public static void register() {
        INSTANCE.messageBuilder(XpToastPacket.class, nextId())
                .encoder(XpToastPacket::encode)
                .decoder(XpToastPacket::decode)
                .consumerMainThread(XpToastPacket::handle)
                .add();

        INSTANCE.messageBuilder(ClassSyncPacket.class, nextId())
                .encoder(ClassSyncPacket::encode)
                .decoder(ClassSyncPacket::decode)
                .consumerMainThread(ClassSyncPacket::handle)
                .add();

        INSTANCE.messageBuilder(SelectClassPacket.class, nextId())
                .encoder(SelectClassPacket::encode)
                .decoder(SelectClassPacket::decode)
                .consumerMainThread(SelectClassPacket::handle)
                .add();

        INSTANCE.messageBuilder(LevelSyncPacket.class, nextId())
                .encoder(LevelSyncPacket::encode)
                .decoder(LevelSyncPacket::decode)
                .consumerMainThread(LevelSyncPacket::handle)
                .add();

        INSTANCE.messageBuilder(StatActionPacket.class, nextId())
                .encoder(StatActionPacket::encode)
                .decoder(StatActionPacket::decode)
                .consumerMainThread(StatActionPacket::handle)
                .add();

        System.out.println("[ALPGMC] Successfully registered XpToastPacket to network channel.");
    }
}