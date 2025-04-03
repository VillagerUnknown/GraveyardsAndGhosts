package me.villagerunknown.graveyardsandghosts.client.mixin;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
	
	@Inject(method = "shouldRenderFeatures(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)Z", at = @At("HEAD"), cancellable = true)
	public void shouldRenderFeatures(PlayerEntityRenderState playerEntityRenderState, CallbackInfoReturnable<Boolean> cir) {
		if( playerEntityRenderState.invisible ) {
			cir.setReturnValue( false );
		} // if
	}
	
}
