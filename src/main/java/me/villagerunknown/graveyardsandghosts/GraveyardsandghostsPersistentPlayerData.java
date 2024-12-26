package me.villagerunknown.graveyardsandghosts;

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
	
}
