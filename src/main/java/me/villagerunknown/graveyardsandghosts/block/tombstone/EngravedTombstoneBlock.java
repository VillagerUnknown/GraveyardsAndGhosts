package me.villagerunknown.graveyardsandghosts.block.tombstone;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class EngravedTombstoneBlock extends HorizontalFacingBlock implements Waterloggable {
	
	protected static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 3.0);
	protected static final VoxelShape SHAPE_EAST = Block.createCuboidShape(13.0, 0.0, 1.0, 15.0, 16.0, 15.0);
	protected static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(1.0, 0.0, 13.0, 15.0, 16.0, 15.0);
	protected static final VoxelShape SHAPE_WEST = Block.createCuboidShape(1.0, 0.0, 1.0, 3.0, 16.0, 15.0);
	
	public static final int TOTAL_MODELS = 2;
	public static final IntProperty RANDOM_MODEL = IntProperty.of("random_model", 0, TOTAL_MODELS - 1);
	
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	
	public static final MapCodec<EngravedTombstoneBlock> CODEC = createCodec(EngravedTombstoneBlock::new);
	
	public EngravedTombstoneBlock() {
		super(
				Settings.copy(Blocks.PLAYER_HEAD)
						.dynamicBounds()
						.nonOpaque()
						.solid()
						.breakInstantly()
		);
	}
	
	public EngravedTombstoneBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	protected MapCodec<? extends EngravedTombstoneBlock> getCodec() {
		return CODEC;
	}
	
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState()
				.with(FACING, ctx.getHorizontalPlayerFacing())
				.with(RANDOM_MODEL, Random.create().nextInt(TOTAL_MODELS))
				.with(WATERLOGGED, false);
	}
	
	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{FACING,RANDOM_MODEL,WATERLOGGED});
	}
	
	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if( state.contains( HorizontalFacingBlock.FACING ) ) {
			VoxelShape shape = switch (state.get(HorizontalFacingBlock.FACING).toString()) {
				case "south" -> SHAPE_SOUTH;
				case "north" -> SHAPE_NORTH;
				case "west" -> SHAPE_WEST;
				default -> SHAPE_EAST;
			};
			return shape;
		}
		return SHAPE_EAST;
	}
	
	@Override
	public boolean canMobSpawnInside(BlockState state) {
		return true;
	}
	
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}
	
	protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return !(Boolean)state.get(WATERLOGGED);
	}
	
	@Override
	protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}
}
