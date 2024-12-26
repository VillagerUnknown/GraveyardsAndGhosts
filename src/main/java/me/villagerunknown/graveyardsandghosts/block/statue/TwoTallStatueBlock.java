package me.villagerunknown.graveyardsandghosts.block.statue;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import static me.villagerunknown.graveyardsandghosts.feature.ghostRespawnFeature.respawnPositions;

public class TwoTallStatueBlock extends StatueBlock {
	
	protected static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);
	protected static final VoxelShape SHAPE_EAST = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);
	protected static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);
	protected static final VoxelShape SHAPE_WEST = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);
	
	public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
	
	public static final MapCodec<TwoTallStatueBlock> CODEC = createCodec(TwoTallStatueBlock::new);
	
	public TwoTallStatueBlock() {
		super(
				Settings.copy(Blocks.STONE)
						.dynamicBounds()
						.nonOpaque()
						.solid()
		);
	}
	
	protected TwoTallStatueBlock(Settings settings) {
		super(settings);
	}
	
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		World world = ctx.getWorld();
		if (blockPos.getY() < world.getTopY() - 1 && world.getBlockState(blockPos.up()).canReplace(ctx)) {
			return withWaterloggedState(world, blockPos, this.getDefaultState()
					.with(FACING, ctx.getHorizontalPlayerFacing())
					.with(HALF, DoubleBlockHalf.LOWER)
			);
		} else {
			return Blocks.AIR.getDefaultState();
		}
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{HALF,FACING,WATERLOGGED});
	}
	
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		BlockPos blockPos = pos.up();
		world.setBlockState(blockPos, withWaterloggedState(world, blockPos, (BlockState)this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER).with(FACING, placer.getHorizontalFacing())), 3);
	}
	
	public static BlockState withWaterloggedState(WorldView world, BlockPos pos, BlockState state) {
		return state.contains(Properties.WATERLOGGED) ? (BlockState)state.with(Properties.WATERLOGGED, world.isWater(pos)) : state;
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
	
	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		if( state.get(HALF) == DoubleBlockHalf.UPPER ) {
			return null;
		}
		return BlockRenderType.MODEL;
	}
	
	protected long getRenderingSeed(BlockState state, BlockPos pos) {
		return MathHelper.hashCode(pos.getX(), pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
	}
	
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient) {
			if (player.isCreative()) {
				onBreakInCreative(world, pos, state, player);
			} else {
				dropStacks(state, world, pos, (BlockEntity)null, player, player.getMainHandStack());
			}
			
			if( state.get(HALF) == DoubleBlockHalf.LOWER ) {
				world.breakBlock(pos.up(),false);
			} // if
		}
		
		return super.onBreak(world, pos, state, player);
	}
	
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, tool);
	}
	
	protected static void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		DoubleBlockHalf doubleBlockHalf = (DoubleBlockHalf)state.get(HALF);
		if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
			BlockPos blockPos = pos.down();
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.isOf(state.getBlock()) && blockState.get(HALF) == DoubleBlockHalf.LOWER) {
				BlockState blockState2 = blockState.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
				world.setBlockState(blockPos, blockState2, 35);
				world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
			}
		}
		
	}
	
	@Override
	protected MapCodec<? extends TwoTallStatueBlock> getCodec() {
		return CODEC;
	}
	
}
