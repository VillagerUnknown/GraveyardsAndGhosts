package me.villagerunknown.graveyardsandghosts.mixin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.villagerunknown.graveyardsandghosts.data.PlayerData;
import me.villagerunknown.graveyardsandghosts.data.persistent.PersistentPlayerData;
import me.villagerunknown.graveyardsandghosts.feature.ghostRespawnFeature;
import me.villagerunknown.graveyardsandghosts.feature.playerGhostFeature;
import me.villagerunknown.platform.util.GsonUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {
	
	@Unique
	private static final Gson gson = GsonUtil.gsonWithAdapters();
	
	/**
	 * + Show resurrection prompt to ghosts
	 * + Add nether respawn point when player interacts with a respawn anchor
	 *
	 * @param state
	 * @param world
	 * @param pos
	 * @param player
	 * @param hit
	 * @param cir
	 */
	@Inject(method = "onUse", at = @At("RETURN"))
	private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if( !world.isClient() ) {
			// # Add nether respawn point when player interacts with a respawn anchor
			if(cir.getReturnValue() == ActionResult.SUCCESS_SERVER) {
				PlayerData playerData = PersistentPlayerData.getPlayerState(player);
				
				Map<String, Set<BlockPos>> playerRespawnPositions = gson.fromJson( playerData.respawnPositions, new TypeToken<Map<String, Set<BlockPos>>>() {}.getType() );
				
				if( null == playerRespawnPositions ) {
					playerRespawnPositions = new HashMap<>();
				} // if
				
				Set<BlockPos> dimensionPositions = playerRespawnPositions.getOrDefault( world.getRegistryKey().getValue().toString(), new HashSet<>() );
				
				if( null == dimensionPositions ) {
					dimensionPositions = Set.of();
				}
				
				dimensionPositions.add( pos );
				
				playerRespawnPositions.put( world.getRegistryKey().getValue().toString(), dimensionPositions );
				
				playerData.respawnPositions = gson.toJson(playerRespawnPositions, new TypeToken<Map<String, Set<BlockPos>>>() {}.getType());
			} // if
		}
	}
	
}
