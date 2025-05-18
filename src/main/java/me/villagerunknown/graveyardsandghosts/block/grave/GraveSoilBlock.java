package me.villagerunknown.graveyardsandghosts.block.grave;

import com.mojang.serialization.MapCodec;
import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import me.villagerunknown.graveyardsandghosts.block.entity.GraveSoilBlockEntity;
import me.villagerunknown.graveyardsandghosts.feature.graveyardBlocksFeature;
import me.villagerunknown.graveyardsandghosts.feature.playerGhostFeature;
import me.villagerunknown.graveyardsandghosts.helper.GraveyardMobHelper;
import me.villagerunknown.platform.timer.ServerTickTimer;
import me.villagerunknown.platform.timer.TickTimer;
import me.villagerunknown.platform.util.EntityUtil;
import me.villagerunknown.platform.util.MathUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GraveSoilBlock extends BlockWithEntity implements BlockEntityProvider {
	
	public static List<RegistryEntry<StatusEffect>> CURSED_STATUS_EFFECTS = List.of(
//			StatusEffects.INSTANT_DAMAGE,
//			StatusEffects.WITHER,
			StatusEffects.WEAKNESS,
			StatusEffects.SLOWNESS,
			StatusEffects.BAD_OMEN,
			StatusEffects.BLINDNESS,
			StatusEffects.DARKNESS,
			StatusEffects.HUNGER,
			StatusEffects.INFESTED,
			StatusEffects.MINING_FATIGUE,
			StatusEffects.NAUSEA,
			StatusEffects.OOZING,
			StatusEffects.POISON
	);
	
	public static final int TOTAL_MODELS = 3;
	public static final IntProperty MODEL = IntProperty.of("model", 0, TOTAL_MODELS - 1 );
	
	public static final MapCodec<GraveSoilBlock> CODEC = createCodec(GraveSoilBlock::new);
	
	private static final Random rand = new Random();
	
	public GraveSoilBlock() {
		super(
				Settings.copy(Blocks.DIRT)
						.solid()
		);
	}
	
	public GraveSoilBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{MODEL});
	}
	
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with( MODEL, getDimensionalModel(ctx.getWorld()) );
	}
	
	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if( world.isClient() ) {
			return;
		} // if
		
		if( entity.isPlayer() ) {
			// Play sound
			playSound(world, pos);
			
			// Spawn mob
			spawnEntity(world, pos.up(), (LivingEntity) entity);
		}
		super.onSteppedOn(world, pos, state, entity);
	}
	
	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		if( world.isClient() ) {
			return;
		} // if
		
		// Spawn mob
		spawnEntity(world, pos, player);
		
		super.afterBreak(world, player, pos, state, blockEntity, tool);
	}
	
	public int getDimensionalModel( World world ) {
		return GraveSoilBlockEntity.getDimensionalModel( world );
	}
	
	@Override
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if( world.isClient() ) {
			return;
		} // if

		state = state.with( MODEL, getDimensionalModel(world) );
		
		world.setBlockState( pos, state );
		
		super.onBlockAdded(state, world, pos, oldState, notify);
	}
	
//	@Override
//	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
//		if( world.isClient() ) {
//			return;
//		} // if
//
//		LogUtil.info("block placed");
//
//		state = state.with( MODEL, getDimensionalModel(world) );
//
//		world.setBlockState( pos, state );
//
//		super.onPlaced(world, pos, state, placer, itemStack);
//	}
	
	protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}
	
	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}
	
	@Override
	protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		if( world.isClient() ) {
			return;
		} // if
		
		// Play sound
		playSound(world, pos );
		
		super.onBlockBreakStart(state, world, pos, player);
	}
	
	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if( world.isClient() ) {
			return super.onBreak(world, pos, state, player);
		} // if
		
		GraveSoilBlockEntity.soundTimers.remove( pos );
		GraveSoilBlockEntity.spawnTimers.remove( pos );
		
		return super.onBreak(world, pos, state, player);
	}
	
	@Override
	public boolean canMobSpawnInside(BlockState state) {
		return false;
	}
	
	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if( world.isClient() ) {
			return;
		} // if
		
		if( entity.isPlayer() || entity instanceof VillagerEntity || entity instanceof IronGolemEntity ) {
			// Play sound
			playSound(world, pos);
			
			// Spawn mob
			spawnEntity(world, pos.up(), (LivingEntity) entity);
		} // if
		
		super.onLandedUpon(world, state, pos, entity, fallDistance);
	}
	
	@Override
	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return false;
	}
	
	@Override
	protected MapCodec<? extends GraveSoilBlock> getCodec() {
		return CODEC;
	}
	
	
	private static void playSound( World world, BlockPos pos ) {
		if( world.isClient() ) {
			return;
		}
		
		float chance = Graveyardsandghosts.CONFIG.graveyardBlockMobSoundChance;
		
		if( world.getTimeOfDay() % 24000 > 13000L ) {
			chance = chance * 2;
		}
		
		if( null != world.getServer() ) {
			long currentTick = world.getServer().getTicks();
			
			if (Graveyardsandghosts.CONFIG.enableSounds && Graveyardsandghosts.CONFIG.enableGraveyardBlockSounds && MathUtil.hasChance(chance)) {
				if (null == GraveSoilBlockEntity.soundTimers.get(pos)) {
					soundAttempt(world, pos);
					
					GraveSoilBlockEntity.soundTimers.put(pos, new ServerTickTimer(currentTick, 0, 5));
					GraveSoilBlockEntity.soundTimers.put(pos.up(), new ServerTickTimer(currentTick, 0, 5));
					GraveSoilBlockEntity.soundTimers.put(pos.north(), new ServerTickTimer(currentTick, 0, 5));
					GraveSoilBlockEntity.soundTimers.put(pos.east(), new ServerTickTimer(currentTick, 0, 5));
					GraveSoilBlockEntity.soundTimers.put(pos.west(), new ServerTickTimer(currentTick, 0, 5));
					GraveSoilBlockEntity.soundTimers.put(pos.south(), new ServerTickTimer(currentTick, 0, 5));
					GraveSoilBlockEntity.soundTimers.put(pos.down(), new ServerTickTimer(currentTick, 0, 5));
				}
				
				if (null != GraveSoilBlockEntity.soundTimers.get(pos) && GraveSoilBlockEntity.soundTimers.get(pos).isAlarmActivated()) {
					soundAttempt(world, pos);
					
					GraveSoilBlockEntity.soundTimers.get(pos).resetAlarmActivation( currentTick );
					GraveSoilBlockEntity.soundTimers.get(pos.up()).resetAlarmActivation( currentTick );
					GraveSoilBlockEntity.soundTimers.get(pos.north()).resetAlarmActivation( currentTick );
					GraveSoilBlockEntity.soundTimers.get(pos.east()).resetAlarmActivation( currentTick );
					GraveSoilBlockEntity.soundTimers.get(pos.west()).resetAlarmActivation( currentTick );
					GraveSoilBlockEntity.soundTimers.get(pos.south()).resetAlarmActivation( currentTick );
					GraveSoilBlockEntity.soundTimers.get(pos.down()).resetAlarmActivation( currentTick );
				} // if
			} // if
		} // if
	}
	
	private static void spawnEntity( World world, BlockPos pos, LivingEntity target ) {
		if( world.isClient() || target.hasStatusEffect(playerGhostFeature.GHOST_EFFECT_REGISTRY ) ) {
			return;
		} // if
		
		float chance = Graveyardsandghosts.CONFIG.graveyardBlockMobSpawnChance;
		
		if( world.getTimeOfDay() % 24000 > 13000L ) {
			chance = chance * 2;
		} // if
		
		if( null != world.getServer() ) {
			long currentTick = world.getServer().getTicks();
			
			if (Graveyardsandghosts.CONFIG.enableGraveyardBlockMobSpawns && MathUtil.hasChance(chance)) {
				if (null == GraveSoilBlockEntity.spawnTimers.get(pos.down())) {
					spawnAttempt(world, pos, target);
					
					GraveSoilBlockEntity.spawnTimers.put(pos.down(), new ServerTickTimer(currentTick, Graveyardsandghosts.CONFIG.graveyardBlockMobSpawnDelayInMinutes));
					GraveSoilBlockEntity.spawnTimers.put(pos.down().up(), new ServerTickTimer(currentTick, Graveyardsandghosts.CONFIG.graveyardBlockMobSpawnDelayInMinutes));
					GraveSoilBlockEntity.spawnTimers.put(pos.down().north(), new ServerTickTimer(currentTick, Graveyardsandghosts.CONFIG.graveyardBlockMobSpawnDelayInMinutes));
					GraveSoilBlockEntity.spawnTimers.put(pos.down().east(), new ServerTickTimer(currentTick, Graveyardsandghosts.CONFIG.graveyardBlockMobSpawnDelayInMinutes));
					GraveSoilBlockEntity.spawnTimers.put(pos.down().west(), new ServerTickTimer(currentTick, Graveyardsandghosts.CONFIG.graveyardBlockMobSpawnDelayInMinutes));
					GraveSoilBlockEntity.spawnTimers.put(pos.down().south(), new ServerTickTimer(currentTick, Graveyardsandghosts.CONFIG.graveyardBlockMobSpawnDelayInMinutes));
					GraveSoilBlockEntity.spawnTimers.put(pos.down().down(), new ServerTickTimer(currentTick, Graveyardsandghosts.CONFIG.graveyardBlockMobSpawnDelayInMinutes));
				} else if (GraveSoilBlockEntity.spawnTimers.get(pos.down()).isAlarmActivated()) {
					spawnAttempt(world, pos, target);
					
					GraveSoilBlockEntity.spawnTimers.get(pos.down()).resetAlarmActivation( currentTick );
					GraveSoilBlockEntity.spawnTimers.get(pos.down().up()).resetAlarmActivation( currentTick );
					GraveSoilBlockEntity.spawnTimers.get(pos.down().north()).resetAlarmActivation( currentTick );
					GraveSoilBlockEntity.spawnTimers.get(pos.down().east()).resetAlarmActivation( currentTick );
					GraveSoilBlockEntity.spawnTimers.get(pos.down().west()).resetAlarmActivation( currentTick );
					GraveSoilBlockEntity.spawnTimers.get(pos.down().south()).resetAlarmActivation( currentTick );
					GraveSoilBlockEntity.spawnTimers.get(pos.down().down()).resetAlarmActivation( currentTick );
				} // if
			} // if
		} // if
	}
	
	private static void soundAttempt(World world, BlockPos pos) {
		GraveyardMobHelper MobHelper = new GraveyardMobHelper(world);
		MobEntity mob = MobHelper.getMobByDimension( pos, false );
		mob.playAmbientSound();
	}
	
	private static void spawnAttempt(World world, BlockPos pos, LivingEntity target) {
		GraveyardMobHelper MobHelper = new GraveyardMobHelper(world, target);
		
		if( MobHelper.spawnMobByDimension( pos, true ) ) {
			target.playSound(SoundEvents.ENTITY_WARDEN_DIG);
			if( target.isPlayer() ) {
				target.sendMessage(Text.of("You disturbed a grave!"));
			} // if
		} else {
			applyStatusEffect( world, target );
		} // if, else
	}
	
	private static void applyStatusEffect( World world, LivingEntity target ) {
		if( world.isClient() ) {
			return;
		} // if
		
		RegistryEntry<StatusEffect> randomEffect = CURSED_STATUS_EFFECTS.get( rand.nextInt( CURSED_STATUS_EFFECTS.size() ) );
		
		if( target.isPlayer() ) {
			EntityUtil.addStatusEffect( target, randomEffect, Math.round(MathUtil.getRandomWithinRange(60, 120)), Math.round(MathUtil.getRandomWithinRange(1, 3)), false, true, true );
		}
	}
	
	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new GraveSoilBlockEntity( pos, state );
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker( type, graveyardBlocksFeature.BLOCK_ENTITY_TYPES.get("grave_soil"), GraveSoilBlockEntity::tick );
	}
	
	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
}
