package rewrite.villagerhelper.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BlockPoiRequestPacket(BlockPos blockPos) implements CustomPacketPayload {

    public static final Type<BlockPoiRequestPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath("villagerhelper", "block_poi_request"));

    public static final StreamCodec<FriendlyByteBuf, BlockPoiRequestPacket> CODEC = StreamCodec.of(
        (StreamEncoder<FriendlyByteBuf, BlockPoiRequestPacket>) (buf, pkt) -> buf.writeBlockPos(pkt.blockPos),
        (StreamDecoder<FriendlyByteBuf, BlockPoiRequestPacket>) buf -> new BlockPoiRequestPacket(buf.readBlockPos())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
