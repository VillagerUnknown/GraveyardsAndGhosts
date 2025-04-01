package me.villagerunknown.graveyardsandghosts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GraveyardsandghostsPersistentPlayerData {
	
	// @type BlockPos
	public String lastOverworldPos = "";
	
	// @type Optional <GlobalPos>
	public String lastCorpsePos = "";

	// @type Map<String, Set<BlockPos>>
	public String respawnPositions = "";
	
	public static final Codec<GraveyardsandghostsPersistentPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("lastOverworldPos").forGetter(player -> player.lastOverworldPos),
			Codec.STRING.fieldOf("lastCorpsePos").forGetter(player -> player.lastCorpsePos),
			Codec.STRING.fieldOf("respawnPositions").forGetter(player -> player.respawnPositions)
	).apply(instance, GraveyardsandghostsPersistentPlayerData::new));
	
	GraveyardsandghostsPersistentPlayerData() {}
	
	GraveyardsandghostsPersistentPlayerData( String lastOverworldPos, String lastCorpsePos, String respawnPositions ) {
		this.lastOverworldPos = lastOverworldPos;
		this.lastCorpsePos = lastCorpsePos;
		this.respawnPositions = respawnPositions;
	}
	
}
