package me.villagerunknown.graveyardsandghosts.feature;

import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import me.villagerunknown.graveyardsandghosts.block.coffin.CoffinBlock;
import me.villagerunknown.graveyardsandghosts.block.entity.CoffinBlockEntity;
import me.villagerunknown.graveyardsandghosts.block.entity.GraveSoilBlockEntity;
import me.villagerunknown.graveyardsandghosts.block.entity.ResurrectionBlockEntity;
import me.villagerunknown.graveyardsandghosts.block.grave.GraveSoilBlock;
import me.villagerunknown.graveyardsandghosts.block.resurrection.TwoTallResurrectionStatueBlock;
import me.villagerunknown.graveyardsandghosts.block.statue.StatueBlock;
import me.villagerunknown.graveyardsandghosts.block.statue.TwoTallStatueBlock;
import me.villagerunknown.graveyardsandghosts.block.tombstone.BrokenTombstoneBlock;
import me.villagerunknown.graveyardsandghosts.block.tombstone.TombstoneBlock;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

import static me.villagerunknown.graveyardsandghosts.Graveyardsandghosts.MOD_ID;

public class graveyardBlocksFeature {
	
	private static List<String> blockTypes = new ArrayList<>(List.of(
			"oak",
			"spruce",
			"birch",
			"jungle",
			"acacia",
			"dark_oak",
			"mangrove",
			"cherry",
//			"pale_oak",
			"crimson",
			"warped",

			"oak_plank",
			"spruce_plank",
			"birch_plank",
			"jungle_plank",
			"acacia_plank",
			"dark_oak_plank",
			"mangrove_plank",
			"cherry_plank",
//			"pale_oak_plank",
			"crimson_plank",
			"warped_plank",
			
			"emerald",
			"gold",
			"redstone",
			"lapis",
			"diamond",
			"netherite",
			"amethyst",
			"dripstone",

			"mossy_cobblestone",
			"moss",
			"clay",
			"mud",
			"packed_mud",

// @todo
//			"terracotta",
//			"white_terracotta",
//			"gray_terracotta",
//			"light_gray_terracotta",
//			"black_terracotta",
//			"brown_terracotta",
//			"red_terracotta",
//			"orange_terracotta",
//			"yellow_terracotta",
//			"lime_terracotta",
//			"green_terracotta",
//			"cyan_terracotta",
//			"light_blue_terracotta",
//			"blue_terracotta",
//			"purple_terracotta",
//			"magenta_terracotta",
//			"pink_terracotta",

// @todo
//			"white_concrete",
//			"gray_concrete",
//			"light_gray_concrete",
//			"black_concrete",
//			"brown_concrete",
//			"red_concrete",
//			"orange_concrete",
//			"yellow_concrete",
//			"lime_concrete",
//			"green_concrete",
//			"cyan_concrete",
//			"light_blue_concrete",
//			"blue_concrete",
//			"purple_concrete",
//			"magenta_concrete",
//			"pink_concrete",

			"blue_ice",
			"snow",
			"sculk",

			"cobblestone",
			"stone",
			"andesite",
			"granite",
			"diorite",
			"polished_andesite",
			"polished_granite",
			"polished_diorite",
			"smooth_sandstone",
			"smooth_red_sandstone",
			"tuff",
			"polished_tuff",
			"calcite",
			"prismarine",
			"dark_prismarine",
			"polished_deepslate",

			"obsidian",
			"crying_obsidian",

			"quartz",
			"netherrack",
			"smooth_basalt",
			"blackstone",
			"polished_blackstone"
	));
	
	public static Map<String, BlockEntityType> BLOCK_ENTITY_TYPES = new HashMap<>();
	public static Map<String, Block> BLOCKS = new HashMap<>();
	
	public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(Graveyardsandghosts.MOD_ID, "item_group"));
	public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
			.icon(() -> new ItemStack(Items.SKELETON_SKULL))
			.displayName(Text.translatable("itemGroup." + MOD_ID))
			.build();
	
	public static void execute() {
		registerItemGroup();
		registerBlocks();
	}
	
	private static void registerBlocks() {
		Collections.sort( blockTypes );
		
		register_grave_soil();
		
		for( String blockType : blockTypes ) {
			
			register_broken_tombstone( blockType );
			register_tombstone( blockType );
			register_coffin( blockType );
			register_pedestal( blockType );
			register_statue( blockType );
			register_resurrection_statue( blockType );
			register_cat_statue( blockType );
			register_wolf_statue( blockType );
			register_player_statue( blockType );
			register_villager_statue( blockType );
			register_pillager_statue( blockType );
			register_piglin_statue( blockType );
			register_dragon_statue( blockType );
			
		} // for
		
		registerBlockEntityTypes();
	}
	
	private static void register_grave_soil() {
		BLOCKS.put( "grave_soil", registerBlock( new GraveSoilBlock(), "grave_soil", true) );
	}
	
	private static void register_tombstone( String blockType ) {
		BLOCKS.put( blockType + "_tombstone", registerBlock( new TombstoneBlock(), blockType + "_tombstone", true) );
		BLOCKS.put( blockType + "_rounded_tombstone", registerBlock( new TombstoneBlock(), blockType + "_rounded_tombstone", true) );
//		BLOCKS.put( blockType + "_tombstone", registerBlock( new TombstoneSignBlock(), blockType + "_tombstone", true) );
	}
	
	private static void register_broken_tombstone( String blockType ) {
		BLOCKS.put( "broken_" + blockType + "_tombstone", registerBlock( new BrokenTombstoneBlock(), "broken_" + blockType + "_tombstone", true) );
	}
	
	private static void register_coffin( String blockType ) {
		BLOCKS.put( blockType + "_coffin", registerBlock( new CoffinBlock(), blockType + "_coffin", true) );
	}
	
	private static void register_pedestal( String blockType ) {
		BLOCKS.put( blockType + "_pedestal", registerBlock( new StatueBlock(), blockType + "_pedestal", true) );
		BLOCKS.put( blockType + "_thin_pedestal", registerBlock( new StatueBlock(), blockType + "_thin_pedestal", true) );
	}
	
	private static void register_statue( String blockType ) {
		BLOCKS.put( blockType + "_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_statue", true) );
		BLOCKS.put( blockType + "_cross_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_cross_statue", true) );
		BLOCKS.put( blockType + "_celtic_cross_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_celtic_cross_statue", true) );
	}
	
	private static void register_resurrection_statue( String blockType ) {
		BLOCKS.put( blockType + "_resurrection_statue", registerBlock( new TwoTallResurrectionStatueBlock(), blockType + "_resurrection_statue", true) );
	}
	
	private static void register_player_statue( String blockType ) {
		BLOCKS.put( blockType + "_player_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_player_statue", true) );
		BLOCKS.put( blockType + "_player_exploring_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_player_exploring_statue", true) );
		BLOCKS.put( blockType + "_player_building_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_player_building_statue", true) );
		BLOCKS.put( blockType + "_player_fighting_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_player_fighting_statue", true) );
		BLOCKS.put( blockType + "_player_reaper_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_player_reaper_statue", true) );
		BLOCKS.put( blockType + "_player_angel_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_player_angel_statue", true) );
	}
	
	private static void register_cat_statue( String blockType ) {
		BLOCKS.put( blockType + "_cat_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_cat_statue", true) );
		BLOCKS.put( blockType + "_cat_sitting_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_cat_sitting_statue", true) );
	}
	
	private static void register_wolf_statue( String blockType ) {
		BLOCKS.put( blockType + "_wolf_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_wolf_statue", true) );
		BLOCKS.put( blockType + "_wolf_sitting_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_wolf_sitting_statue", true) );
	}
	
	private static void register_villager_statue( String blockType ) {
		BLOCKS.put( blockType + "_villager_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_villager_statue", true) );
		BLOCKS.put( blockType + "_villager_poppy_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_villager_poppy_statue", true) );
	}
	
	private static void register_pillager_statue( String blockType ) {
		BLOCKS.put( blockType + "_pillager_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_pillager_statue", true) );
		BLOCKS.put( blockType + "_pillager_celebrating_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_pillager_celebrating_statue", true) );
	}
	
	private static void register_piglin_statue( String blockType ) {
		BLOCKS.put( blockType + "_piglin_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_piglin_statue", true) );
		BLOCKS.put( blockType + "_piglin_axe_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_piglin_axe_statue", true) );
	}
	
	private static void register_dragon_statue( String blockType ) {
//		BLOCKS.put( blockType + "_dragon_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_dragon_statue", true) );
		BLOCKS.put( blockType + "_dragon_egg_statue", registerBlock( new TwoTallStatueBlock(), blockType + "_dragon_egg_statue", true) );
	}
	
	private static void registerItemGroup() {
		Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);
	}
	
	public static Block registerBlock(Block block, String name, boolean shouldRegisterItem) {
		Identifier id = Identifier.of(MOD_ID, name);
		
		// Register item
		if (shouldRegisterItem) {
			BlockItem blockItem = new BlockItem(block, new Item.Settings());
			Registry.register(Registries.ITEM, id, blockItem);
			
			ItemGroupEvents.modifyEntriesEvent(CUSTOM_ITEM_GROUP_KEY).register(fabricItemGroupEntries -> fabricItemGroupEntries.add( blockItem ));
		}
		
		// Register block
		return Registry.register(Registries.BLOCK, id, block);
	}
	
	public static void registerBlockEntityTypes() {
		registerGraveSoilBlockEntities();
//		registerEngravedTombstoneBlockEntities();
		registerCoffinBlockEntities();
		registerResurrectionBlockEntities();
	}
	
	private static void registerGraveSoilBlockEntities() {
		// # Explicitly define each engraved tombstone block
		BlockEntityType.Builder<GraveSoilBlockEntity> builder = BlockEntityType.Builder.create(
				GraveSoilBlockEntity::new,
				BLOCKS.get("grave_soil")
		);
		
		// # Register the Block Entity Types
		BLOCK_ENTITY_TYPES.put( "grave_soil",
				Registry.register(
						Registries.BLOCK_ENTITY_TYPE,
						Identifier.of(MOD_ID, "grave_soil"),
						builder.build()
				)
		);
	}
	
	private static void registerEngravedTombstoneBlockEntities() {
		// # Explicitly define each engraved tombstone block
//		BlockEntityType.Builder<EngravedTombstoneBlockEntity> builder = BlockEntityType.Builder.create(
//				EngravedTombstoneBlockEntity::new,
//				BLOCKS.get("moss_engraved_tombstone_block"),
//				BLOCKS.get("snow_engraved_tombstone_block"),
//				BLOCKS.get("sculk_engraved_tombstone_block"),
//				BLOCKS.get("oak_engraved_tombstone_block"),
//				BLOCKS.get("spruce_engraved_tombstone_block"),
//				BLOCKS.get("birch_engraved_tombstone_block"),
//				BLOCKS.get("jungle_engraved_tombstone_block"),
//				BLOCKS.get("acacia_engraved_tombstone_block"),
//				BLOCKS.get("dark_oak_engraved_tombstone_block"),
//				BLOCKS.get("mangrove_engraved_tombstone_block"),
//				BLOCKS.get("cherry_engraved_tombstone_block"),
//				BLOCKS.get("crimson_stem_engraved_tombstone_block"),
//				BLOCKS.get("warped_stem_engraved_tombstone_block"),
//				BLOCKS.get("quartz_engraved_tombstone_block"),
//				BLOCKS.get("emerald_engraved_tombstone_block"),
//				BLOCKS.get("gold_engraved_tombstone_block"),
//				BLOCKS.get("obsidian_engraved_tombstone_block"),
//				BLOCKS.get("netherrack_engraved_tombstone_block")
//		);
		
		// # Register the Block Entity Types
//		BLOCK_ENTITY_TYPES.put( "coffin_block",
//				Registry.register(
//						Registries.BLOCK_ENTITY_TYPE,
//						Identifier.of(MOD_ID, "coffin_block"),
//						builder.build()
//				)
//		);
	}
	
	private static void registerCoffinBlockEntities() {
		// # Explicitly define each resurrection statue block
		BlockEntityType.Builder<CoffinBlockEntity> builder = BlockEntityType.Builder.create(
				CoffinBlockEntity::new,
				BLOCKS.get("moss_coffin"),
				BLOCKS.get("clay_coffin"),
				BLOCKS.get("mud_coffin"),
				BLOCKS.get("packed_mud_coffin"),
				BLOCKS.get("dripstone_coffin"),
				BLOCKS.get("mossy_cobblestone_coffin"),
				BLOCKS.get("snow_coffin"),
				BLOCKS.get("sculk_coffin"),
				
				BLOCKS.get("blue_ice_coffin"),
				
				BLOCKS.get("oak_coffin"),
				BLOCKS.get("spruce_coffin"),
				BLOCKS.get("birch_coffin"),
				BLOCKS.get("jungle_coffin"),
				BLOCKS.get("acacia_coffin"),
				BLOCKS.get("dark_oak_coffin"),
				BLOCKS.get("mangrove_coffin"),
				BLOCKS.get("cherry_coffin"),
				BLOCKS.get("crimson_coffin"),
				BLOCKS.get("warped_coffin"),
				
				BLOCKS.get("oak_plank_coffin"),
				BLOCKS.get("spruce_plank_coffin"),
				BLOCKS.get("birch_plank_coffin"),
				BLOCKS.get("jungle_plank_coffin"),
				BLOCKS.get("acacia_plank_coffin"),
				BLOCKS.get("dark_oak_plank_coffin"),
				BLOCKS.get("mangrove_plank_coffin"),
				BLOCKS.get("cherry_plank_coffin"),
//				BLOCKS.get("pale_oak_plank_coffin"),
				BLOCKS.get("crimson_plank_coffin"),
				BLOCKS.get("warped_plank_coffin"),
				
				BLOCKS.get("cobblestone_coffin"),
				BLOCKS.get("stone_coffin"),
				BLOCKS.get("andesite_coffin"),
				BLOCKS.get("granite_coffin"),
				BLOCKS.get("diorite_coffin"),
				BLOCKS.get("polished_andesite_coffin"),
				BLOCKS.get("polished_granite_coffin"),
				BLOCKS.get("polished_diorite_coffin"),
				BLOCKS.get("smooth_sandstone_coffin"),
				BLOCKS.get("smooth_red_sandstone_coffin"),
				BLOCKS.get("calcite_coffin"),
				BLOCKS.get("tuff_coffin"),
				BLOCKS.get("amethyst_coffin"),
				BLOCKS.get("emerald_coffin"),
				BLOCKS.get("redstone_coffin"),
				BLOCKS.get("gold_coffin"),
				BLOCKS.get("lapis_coffin"),
				BLOCKS.get("diamond_coffin"),
				BLOCKS.get("netherite_coffin"),
				BLOCKS.get("obsidian_coffin"),
				BLOCKS.get("crying_obsidian_coffin"),
				BLOCKS.get("netherrack_coffin"),
				BLOCKS.get("blackstone_coffin"),
				BLOCKS.get("polished_tuff_coffin"),
				BLOCKS.get("smooth_basalt_coffin"),
				BLOCKS.get("prismarine_coffin"),
				BLOCKS.get("dark_prismarine_coffin"),
				BLOCKS.get("polished_deepslate_coffin"),
				BLOCKS.get("polished_blackstone_coffin")
		);
		
		// # Register the Block Entity Types
		BLOCK_ENTITY_TYPES.put( "coffin_block",
				Registry.register(
						Registries.BLOCK_ENTITY_TYPE,
						Identifier.of(MOD_ID, "coffin_block"),
						builder.build()
				)
		);
	}
	
	private static void registerResurrectionBlockEntities() {
		// # Explicitly define each resurrection statue block
		BlockEntityType.Builder<ResurrectionBlockEntity> builder = BlockEntityType.Builder.create(
				ResurrectionBlockEntity::new,
				BLOCKS.get("moss_resurrection_statue"),
				BLOCKS.get("mud_resurrection_statue"),
				BLOCKS.get("packed_mud_resurrection_statue"),
				BLOCKS.get("clay_resurrection_statue"),
				BLOCKS.get("dripstone_resurrection_statue"),
				BLOCKS.get("mossy_cobblestone_resurrection_statue"),
				BLOCKS.get("snow_resurrection_statue"),
				BLOCKS.get("sculk_resurrection_statue"),
				
				BLOCKS.get("blue_ice_resurrection_statue"),
				
				BLOCKS.get("oak_resurrection_statue"),
				BLOCKS.get("spruce_resurrection_statue"),
				BLOCKS.get("birch_resurrection_statue"),
				BLOCKS.get("jungle_resurrection_statue"),
				BLOCKS.get("acacia_resurrection_statue"),
				BLOCKS.get("dark_oak_resurrection_statue"),
				BLOCKS.get("mangrove_resurrection_statue"),
				BLOCKS.get("cherry_resurrection_statue"),
				BLOCKS.get("crimson_resurrection_statue"),
				BLOCKS.get("warped_resurrection_statue"),
				
				BLOCKS.get("oak_plank_resurrection_statue"),
				BLOCKS.get("spruce_plank_resurrection_statue"),
				BLOCKS.get("birch_plank_resurrection_statue"),
				BLOCKS.get("jungle_plank_resurrection_statue"),
				BLOCKS.get("acacia_plank_resurrection_statue"),
				BLOCKS.get("dark_oak_plank_resurrection_statue"),
				BLOCKS.get("mangrove_plank_resurrection_statue"),
				BLOCKS.get("cherry_plank_resurrection_statue"),
				BLOCKS.get("crimson_plank_resurrection_statue"),
				BLOCKS.get("warped_plank_resurrection_statue"),
				
				BLOCKS.get("cobblestone_resurrection_statue"),
				BLOCKS.get("stone_resurrection_statue"),
				BLOCKS.get("andesite_resurrection_statue"),
				BLOCKS.get("granite_resurrection_statue"),
				BLOCKS.get("diorite_resurrection_statue"),
				BLOCKS.get("polished_andesite_resurrection_statue"),
				BLOCKS.get("polished_granite_resurrection_statue"),
				BLOCKS.get("polished_diorite_resurrection_statue"),
				BLOCKS.get("smooth_sandstone_resurrection_statue"),
				BLOCKS.get("smooth_red_sandstone_resurrection_statue"),
				BLOCKS.get("tuff_resurrection_statue"),
				BLOCKS.get("polished_tuff_resurrection_statue"),
				BLOCKS.get("calcite_resurrection_statue"),
				BLOCKS.get("amethyst_resurrection_statue"),
				BLOCKS.get("quartz_resurrection_statue"),
				BLOCKS.get("emerald_resurrection_statue"),
				BLOCKS.get("redstone_resurrection_statue"),
				BLOCKS.get("gold_resurrection_statue"),
				BLOCKS.get("lapis_resurrection_statue"),
				BLOCKS.get("diamond_resurrection_statue"),
				BLOCKS.get("netherite_resurrection_statue"),
				BLOCKS.get("obsidian_resurrection_statue"),
				BLOCKS.get("crying_obsidian_resurrection_statue"),
				BLOCKS.get("netherrack_resurrection_statue"),
				BLOCKS.get("blackstone_resurrection_statue"),
				BLOCKS.get("smooth_basalt_resurrection_statue"),
				BLOCKS.get("prismarine_resurrection_statue"),
				BLOCKS.get("dark_prismarine_resurrection_statue"),
				BLOCKS.get("polished_deepslate_resurrection_statue"),
				BLOCKS.get("polished_blackstone_resurrection_statue")
		);
		
		// # Register the Block Entity Types
		BLOCK_ENTITY_TYPES.put( "resurrection_block",
				Registry.register(
						Registries.BLOCK_ENTITY_TYPE,
						Identifier.of(MOD_ID, "resurrection_block"),
						builder.build()
				)
		);
	}
	
}
