package me.villagerunknown.graveyardsandghosts.block.resurrection;

import com.mojang.serialization.MapCodec;
import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import me.villagerunknown.graveyardsandghosts.block.entity.ResurrectionBlockEntity;
import me.villagerunknown.graveyardsandghosts.feature.graveyardBlocksFeature;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TwoTallResurrectionBlock extends BlockWithEntity implements BlockEntityProvider {
	
	protected static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);
	protected static final VoxelShape SHAPE_EAST = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);
	protected static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);
	protected static final VoxelShape SHAPE_WEST = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);
	
	public static final MapCodec<TwoTallResurrectionBlock> CODEC = createCodec(TwoTallResurrectionBlock::new);
	
	public TwoTallResurrectionBlock() {
		super(
				Settings.copy(Blocks.NETHERITE_BLOCK)
						.dynamicBounds()
						.nonOpaque()
						.solid()
		);
	}
	
	protected TwoTallResurrectionBlock(Settings settings) {
		super(settings);
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
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		Graveyardsandghosts.LOGGER.info("created");
		return new ResurrectionBlockEntity( pos, state );
	}
	
	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		Graveyardsandghosts.LOGGER.info("ticker");
//		return BlockEntityProvider.super.getTicker(world, state, customBlocksFeature.BLOCK_ENTITY_TYPES.get("resurrection_statue"));
		return validateTicker( type, graveyardBlocksFeature.BLOCK_ENTITY_TYPES.get("resurrection_statue"), ResurrectionBlockEntity::tick );
	}
	
	@Override
	protected MapCodec<? extends TwoTallResurrectionBlock> getCodec() {
		return CODEC;
	}
	
}
