package me.villagerunknown.graveyardsandghosts;

import me.villagerunknown.graveyardsandghosts.feature.graveyardBlocksFeature;
import me.villagerunknown.graveyardsandghosts.feature.ghostRespawnFeature;
import me.villagerunknown.graveyardsandghosts.feature.groundskeeperVillagerFeature;
import me.villagerunknown.graveyardsandghosts.feature.playerGhostFeature;
import me.villagerunknown.platform.Platform;
import me.villagerunknown.platform.PlatformMod;
import me.villagerunknown.platform.manager.featureManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Graveyardsandghosts implements ModInitializer {
	
	public static PlatformMod<GraveyardsandghostsConfigData> MOD = null;
	public static String MOD_ID = null;
	public static Logger LOGGER = null;
	public static GraveyardsandghostsConfigData CONFIG = null;
	
	@Override
	public void onInitialize() {
		// # Register Mod w/ Platform
		MOD = Platform.register( "graveyardsandghosts", Graveyardsandghosts.class, GraveyardsandghostsConfigData.class );
		
		MOD_ID = MOD.getModId();
		LOGGER = MOD.getLogger();
		CONFIG = MOD.getConfig();
		
		// # Initialize Mod
		init();
	}
	
	private static void init() {
		Platform.init_mod( MOD );
		
		// # Activate Features
		featureManager.addFeature( "graveyardBlocks", graveyardBlocksFeature::execute );
		featureManager.addFeature( "playerGhost", playerGhostFeature::execute );
		featureManager.addFeature( "ghostRespawn", ghostRespawnFeature::execute );
		featureManager.addFeature( "groundskeeperVillager", groundskeeperVillagerFeature::execute );
	}
	
}
