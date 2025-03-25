package me.villagerunknown.graveyardsandghosts.feature;

import com.google.common.collect.ImmutableSet;
import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static me.villagerunknown.graveyardsandghosts.Graveyardsandghosts.MOD_ID;

public class groundskeeperVillagerFeature {
	
	public static final int DEFAULT_MAX_USES = 12;
	public static final int COMMON_MAX_USES = 16;
	public static final int RARE_MAX_USES = 3;
	public static final int NOVICE_SELL_XP = 1;
	public static final int NOVICE_BUY_XP = 2;
	public static final int APPRENTICE_SELL_XP = 5;
	public static final int APPRENTICE_BUY_XP = 10;
	public static final int JOURNEYMAN_SELL_XP = 10;
	public static final int JOURNEYMAN_BUY_XP = 20;
	public static final int EXPERT_SELL_XP = 15;
	public static final int EXPERT_BUY_XP = 30;
	public static final int MASTER_TRADE_XP = 30;
	public static final float LOW_PRICE_MULTIPLIER = 0.05F;
	public static final float HIGH_PRICE_MULTIPLIER = 0.2F;
	
	public static final String GROUNDSKEEPER_STRING = "groundskeeper";
	public static final Identifier GROUNDSKEEPER_IDENTIFIER = Identifier.of( MOD_ID, GROUNDSKEEPER_STRING );
	
	public static PointOfInterestType GROUNDSKEEPER_POI_TYPE = null;
	public static RegistryKey<PointOfInterestType> GROUNDSKEEPER_POI_TYPE_REGISTRY_KEY = null;
	public static RegistryEntry<PointOfInterestType> GROUNDSKEEPER_POI_TYPE_REGISTRY = null;
	public static VillagerProfession GROUNDSKEEPER_PROFESSION = null;
	public static RegistryEntry<VillagerProfession> GROUNDSKEEPER_PROFESSION_REGISTRY = null;
	
	public static void execute() {
		registerPointOfInterest();
		registerVillagerProfession();
		registerVillagerTrades();
	}
	
	private static void registerPointOfInterest() {
		Set<BlockState> blockStates = new HashSet<>();
		
		graveyardBlocksFeature.RESURRECTION_STATUES.forEach( ( id, block ) -> {
			blockStates.addAll( block.getStateManager().getStates() );
		} );
		
		GROUNDSKEEPER_POI_TYPE = PointOfInterestHelper.register( GROUNDSKEEPER_IDENTIFIER, 1, 1, blockStates );
		
		Registry.register( Registries.POINT_OF_INTEREST_TYPE, GROUNDSKEEPER_IDENTIFIER, GROUNDSKEEPER_POI_TYPE );
		
		GROUNDSKEEPER_POI_TYPE_REGISTRY = Registries.POINT_OF_INTEREST_TYPE.getEntry( GROUNDSKEEPER_POI_TYPE );
		GROUNDSKEEPER_POI_TYPE_REGISTRY_KEY =  RegistryKey.of( RegistryKeys.POINT_OF_INTEREST_TYPE, GROUNDSKEEPER_IDENTIFIER );
	}
	
	private static void registerVillagerProfession() {
		Predicate<RegistryEntry<PointOfInterestType>> predicate = (entry) -> entry.matchesKey( GROUNDSKEEPER_POI_TYPE_REGISTRY_KEY );
		
		GROUNDSKEEPER_PROFESSION = new VillagerProfession( GROUNDSKEEPER_STRING, predicate, predicate, ImmutableSet.of(), ImmutableSet.of(), SoundEvents.BLOCK_ROOTED_DIRT_BREAK );
		
		Registry.register( Registries.VILLAGER_PROFESSION, GROUNDSKEEPER_IDENTIFIER, GROUNDSKEEPER_PROFESSION );
		
		GROUNDSKEEPER_PROFESSION_REGISTRY = Registries.VILLAGER_PROFESSION.getEntry( GROUNDSKEEPER_PROFESSION );
	}
	
	private static void registerVillagerTrades() {
		// # Level 1 - Tombstones
		TradeOfferHelper.registerVillagerOffers( GROUNDSKEEPER_PROFESSION, 1, f -> {
			graveyardBlocksFeature.TOMBSTONES.forEach( ( id, block ) -> {
				f.add( (entity, random) -> new TradeOffer(
						new TradedItem( Items.EMERALD, 3 ),
						new ItemStack( block, 8 ),
						DEFAULT_MAX_USES,
						NOVICE_BUY_XP,
						LOW_PRICE_MULTIPLIER
				));
			} );
		} );
		
		// # Level 2 - Pedestals
		TradeOfferHelper.registerVillagerOffers( GROUNDSKEEPER_PROFESSION, 2, f -> {
			graveyardBlocksFeature.PEDESTALS.forEach( ( id, block ) -> {
				f.add((entity, random) -> new TradeOffer(
						new TradedItem(Items.EMERALD, 4),
						new ItemStack(block, 1),
						COMMON_MAX_USES,
						APPRENTICE_BUY_XP,
						LOW_PRICE_MULTIPLIER
				));
			} );
		} );
		
		// # Level 3 - Statues
		TradeOfferHelper.registerVillagerOffers( GROUNDSKEEPER_PROFESSION, 3, f -> {
			graveyardBlocksFeature.STATUES.forEach( ( id, block ) -> {
				f.add((entity, random) -> new TradeOffer(
						new TradedItem(Items.EMERALD, 6),
						new ItemStack(block, 1),
						COMMON_MAX_USES,
						JOURNEYMAN_BUY_XP,
						LOW_PRICE_MULTIPLIER
				));
			} );
		} );
		
		// # Level 4 - Coffins
		TradeOfferHelper.registerVillagerOffers( GROUNDSKEEPER_PROFESSION, 4, f -> {
			graveyardBlocksFeature.COFFINS.forEach( ( id, block ) -> {
				f.add((entity, random) -> new TradeOffer(
						new TradedItem(Items.EMERALD, 16),
						new ItemStack(block, 1),
						RARE_MAX_USES,
						EXPERT_BUY_XP,
						LOW_PRICE_MULTIPLIER
				));
				f.add((entity, random) -> new TradeOffer(
						new TradedItem(Items.EMERALD, 8),
						Optional.of( new TradedItem(Items.CHEST, 1) ),
						new ItemStack(block, 1),
						RARE_MAX_USES,
						EXPERT_BUY_XP,
						LOW_PRICE_MULTIPLIER
				));
			} );
		} );
		
		// # Level 5 - Resurrection Statues
		TradeOfferHelper.registerVillagerOffers( GROUNDSKEEPER_PROFESSION, 5, f -> {
			graveyardBlocksFeature.RESURRECTION_STATUES.forEach( ( id, block ) -> {
				f.add( (entity, random) -> new TradeOffer(
						new TradedItem( Items.EMERALD, 32 ),
						new ItemStack( block, 1 ),
						RARE_MAX_USES,
						MASTER_TRADE_XP,
						HIGH_PRICE_MULTIPLIER
				));
				f.add( (entity, random) -> new TradeOffer(
						new TradedItem( Items.EMERALD, 64 ),
						Optional.of( new TradedItem(Items.TOTEM_OF_UNDYING, 1) ),
						new ItemStack( block, 4 ),
						RARE_MAX_USES,
						MASTER_TRADE_XP,
						HIGH_PRICE_MULTIPLIER
				));
			} );
		} );
	}
	
}
