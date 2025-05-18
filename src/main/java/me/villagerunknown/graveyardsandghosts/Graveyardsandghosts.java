package me.villagerunknown.graveyardsandghosts;

import me.villagerunknown.graveyardsandghosts.feature.*;
import me.villagerunknown.platform.Platform;
import me.villagerunknown.platform.PlatformMod;
import me.villagerunknown.platform.manager.featureManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Graveyardsandghosts implements ModInitializer {
	
	public static PlatformMod<GraveyardsandghostsConfigData> MOD = Platform.register( "graveyardsandghosts", Graveyardsandghosts.class, GraveyardsandghostsConfigData.class );
	public static String MOD_ID = MOD.getModId();
	public static Logger LOGGER = MOD.getLogger();
	public static GraveyardsandghostsConfigData CONFIG = MOD.getConfig();
	
	@Override
	public void onInitialize() {
		// # Register mod with Platform
		Platform.init_mod( MOD );
		
		// # Activate Features
		featureManager.addFeature( "graveyard-blocks", graveyardBlocksFeature::execute );
		featureManager.addFeature( "player-ghost", playerGhostFeature::execute );
		featureManager.addFeature( "ghost-respawn", ghostRespawnFeature::execute );
		featureManager.addFeature( "groundskeeper-villager", groundskeeperVillagerFeature::execute );
		featureManager.addFeature( "graveyard-maps-trade", graveyardMapsTradeFeature::execute );
		
		// # Load Features
		featureManager.loadFeatures();
	}
	
}
