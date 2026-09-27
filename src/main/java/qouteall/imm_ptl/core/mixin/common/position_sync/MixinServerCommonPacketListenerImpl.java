package qouteall.imm_ptl.core.mixin.common.position_sync;

import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.ducks.IEPlayerPositionLookS2CPacket;

/**
 * Fixes the post-respawn disconnect caused by Immersive Portals 6.0.7+.
 *
 * <p>IP adds a "dimension" field to {@link ClientboundPlayerPositionPacket} and writes it
 * unconditionally in {@link MixinPlayerPositionLookS2CPacket}. That field is only ever
 * populated by IP's own {@code @Overwrite} of {@code ServerGamePacketListenerImpl#teleport},
 * so position packets produced by any other code path — most notably the respawn flow —
 * carry a {@code null} dimension, causing
 * {@code FriendlyByteBuf.writeResourceKey} to throw NPE and kick the player with
 * "Internal Exception: io.netty.handler.codec.EncoderException".
 *
 * <p>This mixin fills in the missing dimension with the player's current dimension right
 * before the packet leaves the server. Packets that already carry a dimension are untouched,
 * so normal teleports and portals behave exactly as before.
 */
@Mixin(ServerCommonPacketListenerImpl.class)
public class MixinServerCommonPacketListenerImpl {

    @Inject(
            method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V",
            at = @At("HEAD")
    )
    private void iptlfix$fillMissingPositionDimension(
            Packet<?> packet, PacketSendListener listener, CallbackInfo ci
    ) {
        if (!(packet instanceof ClientboundPlayerPositionPacket)) {
            return;
        }
        Object self = this;
        if (!(self instanceof ServerGamePacketListenerImpl gameListener)) {
            return;
        }
        if (gameListener.player == null) {
            return;
        }
        IEPlayerPositionLookS2CPacket ipPacket = (IEPlayerPositionLookS2CPacket) packet;
        if (ipPacket.ip_getPlayerDimension() != null) {
            return;
        }
        ipPacket.ip_setPlayerDimension(gameListener.player.level().dimension());
    }
}
