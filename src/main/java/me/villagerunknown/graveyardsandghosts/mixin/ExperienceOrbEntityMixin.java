package me.villagerunknown.graveyardsandghosts.mixin;

import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import me.villagerunknown.graveyardsandghosts.feature.playerGhostFeature;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {

	@Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
	private void onPlayerCollision(PlayerEntity player, CallbackInfo ci){
		if( Graveyardsandghosts.CONFIG.preventGhostItemPickup ) {
			if (player.isAlive() && player.hasStatusEffect(playerGhostFeature.GHOST_EFFECT_REGISTRY)) {
				ci.cancel();
			} // if
		} // if
	}

}
