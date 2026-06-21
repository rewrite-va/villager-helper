package rewrite.villagerhelper.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.Optional;

// found=false means no villager claimed this block
public record BlockPoiResponsePacket(
    boolean found,
    int entityId,
    Optional<BlockPos> bedPos,
    Optional<BlockPos> jobPos
) implements CustomPacketPayload {

    public static final Type<BlockPoiResponsePacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath("villagerhelper", "block_poi_response"));

    public static final StreamCodec<FriendlyByteBuf, BlockPoiResponsePacket> CODEC = StreamCodec.of(
        (StreamEncoder<FriendlyByteBuf, BlockPoiResponsePacket>) (buf, pkt) -> {
            buf.writeBoolean(pkt.found);
            if (pkt.found) {
                buf.writeInt(pkt.entityId);
                buf.writeBoolean(pkt.bedPos.isPresent());
                pkt.bedPos.ifPresent(buf::writeBlockPos);
                buf.writeBoolean(pkt.jobPos.isPresent());
                pkt.jobPos.ifPresent(buf::writeBlockPos);
            }
        },
        (StreamDecoder<FriendlyByteBuf, BlockPoiResponsePacket>) buf -> {
            boolean found = buf.readBoolean();
            if (!found) return new BlockPoiResponsePacket(false, -1, Optional.empty(), Optional.empty());
            int id = buf.readInt();
            Optional<BlockPos> bed = buf.readBoolean() ? Optional.of(buf.readBlockPos()) : Optional.empty();
            Optional<BlockPos> job = buf.readBoolean() ? Optional.of(buf.readBlockPos()) : Optional.empty();
            return new BlockPoiResponsePacket(true, id, bed, job);
        }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
