package me.villagerunknown.graveyardsandghosts.block.tombstone;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BrokenTombstoneBlock extends TombstoneBlock {
	
	protected static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 13.0, 3.0);
	protected static final VoxelShape SHAPE_EAST = Block.createCuboidShape(13.0, 0.0, 1.0, 15.0, 13.0, 15.0);
	protected static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(1.0, 0.0, 13.0, 15.0, 13.0, 15.0);
	protected static final VoxelShape SHAPE_WEST = Block.createCuboidShape(1.0, 0.0, 1.0, 3.0, 13.0, 15.0);
	
	public static final int TOTAL_MODELS = 2;
	public static final IntProperty RANDOM_MODEL = IntProperty.of("random_model", 0, TOTAL_MODELS - 1);
	
	public static final MapCodec<BrokenTombstoneBlock> CODEC = createCodec(BrokenTombstoneBlock::new);
	
	public BrokenTombstoneBlock() {
		super(
				Settings.copy(Blocks.STONE)
						.dynamicBounds()
						.nonOpaque()
						.solid()
						.breakInstantly()
		);
	}
	
	public BrokenTombstoneBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	protected MapCodec<? extends BrokenTombstoneBlock> getCodec() {
		return CODEC;
	}
	
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState()
				.with(FACING, ctx.getHorizontalPlayerFacing())
				.with(RANDOM_MODEL, Random.create().nextInt(TOTAL_MODELS))
				.with(WATERLOGGED, false);
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
	
}
