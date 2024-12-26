package me.villagerunknown.graveyardsandghosts.mixin;

import com.google.common.base.Predicates;
import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import me.villagerunknown.graveyardsandghosts.feature.playerGhostFeature;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	
	@Shadow public abstract ItemStack getStack();
	
	@Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
	private void onPlayerCollision(PlayerEntity player, CallbackInfo ci){
		if( Graveyardsandghosts.CONFIG.preventGhostItemPickup ) {
			if (player.isAlive() && player.hasStatusEffect(playerGhostFeature.GHOST_EFFECT_REGISTRY)) {
				ItemStack itemStack = this.getStack();
				Item item = itemStack.getItem();
				
				if( !playerGhostFeature.allowedGhostPickupItems.contains( item ) ){
					ci.cancel();
				} // if
			} // if
		} // if
	}

}
