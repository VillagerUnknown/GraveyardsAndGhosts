package me.villagerunknown.graveyardsandghosts.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PlayerData {
	
	public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("lastOverworldPos").forGetter(player -> player.lastOverworldPos),
			Codec.STRING.fieldOf("lastCorpsePos").forGetter(player -> player.lastCorpsePos),
			Codec.STRING.fieldOf("respawnPositions").forGetter(player -> player.respawnPositions)
	).apply(instance, PlayerData::new));
	
	// @type BlockPos
	public String lastOverworldPos = "";
	
	// @type Optional <GlobalPos>
	public String lastCorpsePos = "";

	// @type Map<String, Set<BlockPos>>
	public String respawnPositions = "";
	
	public PlayerData() {}
	
	PlayerData(String lastOverworldPos, String lastCorpsePos, String respawnPositions ) {
		this.lastOverworldPos = lastOverworldPos;
		this.lastCorpsePos = lastCorpsePos;
		this.respawnPositions = respawnPositions;
	}
	
}
