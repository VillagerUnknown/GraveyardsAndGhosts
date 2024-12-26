package me.villagerunknown.graveyardsandghosts.block.entity;

import me.villagerunknown.graveyardsandghosts.feature.graveyardBlocksFeature;
import me.villagerunknown.platform.timer.TickTimer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

import static me.villagerunknown.graveyardsandghosts.block.grave.GraveSoilBlock.MODEL;

public class GraveSoilBlockEntity extends BlockEntity {
	
	public static Map<BlockPos, Boolean> blocksLoaded = new HashMap<>();
	
	public static Map<BlockPos, TickTimer> soundTimers = new HashMap<>();
	public static Map<BlockPos, TickTimer> spawnTimers = new HashMap<>();
	
	public GraveSoilBlockEntity(BlockPos pos, BlockState state) {
		super(graveyardBlocksFeature.BLOCK_ENTITY_TYPES.get("grave_soil"), pos, state);
	}
	
	public GraveSoilBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public static int getDimensionalModel(World world) {
		String dimension = world.getDimensionEntry().getIdAsString().replace("minecraft:","");
		
		return switch (dimension) {
			case "the_nether" -> 1;
			case "the_end" -> 2;
			default -> 0;
		};
	}
	
	public static void tick(World world, BlockPos pos, BlockState state, GraveSoilBlockEntity blockEntity) {
		if( null == world.getServer() || world.isClient() ) {
			return;
		} // if
		
		if( null == blocksLoaded.get( pos ) ) {
			blocksLoaded.put( pos, true );

			state = state.with( MODEL, getDimensionalModel(world) );

			world.setBlockState( pos, state );
		} // if
		
		
		// # Sound Timers
		if( null != soundTimers.get( pos ) ) {
			soundTimers.get(pos).tick();
		} // if
		
		// # Spawn Timers
		if( null != spawnTimers.get( pos ) ) {
			spawnTimers.get(pos).tick();
		} // if
	}
}
