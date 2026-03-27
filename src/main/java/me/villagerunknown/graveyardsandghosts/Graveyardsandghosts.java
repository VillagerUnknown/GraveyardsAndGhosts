package me.villagerunknown.graveyardsandghosts;

import me.villagerunknown.graveyardsandghosts.feature.*;
import me.villagerunknown.platform.Platform;
import me.villagerunknown.platform.PlatformMod;
import me.villagerunknown.platform.manager.featureManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class Graveyardsandghosts implements ModInitializer {
	
	public static PlatformMod<GraveyardsandghostsConfigData> MOD = Platform.register( "graveyardsandghosts", Graveyardsandghosts.class, GraveyardsandghostsConfigData.class );
	public static String MOD_ID = MOD.getModId();
	public static Logger LOGGER = MOD.getLogger();
	public static GraveyardsandghostsConfigData CONFIG = MOD.getConfig();
	
	public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(Graveyardsandghosts.MOD_ID, "item_group"));
	public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
			.icon(() -> new ItemStack(Items.SKELETON_SKULL))
			.displayName(Text.translatable("itemGroup." + MOD_ID))
			.build();
	
	@Override
	public void onInitialize() {
		// # Register mod with Platform
		Platform.init_mod( MOD );
		
		// Register Item Group
		Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);
		
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
