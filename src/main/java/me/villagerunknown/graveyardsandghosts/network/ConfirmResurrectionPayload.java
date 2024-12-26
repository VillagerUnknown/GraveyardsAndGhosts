package me.villagerunknown.graveyardsandghosts.network;

import me.villagerunknown.graveyardsandghosts.feature.ghostRespawnFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ConfirmResurrectionPayload() implements CustomPayload {
	
	public static final Id<ConfirmResurrectionPayload> ID = new Id<>(ghostRespawnFeature.CONFIRM_RESURRECTION_PACKET_ID);
	public static final PacketCodec<RegistryByteBuf, ConfirmResurrectionPayload> CODEC = PacketCodec.of( ConfirmResurrectionPayload::encode, ConfirmResurrectionPayload::decode );
	
	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
	
	public static void encode(ConfirmResurrectionPayload payload, PacketByteBuf buf) {
	
	}
	
	public static ConfirmResurrectionPayload decode(PacketByteBuf buf) {
		return new ConfirmResurrectionPayload();
	}
	
}