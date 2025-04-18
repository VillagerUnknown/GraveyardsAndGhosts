package me.villagerunknown.graveyardsandghosts.statuseffect;

import me.villagerunknown.graveyardsandghosts.feature.playerGhostFeature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class GhostEffect extends StatusEffect {
	
	public GhostEffect() {
		super(StatusEffectCategory.NEUTRAL, 0xADD8E6);
	}
	
	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	public void applyInstantEffect(ServerWorld world, @Nullable Entity effectEntity, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
		if( target instanceof ServerPlayerEntity ) {
			playerGhostFeature.applyGhostAbilities(target, amplifier);
		} // if
		
		super.applyInstantEffect(world, effectEntity, attacker, target, amplifier, proximity);
	}
	
	public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
		if( entity instanceof ServerPlayerEntity ) {
			playerGhostFeature.applyGhostAbilities(entity, amplifier);
		} // if
		
		return super.applyUpdateEffect(world, entity, amplifier);
	}
	
	public void onEntityRemoval(ServerWorld world, LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
		if( entity instanceof ServerPlayerEntity ) {
			playerGhostFeature.applyHumanAbilities(entity);
		} // if
		
		super.onEntityRemoval(world, entity, amplifier, reason);
	}
	
}
