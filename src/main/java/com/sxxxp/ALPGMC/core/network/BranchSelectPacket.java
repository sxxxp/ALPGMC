package com.sxxxp.ALPGMC.core.network;

import com.sxxxp.ALPGMC.common.playerclass.ClassProgression;
import com.sxxxp.ALPGMC.common.playerclass.PlayerBranch;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

// 클라이언트 -> 서버: 고르거나 바꿀 전투 갈래를 보냅니다.
public class BranchSelectPacket {
    private final String branchId;

    public BranchSelectPacket(String branchId) {
        this.branchId = branchId;
    }

    public static BranchSelectPacket decode(FriendlyByteBuf buf) {
        return new BranchSelectPacket(buf.readUtf(32));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.branchId, 32);
    }

    public static void handle(BranchSelectPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            PlayerBranch branch = PlayerBranch.fromId(packet.branchId);
            if (player != null && branch != null) {
                ClassProgression.choose(player, branch);
            }
        });
        context.setPacketHandled(true);
    }
}
