package me.villagerunknown.graveyardsandghosts.mixin;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.BedBlock.*;

@Mixin(BedBlock.class)
public class BedBlockMixin {
	
	/**
	 * Prevent respawn point message when a player interacts with a bed
	 *
	 * @param state
	 * @param world
	 * @param pos
	 * @param player
	 * @param hit
	 * @param cir
	 */
	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if( !world.isClient() ) {
			if( state.get(PART) == BedPart.HEAD && isBedWorking(world) && !(Boolean)state.get(OCCUPIED)) {
				player.trySleep(pos).ifLeft((reason) -> {
					if (reason.getMessage() != null) {
						if( reason.getMessage().contains(Text.of("respawn")) ) {
							cir.setReturnValue( ActionResult.SUCCESS );
						} // if
					}
					
				});
			}
		}
	}
	
}
