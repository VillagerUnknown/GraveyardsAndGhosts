package me.villagerunknown.graveyardsandghosts.block.statue;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.random.Random;

public class RandomTwoTallStatueBlock extends TwoTallStatueBlock {
	
	public static final int TOTAL_MODELS = 3;
	public static final IntProperty RANDOM_MODEL = IntProperty.of("random_model", 0, TOTAL_MODELS - 1);
	
	public static final MapCodec<RandomTwoTallStatueBlock> CODEC = createCodec(RandomTwoTallStatueBlock::new);
	
	public RandomTwoTallStatueBlock() {
		super(
				Settings.copy(Blocks.STONE)
						.dynamicBounds()
						.nonOpaque()
						.solid()
		);
	}
	
	protected RandomTwoTallStatueBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{FACING,RANDOM_MODEL,WATERLOGGED});
	}
	
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState()
				.with(FACING, ctx.getHorizontalPlayerFacing())
				.with(RANDOM_MODEL, Random.create().nextInt(TOTAL_MODELS))
				.with(WATERLOGGED, false);
	}
	
	@Override
	protected MapCodec<? extends RandomTwoTallStatueBlock> getCodec() {
		return CODEC;
	}
	
}
