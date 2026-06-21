package rewrite.villagerhelper.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record VillagerPoiResponsePacket(
    int entityId,
    Optional<BlockPos> bedPos,
    Optional<BlockPos> jobPos
) implements CustomPacketPayload {

    public static final Type<VillagerPoiResponsePacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath("villagerhelper", "poi_response"));

    public static final StreamCodec<FriendlyByteBuf, VillagerPoiResponsePacket> CODEC = StreamCodec.of(
        (StreamEncoder<FriendlyByteBuf, VillagerPoiResponsePacket>) (buf, pkt) -> {
            buf.writeInt(pkt.entityId);
            buf.writeBoolean(pkt.bedPos.isPresent());
            pkt.bedPos.ifPresent(buf::writeBlockPos);
            buf.writeBoolean(pkt.jobPos.isPresent());
            pkt.jobPos.ifPresent(buf::writeBlockPos);
        },
        (StreamDecoder<FriendlyByteBuf, VillagerPoiResponsePacket>) buf -> {
            int id = buf.readInt();
            Optional<BlockPos> bed = buf.readBoolean() ? Optional.of(buf.readBlockPos()) : Optional.empty();
            Optional<BlockPos> job = buf.readBoolean() ? Optional.of(buf.readBlockPos()) : Optional.empty();
            return new VillagerPoiResponsePacket(id, bed, job);
        }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
