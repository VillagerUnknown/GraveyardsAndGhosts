package me.villagerunknown.graveyardsandghosts.block.entity;

import me.villagerunknown.graveyardsandghosts.feature.graveyardBlocksFeature;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class TombstoneBlockEntity extends SignBlockEntity {
	
	public TombstoneBlockEntity(BlockPos pos, BlockState state) {
		super(graveyardBlocksFeature.BLOCK_ENTITY_TYPES.get("tombstone"), pos, state);
	}
	
}
