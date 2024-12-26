package me.villagerunknown.graveyardsandghosts.network;

import me.villagerunknown.graveyardsandghosts.feature.ghostRespawnFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record DeclineResurrectionPayload() implements CustomPayload {
	
	public static final Id<DeclineResurrectionPayload> ID = new Id<>(ghostRespawnFeature.DECLINE_RESURRECTION_PACKET_ID);
	public static final PacketCodec<RegistryByteBuf, DeclineResurrectionPayload> CODEC = PacketCodec.of( DeclineResurrectionPayload::encode, DeclineResurrectionPayload::decode );
	
	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
	
	public static void encode(DeclineResurrectionPayload payload, PacketByteBuf buf) {
	
	}
	
	public static DeclineResurrectionPayload decode(PacketByteBuf buf) {
		return new DeclineResurrectionPayload();
	}
	
}