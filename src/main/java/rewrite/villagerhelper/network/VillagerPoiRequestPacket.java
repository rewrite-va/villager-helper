package rewrite.villagerhelper.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record VillagerPoiRequestPacket(int entityId) implements CustomPacketPayload {

    public static final Type<VillagerPoiRequestPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath("villagerhelper", "poi_request"));

    public static final StreamCodec<FriendlyByteBuf, VillagerPoiRequestPacket> CODEC = StreamCodec.of(
        (StreamEncoder<FriendlyByteBuf, VillagerPoiRequestPacket>) (buf, pkt) -> buf.writeInt(pkt.entityId),
        (StreamDecoder<FriendlyByteBuf, VillagerPoiRequestPacket>) buf -> new VillagerPoiRequestPacket(buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
