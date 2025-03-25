package me.villagerunknown.graveyardsandghosts.feature;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.block.MapColor;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.gen.structure.Structure;

import static me.villagerunknown.graveyardsandghosts.Graveyardsandghosts.MOD_ID;

public class graveyardMapsTradeFeature {
	
	public static final TagKey<Structure> ON_GRAVEYARDS_MAPS = TagKey.of(RegistryKeys.STRUCTURE, Identifier.of(MOD_ID, "graveyard") );
	public static final RegistryEntry<MapDecorationType> GRAVEYARD_MAP_DECORATION = registerMapDecorationType( Identifier.of(MOD_ID,"graveyard"), Identifier.of(MOD_ID,"graveyard"), true, MapColor.GRAY.color, false, true);
	
	public static void execute() {
		registerCartographerTrades();
	}
	
	private static void registerCartographerTrades() {
		TradeOfferHelper.registerVillagerOffers( VillagerProfession.CARTOGRAPHER, 1, f -> {
			f.add( new TradeOffers.SellMapFactory(8, ON_GRAVEYARDS_MAPS, "filled_map.graveyards", GRAVEYARD_MAP_DECORATION, 12, 4) );
		} );
	}
	
	private static RegistryEntry<MapDecorationType> registerMapDecorationType(Identifier id, Identifier assetId, boolean showOnItemFrame, int mapColor, boolean trackCount, boolean explorationMapElement) {
		RegistryKey<MapDecorationType> registryKey = RegistryKey.of(RegistryKeys.MAP_DECORATION_TYPE, id);
		MapDecorationType mapDecorationType = new MapDecorationType(assetId, showOnItemFrame, mapColor, explorationMapElement, trackCount);
		return Registry.registerReference(Registries.MAP_DECORATION_TYPE, registryKey, mapDecorationType);
	}
	
}
