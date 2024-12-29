package me.villagerunknown.graveyardsandghosts.mixin;

import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import me.villagerunknown.graveyardsandghosts.feature.playerGhostFeature;
import me.villagerunknown.graveyardsandghosts.statuseffect.GhostEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
	
	@Unique
	private void setNoClip(Entity entity, boolean value ) {
		if( entity instanceof LivingEntity livingEntity) {
			if( !Graveyardsandghosts.CONFIG.preventGhostCollisions && livingEntity.hasStatusEffect(playerGhostFeature.GHOST_EFFECT_REGISTRY) ) {
				entity.noClip = value;
				entity.verticalCollision = !value;
				entity.horizontalCollision = !value;
			} // if
		} // if
	}
	
	@Inject(method = "baseTick", at = @At("HEAD"))
	private void baseTick( CallbackInfo ci ) {
		Entity entity = (Entity) (Object) this;
		setNoClip( entity, true );
	}
	
	@Inject(method = "isFireImmune", at = @At("HEAD"), cancellable = true)
	private void isFireImmune(CallbackInfoReturnable<Boolean> cir) {
		Entity entity = (Entity) (Object) this;
		if( entity instanceof LivingEntity livingEntity) {
			if( livingEntity.hasStatusEffect(playerGhostFeature.GHOST_EFFECT_REGISTRY) ) {
				cir.setReturnValue(true);
			} // if
		} // if
	}
	
}
