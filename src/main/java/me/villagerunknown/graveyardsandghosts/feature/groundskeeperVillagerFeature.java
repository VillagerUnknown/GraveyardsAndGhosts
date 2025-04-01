package me.villagerunknown.graveyardsandghosts.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import me.villagerunknown.platform.util.VillagerUtil;
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
	
	public static final String GROUNDSKEEPER_STRING = "groundskeeper";
	public static final Identifier GROUNDSKEEPER_IDENTIFIER = Identifier.of( MOD_ID, GROUNDSKEEPER_STRING );
	
	public static VillagerUtil.CustomVillager GROUNDSKEEPER = new VillagerUtil.CustomVillager( GROUNDSKEEPER_IDENTIFIER, getResurrectionStatueBlockStates(), GROUNDSKEEPER_STRING, SoundEvents.BLOCK_ROOTED_DIRT_BREAK );

	public static void execute() {
		registerVillagerTrades();
	}
	
	public static ImmutableList<BlockState> getResurrectionStatueBlockStates() {
		Set<BlockState> blockStates = new HashSet<>();
		
		graveyardBlocksFeature.RESURRECTION_STATUES.forEach( ( id, block ) -> {
			blockStates.addAll( block.getStateManager().getStates() );
		} );
		
		return ImmutableList.copyOf( blockStates );
	}
	
	private static void registerVillagerTrades() {
		RegistryKey<VillagerProfession> professionRegistryKey = RegistryKey.of(Registries.VILLAGER_PROFESSION.getKey(), GROUNDSKEEPER.IDENTIFIER );
		
		// # Level 1 - Tombstones
		TradeOfferHelper.registerVillagerOffers( professionRegistryKey, 1, f -> {
			graveyardBlocksFeature.TOMBSTONES.forEach( ( id, block ) -> {
				f.add( (entity, random) -> VillagerUtil.sellTradeOffer( 1, new TradedItem( Items.EMERALD, 6 ), new ItemStack( block, 8 ) ) );
			} );
		} );
		
		// # Level 2 - Pedestals
		TradeOfferHelper.registerVillagerOffers( professionRegistryKey, 2, f -> {
			graveyardBlocksFeature.PEDESTALS.forEach( ( id, block ) -> {
				f.add( (entity, random) -> VillagerUtil.sellTradeOffer( 2, new TradedItem( Items.EMERALD, 8 ), new ItemStack( block, 1 ) ) );
			} );
		} );
		
		// # Level 3 - Statues
		TradeOfferHelper.registerVillagerOffers( professionRegistryKey, 3, f -> {
			graveyardBlocksFeature.STATUES.forEach( ( id, block ) -> {
				f.add( (entity, random) -> VillagerUtil.sellTradeOffer( 3, new TradedItem( Items.EMERALD, 12 ), new ItemStack( block, 1 ) ) );
			} );
		} );
		
		// # Level 4 - Coffins
		TradeOfferHelper.registerVillagerOffers( professionRegistryKey, 4, f -> {
			graveyardBlocksFeature.COFFINS.forEach( ( id, block ) -> {
				f.add( (entity, random) -> VillagerUtil.sellTradeOffer( 4, new TradedItem( Items.EMERALD, 16 ), new ItemStack( block, 1 ) ) );
				f.add( (entity, random) -> VillagerUtil.sellTradeOffer( 4, new TradedItem( Items.EMERALD, 8 ), new TradedItem( Items.CHEST, 1 ), new ItemStack( block, 1 ) ) );
			} );
		} );
		
		// # Level 5 - Resurrection Statues
		TradeOfferHelper.registerVillagerOffers( professionRegistryKey, 5, f -> {
			graveyardBlocksFeature.RESURRECTION_STATUES.forEach( ( id, block ) -> {
				f.add( (entity, random) -> VillagerUtil.sellTradeOffer( 5, new TradedItem( Items.EMERALD, 64 ), new ItemStack( block, 1 ) ) );
				f.add( (entity, random) -> VillagerUtil.sellTradeOffer( 5, new TradedItem( Items.EMERALD, 32 ), new TradedItem( Items.TOTEM_OF_UNDYING, 1 ), new ItemStack( block, 4 ) ) );
			} );
		} );
	}
	
}
