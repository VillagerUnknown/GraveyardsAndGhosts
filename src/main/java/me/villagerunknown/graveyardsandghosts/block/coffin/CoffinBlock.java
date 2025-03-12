package me.villagerunknown.graveyardsandghosts.block.coffin;

import com.mojang.serialization.MapCodec;
import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import me.villagerunknown.graveyardsandghosts.block.entity.CoffinBlockEntity;
import me.villagerunknown.graveyardsandghosts.feature.graveyardBlocksFeature;
import me.villagerunknown.graveyardsandghosts.helper.GraveyardMobHelper;
import me.villagerunknown.platform.timer.TickTimer;
import me.villagerunknown.platform.util.MathUtil;
import me.villagerunknown.platform.util.PositionUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class CoffinBlock extends BlockWithEntity implements Waterloggable {
	
	protected static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape SHAPE_EAST = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape SHAPE_WEST = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	
	public static final int TOTAL_MODELS = 2;
	public static final IntProperty MODEL = IntProperty.of("model", 0, TOTAL_MODELS - 1 );
	
	public static final EnumProperty<BedPart> PART = Properties.BED_PART;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	
	public static final MapCodec<CoffinBlock> CODEC = createCodec(CoffinBlock::new);
	
	public CoffinBlock() {
		super(
				Settings.copy(Blocks.STONE)
						.dynamicBounds()
						.nonOpaque()
						.solid()
		);
	}
	
	protected CoffinBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{FACING,PART,MODEL,WATERLOGGED});
	}
	
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getHorizontalPlayerFacing();
		BlockPos blockPos = ctx.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(direction);
		World world = ctx.getWorld();
		return world.getBlockState(blockPos2).canReplace(ctx) && world.getWorldBorder().contains(blockPos2) ? (BlockState)this.getDefaultState().with(FACING, direction).with(PART, BedPart.FOOT).with(MODEL, 1).with(WATERLOGGED, false) : null;
	}
	
	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
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
		} // if
		
		if (direction == getDirectionTowardsOtherPart((BedPart)state.get(PART), (Direction)state.get(FACING))) {
			return neighborState.isOf(this) && neighborState.get(PART) != state.get(PART) ? state : Blocks.AIR.getDefaultState();
		} else {
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} // if, else
	}
	
	@Nullable
	public static Direction getDirection(BlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return blockState.getBlock() instanceof CoffinBlock ? (Direction)blockState.get(FACING) : null;
	}
	
	private static Direction getDirectionTowardsOtherPart(BedPart part, Direction direction) {
		return part == BedPart.FOOT ? direction : direction.getOpposite();
	}
	
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient) {
			BedPart bedPart = (BedPart)state.get(PART);
			BlockPos joinedBlockPos = pos.offset(getDirectionTowardsOtherPart(bedPart, (Direction)state.get(FACING)));
			BlockState joinedBlockState = world.getBlockState(joinedBlockPos);
			
			if( joinedBlockState.isOf(this) ) {
				if( player.isCreative() ) {
					world.setBlockState(joinedBlockPos, Blocks.AIR.getDefaultState(), 35);
					world.syncWorldEvent(player, 2001, joinedBlockPos, Block.getRawIdFromState(joinedBlockState));
				} // if
				
				world.breakBlock(joinedBlockPos,false);
			} // if
		} // if
		
		return super.onBreak(world, pos, state, player);
	}
	
	public static Direction getOppositePartDirection(BlockState state) {
		Direction direction = (Direction)state.get(FACING);
		return state.get(PART) == BedPart.HEAD ? direction.getOpposite() : direction;
	}
	
	public static DoubleBlockProperties.Type getBedPart(BlockState state) {
		BedPart bedPart = (BedPart)state.get(PART);
		return bedPart == BedPart.HEAD ? DoubleBlockProperties.Type.FIRST : DoubleBlockProperties.Type.SECOND;
	}
	
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		if (!world.isClient) {
			BlockPos blockPos = pos.offset((Direction)state.get(FACING));
			world.setBlockState(blockPos, (BlockState)state.with(PART, BedPart.HEAD), 3);
			world.updateNeighbors(pos, Blocks.AIR);
			state.updateNeighbors(world, pos, 3);
		}
	}
	
	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		if( state.get(PART) == BedPart.HEAD ) {
			return null;
		}
		
		return BlockRenderType.MODEL;
	}
	
	protected long getRenderingSeed(BlockState state, BlockPos pos) {
		BlockPos blockPos = pos.offset((Direction)state.get(FACING), state.get(PART) == BedPart.HEAD ? 0 : 1);
		return MathHelper.hashCode(blockPos.getX(), pos.getY(), blockPos.getZ());
	}
	
	@Override
	protected MapCodec<? extends CoffinBlock> getCodec() {
		return CODEC;
	}
	
	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CoffinBlockEntity(pos,state);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!world.isClient) {
			// This will call the createScreenHandlerFactory method from BlockWithEntity, which will return our blockEntity casted to
			// a namedScreenHandlerFactory. If your block class does not extend BlockWithEntity, it needs to implement createScreenHandlerFactory.
			NamedScreenHandlerFactory screenHandlerFactory = null;
			
			if( state.get( PART ).equals( BedPart.HEAD ) ) {
				screenHandlerFactory = state.createScreenHandlerFactory(world, pos.offset(getDirectionTowardsOtherPart(state.get(PART), (Direction)state.get(FACING))));
			} else {
				screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
			} // if, else
			
			if (screenHandlerFactory != null) {
				// With this call the server will request the client to open the appropriate Screenhandler
				player.openHandledScreen(screenHandlerFactory);
			}
			
			float mobSpawnChance = Graveyardsandghosts.CONFIG.graveyardBlockMobSpawnChance;
			
			if( state.get( MODEL ).equals(1) ) {
				mobSpawnChance = 0F;
			}
			
			MinecraftServer minecraftServer = world.getServer();
			ServerWorld serverWorld = minecraftServer.getWorld(world.getRegistryKey());
			
			if( serverWorld.getTimeOfDay() % 24000 > 13000L ) {
				mobSpawnChance = mobSpawnChance * 2;
			}
			
			world.playSound( null, pos, SoundEvents.BLOCK_BARREL_OPEN, SoundCategory.AMBIENT, Graveyardsandghosts.CONFIG.soundVolume, 1.0F );
			
			if( MathUtil.hasChance( mobSpawnChance ) && Graveyardsandghosts.CONFIG.enableGraveyardBlockMobSpawns ) {
				if( null == CoffinBlockEntity.spawnTimers.get( pos ) || CoffinBlockEntity.spawnTimers.get( pos ).isAlarmActivated() ) {
					if( null == CoffinBlockEntity.spawnTimers.get( pos ) ) {
						CoffinBlockEntity.spawnTimers.put(pos, new TickTimer( Graveyardsandghosts.CONFIG.graveyardBlockMobSpawnDelayInMinutes ));
					} else {
						CoffinBlockEntity.spawnTimers.get( pos ).resetAlarmActivation();
					} // if, else
					
					BlockPos safeSpawnPosition = PositionUtil.findSafeSpawnPosition(serverWorld, pos, Graveyardsandghosts.CONFIG.graveyardBlockMobSpawnSearchRadius);
					GraveyardMobHelper MobHelper = new GraveyardMobHelper(serverWorld, (ServerPlayerEntity) player);
					MobHelper.spawnMobByDimension( safeSpawnPosition, true );
				} // if
			} // if
		}
		
		return ActionResult.SUCCESS;
	}
	
	// This method will drop all items onto the ground when the block is broken
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CoffinBlockEntity coffinBlockEntity) {
				ItemScatterer.spawn(world, pos, coffinBlockEntity);
				// update comparators
				world.updateComparators(pos,this);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}
	
	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}
	
	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
	}
	
	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
	}
	
	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}
	
	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker( type, graveyardBlocksFeature.BLOCK_ENTITY_TYPES.get("coffin_block"), CoffinBlockEntity::tick );
	}
	
}
