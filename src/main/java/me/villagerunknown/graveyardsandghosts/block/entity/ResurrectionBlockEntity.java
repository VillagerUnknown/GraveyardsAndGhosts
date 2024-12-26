package me.villagerunknown.graveyardsandghosts.block.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import me.villagerunknown.graveyardsandghosts.GraveyardsandghostsPersistentData;
import me.villagerunknown.graveyardsandghosts.GraveyardsandghostsPersistentPlayerData;
import me.villagerunknown.graveyardsandghosts.feature.graveyardBlocksFeature;
import me.villagerunknown.graveyardsandghosts.feature.ghostRespawnFeature;
import me.villagerunknown.platform.timer.TickTimer;
import me.villagerunknown.platform.util.GsonUtil;
import me.villagerunknown.platform.util.MathUtil;
import me.villagerunknown.platform.util.MessageUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class ResurrectionBlockEntity extends BlockEntity {
	
	private static int blockTimerFrequencyInSeconds = 3;
	private static int blockActivationDistance = 64;
	
	public static TickTimer resurrectionBlockTimer = new TickTimer( 0, blockTimerFrequencyInSeconds );
	
	public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
	
	private static final Gson gson = GsonUtil.gsonWithAdapters();
	
	public ResurrectionBlockEntity(BlockPos pos, BlockState state) {
		super(graveyardBlocksFeature.BLOCK_ENTITY_TYPES.get("resurrection_block"), pos, state);
	}
	
	public ResurrectionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public static void tick(World world, BlockPos pos, BlockState state, ResurrectionBlockEntity blockEntity) {
		if( null == world.getServer() || world.isClient() ) {
			return;
		}
		
		if( state.get(HALF) == DoubleBlockHalf.UPPER ) {
			// Tick upper block
			tickUpper( world, pos, state, blockEntity );
		} else {
			// Tick lower block
			tickLower( world, pos, state, blockEntity );
		} // if, else
	}
	
	private static void tickUpper( World world, BlockPos pos, BlockState state, ResurrectionBlockEntity blockEntity ) {
		MinecraftServer minecraftServer = world.getServer();
		ServerWorld serverWorld = minecraftServer.getWorld( world.getRegistryKey() );
		
		resurrectionBlockTimer.tick();
		
		if( resurrectionBlockTimer.isAlarmActivated() ) {
			// # Sounds
			if( Graveyardsandghosts.CONFIG.enableSounds && Graveyardsandghosts.CONFIG.enableGraveyardBlockSounds ) {
				assert serverWorld != null;
				
				if( MathUtil.hasChance( 0.001F ) ) {
					world.playSound( null, pos, SoundEvents.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS.value(), SoundCategory.AMBIENT, Graveyardsandghosts.CONFIG.soundVolume, 1.0F );
				} // if
				
				world.playSound( null, pos, SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP.value(), SoundCategory.AMBIENT, Graveyardsandghosts.CONFIG.soundVolume, 1.0F );
			} // if
			resurrectionBlockTimer.resetAlarmActivation();
		} // if
		
		// # Particles
		ghostRespawnFeature.resurrectionCorpseTimers.forEach((UUID playerUUID, TickTimer corpseTimer ) -> {
			ServerPlayerEntity player = minecraftServer.getPlayerManager().getPlayer( playerUUID );
			
			if(corpseTimer.isAlarmActivated() ) {
				if( pos.isWithinDistance( player.getPos(), blockActivationDistance ) ) {
					if(Graveyardsandghosts.CONFIG.enableParticles && Graveyardsandghosts.CONFIG.enableGraveyardBlockParticles) {
						assert serverWorld != null;
						serverWorld.spawnParticles(player, ParticleTypes.HEART, true, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 3, 0.25, 0.25, 0.25, 0.005F);
					} // if
				} // if
			} // if
		});
	}
	
	private static void tickLower( World world, BlockPos pos, BlockState state, ResurrectionBlockEntity blockEntity ) {
		if( world.isClient() || world.getRegistryKey().toString().contains("null") ) {
			return;
		}
		
		int radius = Graveyardsandghosts.CONFIG.graveyardDiscoveryRadius;
		List<ServerPlayerEntity> playersWithinRange = world.getNonSpectatingEntities(ServerPlayerEntity.class, new Box(
				new Vec3d( pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius ),
				new Vec3d( pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius )
		));
		
		if( null != playersWithinRange ) {
			for (ServerPlayerEntity serverPlayerEntity : playersWithinRange) {
				GraveyardsandghostsPersistentPlayerData playerData = GraveyardsandghostsPersistentData.getPlayerState(serverPlayerEntity);
				
//				LogUtil.info("playerData.respawnPositions: " + playerData.respawnPositions);
				
				Map<String, Set<BlockPos>> playerRespawnPositions = gson.fromJson( playerData.respawnPositions, new TypeToken<Map<String, Set<BlockPos>>>(){}.getType());
				
				Set<BlockPos> dimensionPositions = Set.of();
				
				if( null == playerRespawnPositions) {
					playerRespawnPositions = new HashMap<>();
				} // if
				
				if( !playerRespawnPositions.containsKey( world.getRegistryKey().getValue().toString() ) ) {
					playerRespawnPositions.put( world.getRegistryKey().getValue().toString(), dimensionPositions );
				} // if
				
				dimensionPositions = new HashSet<>( playerRespawnPositions.get( world.getRegistryKey().getValue().toString() ) );
				
//				LogUtil.info("dimensionPositions: " + dimensionPositions);
				
				if( !dimensionPositions.contains( pos ) ) {
					dimensionPositions.add( pos );
					playerRespawnPositions.put( world.getRegistryKey().getValue().toString(), dimensionPositions );
					
					playerData.respawnPositions = gson.toJson(playerRespawnPositions, new TypeToken<Map<String, Set<BlockPos>>>(){}.getType());
					
//					LogUtil.info("Added respawn point: " + pos);
					
					serverPlayerEntity.incrementStat( ghostRespawnFeature.DISCOVERED_GRAVEYARDS_ID );
					
					MessageUtil.sendChatMessage( serverPlayerEntity, "Graveyard discovered! [" + pos.getX() + " " + pos.getY() + " " + pos.getZ() + "]");
				} // if
			} // for
		} // if
	}
	
}
