package me.villagerunknown.graveyardsandghosts.feature;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import me.villagerunknown.graveyardsandghosts.GraveyardsandghostsPersistentData;
import me.villagerunknown.graveyardsandghosts.GraveyardsandghostsPersistentPlayerData;
import me.villagerunknown.graveyardsandghosts.block.resurrection.ResurrectionBlock;
import me.villagerunknown.graveyardsandghosts.network.ConfirmResurrectionPayload;
import me.villagerunknown.graveyardsandghosts.network.DeclineResurrectionPayload;
import me.villagerunknown.platform.timer.ServerTickTimer;
import me.villagerunknown.platform.timer.TickTimer;
import me.villagerunknown.platform.util.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.block.RespawnAnchorBlock.CHARGES;

public class ghostRespawnFeature {
	
	private static final int corpseTimerFrequencyInSeconds = 3;
	
	public static Map<UUID, ServerTickTimer> resurrectionCorpseTimers = new HashMap<>();
	public static Map<UUID, ServerTickTimer> resurrectionPromptTimers = new HashMap<>();
	public static Map<UUID, ServerTickTimer> resurrectionPromptDelayTimers = new HashMap<>();
	
	public static Map<BlockPos, RegistryKey<World>> respawnPositions = new HashMap<>();
	
	private static double closestRespawnDistance = Double.MAX_VALUE;
	private static BlockPos respawnPosition = null;
	
	private static final Gson gson = GsonUtil.gsonWithAdapters();
	
	public static final Identifier CONFIRM_RESURRECTION_PACKET_ID = Identifier.of( Graveyardsandghosts.MOD_ID, "confirm_resurrection" );
	public static final Identifier DECLINE_RESURRECTION_PACKET_ID = Identifier.of( Graveyardsandghosts.MOD_ID, "decline_resurrection" );
	
	public static final Identifier DISCOVERED_GRAVEYARDS_ID = RegistryUtil.registerStat( "discovered_graveyards", Graveyardsandghosts.MOD_ID, StatFormatter.DEFAULT );
	public static final Identifier TOTAL_RESURRECTIONS_ID = RegistryUtil.registerStat( "total_resurrections", Graveyardsandghosts.MOD_ID, StatFormatter.DEFAULT );
	
	public static void execute() {
		init();
		
		// # Track last known Overworld locations for death in other dimensions without a known respawn point
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, serverWorld) -> {
			if( entity.isPlayer() ) {
				String dimension = serverWorld.getDimensionEntry().getIdAsString();
				if("minecraft:overworld".equals(dimension)) {
					// Player is leaving Overworld
					GraveyardsandghostsPersistentPlayerData playerData = GraveyardsandghostsPersistentData.getPlayerState((LivingEntity) entity);
					playerData.lastOverworldPos = gson.toJson( entity.getBlockPos(), BlockPos.class );
				} // if
			} // if
		});
		
		
		// # Set Resurrection Respawn point on Death
		ServerPlayerEvents.ALLOW_DEATH.register((serverPlayerEntity, damageSource, v) -> {
			if( serverPlayerEntity.hasStatusEffect( playerGhostFeature.GHOST_EFFECT_REGISTRY ) ) {
				return false;
			} // if
			
			if( Graveyardsandghosts.CONFIG.enableGraveyardRespawnPoints ) {
				GraveyardsandghostsPersistentPlayerData playerData = GraveyardsandghostsPersistentData.getPlayerState(serverPlayerEntity);
				Map<String, Set<BlockPos>> playerRespawnPositions = gson.fromJson( playerData.respawnPositions, new TypeToken<Map<String, Set<BlockPos>>>() {}.getType() );

				closestRespawnDistance = Double.MAX_VALUE;
				respawnPosition = null;

				if( null != playerRespawnPositions) {
					Set<BlockPos> dimensionPositions = playerRespawnPositions.getOrDefault( serverPlayerEntity.getServerWorld().getRegistryKey().getValue().toString(), new HashSet<>() );
					World dimWorld = serverPlayerEntity.getServer().getWorld( serverPlayerEntity.getServerWorld().getRegistryKey() );
					BlockPos lastPlayerPosition = serverPlayerEntity.getBlockPos();
					
					Graveyardsandghosts.LOGGER.info("Trying to set respawn point for " + serverPlayerEntity.getNameForScoreboard() + " in " + dimWorld.getDimensionEntry().getIdAsString() + " from " + dimensionPositions.size() + " possible points.");
					
					if( dimensionPositions.isEmpty() ) {
						dimensionPositions = playerRespawnPositions.getOrDefault( serverPlayerEntity.getServer().getOverworld().getRegistryKey().getValue().toString(), new HashSet<>() );
						dimWorld = serverPlayerEntity.getServer().getWorld( serverPlayerEntity.getServer().getOverworld().getRegistryKey() );
						lastPlayerPosition = gson.fromJson( playerData.lastOverworldPos, BlockPos.class );
						
						Graveyardsandghosts.LOGGER.info("Trying to set respawn point for " + serverPlayerEntity.getNameForScoreboard() + " in " + dimWorld.getDimensionEntry().getIdAsString() + " from " + dimensionPositions.size() + " possible points.");
						
						if( dimensionPositions.isEmpty() ) {
							Graveyardsandghosts.LOGGER.info("No respawn points. Using default mechanics.");
							return false;
						} // if
					} // if
					
					for (BlockPos pos : dimensionPositions) {
						BlockEntity blockentity = dimWorld.getBlockEntity(pos);
						BlockState blockstate = dimWorld.getBlockState(pos);
						
						// # Remove Position IF:
						// (Block Entity or Block State is null)
						// OR
						// (
						// 	(Not Resurrection Block Entity AND Not Respawn Anchor Block)
						// 	OR
						// 	(Respawn Anchor AND 0 Charges)
						// )
						if (
								(
									blockentity == null
									&& blockstate == null
								)
								|| (
									(
										(
											blockentity != null
											&& !blockentity.getType().equals(graveyardBlocksFeature.BLOCK_ENTITY_TYPES.get("resurrection_block"))
										)
										&& (
											blockstate != null
											&& blockstate.getBlock() != Blocks.RESPAWN_ANCHOR
										)
									)
									|| (
										blockstate != null
										&& blockstate.getBlock() == Blocks.RESPAWN_ANCHOR
										&& blockstate.get(CHARGES) <= 0
									)
								)
						) {
							Graveyardsandghosts.LOGGER.info("Removing respawn position that no longer exists in " + dimWorld.getDimensionEntry().getIdAsString() + ": " + pos);
							
							dimensionPositions.remove(pos);
							playerRespawnPositions.put(dimWorld.getRegistryKey().getValue().toString(), dimensionPositions);
							playerData.respawnPositions = gson.toJson(playerRespawnPositions, new TypeToken<Map<String, Set<BlockPos>>>(){}.getType());
							
							continue;
						} // if
						
						double distance = pos.getSquaredDistance(lastPlayerPosition);
						
						if (distance < closestRespawnDistance) {
							closestRespawnDistance = distance;
							respawnPosition = pos;
						} // if
					} // for
					
					Graveyardsandghosts.LOGGER.info("Respawn point set at: " + respawnPosition + " in " + dimWorld.getDimensionEntry().getIdAsString() + "; distance: " + closestRespawnDistance);
					
					if( null != respawnPosition ) {
						BlockPos safeSpawnPos = respawnPosition;
						BlockState blockState = dimWorld.getBlockState( respawnPosition );
						
						if( Graveyardsandghosts.CONFIG.enablePlayerGhostOnDeath ) {
							respawnPosition.up(3);
						} else {
							Direction facing = blockState.get( ResurrectionBlock.FACING );
							safeSpawnPos = respawnPosition.offset( facing );
						} // if, else
						
						if( !blockState.isAir() || !dimWorld.getBlockState( safeSpawnPos.up() ).isAir() ) {
							safeSpawnPos = PositionUtil.findSafeSpawnPosition( dimWorld, respawnPosition, Graveyardsandghosts.CONFIG.resurrectionSafeRespawnSearchRadius );
						} // if
						
						serverPlayerEntity.setSpawnPoint(dimWorld.getRegistryKey(), safeSpawnPos, serverPlayerEntity.getSpawnAngle(), true, false);
					} // if
				} // if
			} // if
			return true;
		});
		
		// # Convert a Player to a Ghost on Respawn
		ServerPlayerEvents.AFTER_RESPAWN.register((serverPlayerEntity, serverPlayerEntity1, b) -> {
			if( Graveyardsandghosts.CONFIG.enablePlayerGhostOnDeath && Graveyardsandghosts.CONFIG.enableGraveyardRespawnPoints && !serverPlayerEntity1.isCreative() && !serverPlayerEntity1.isSpectator() ) {
				MessageUtil.sendChatMessage( serverPlayerEntity1, Text.translatable("text.villagerunknown-graveyardsandghosts.respawn.respawned").getString() );
				MessageUtil.sendChatMessage( serverPlayerEntity1, Text.translatable("text.villagerunknown-graveyardsandghosts.respawn.instructions").getString() );
				
				playerGhostFeature.applyGhostEffect(serverPlayerEntity1);
				
				GraveyardsandghostsPersistentPlayerData playerData = GraveyardsandghostsPersistentData.getPlayerState(serverPlayerEntity1);
				playerData.lastCorpsePos = gson.toJson( serverPlayerEntity.getLastDeathPos(), new TypeToken<Optional<GlobalPos>>(){}.getType() );
				
				if( null != serverPlayerEntity.getServer() ) {
					long currentTick = serverPlayerEntity.getServer().getTicks();
					resurrectionCorpseTimers.put(serverPlayerEntity1.getUuid(), new ServerTickTimer(currentTick, 0, corpseTimerFrequencyInSeconds));
					resurrectionPromptTimers.put(serverPlayerEntity1.getUuid(), new ServerTickTimer(currentTick, 0, Graveyardsandghosts.CONFIG.resurrectionPromptFrequencyInSeconds));
				}
			} // if
		});
		
		// # Player joins the server
		ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
			ServerPlayerEntity player = serverPlayNetworkHandler.player;
			
			GraveyardsandghostsPersistentPlayerData playerData = GraveyardsandghostsPersistentData.getPlayerState(player);
			Map<String, Set<BlockPos>> playerRespawnPositions = gson.fromJson( playerData.respawnPositions, new TypeToken<Map<String, Set<BlockPos>>>() {}.getType() );
			BlockPos lastOverworldPos = gson.fromJson( playerData.lastOverworldPos, BlockPos.class );
			
			if( null == playerRespawnPositions ) {
				Graveyardsandghosts.LOGGER.info(player.getNameForScoreboard() + " has discovered 0 graveyards.");
			} else {
				AtomicInteger totalGraveyards = new AtomicInteger();
				
				playerRespawnPositions.forEach((String dimension, Set<BlockPos> graveyards) -> {
					totalGraveyards.addAndGet(graveyards.size());
				});
				
				Graveyardsandghosts.LOGGER.info(player.getNameForScoreboard() + " has discovered " + totalGraveyards + " graveyards.");
			} // if, else
			
			if( null == lastOverworldPos ) {
				Graveyardsandghosts.LOGGER.info(player.getNameForScoreboard() + " has not used a portal to leave the Overworld");
			} else {
				Graveyardsandghosts.LOGGER.info(player.getNameForScoreboard() + " last took a portal out of the Overworld at " + lastOverworldPos);
			} // if, else
			
			if( Graveyardsandghosts.CONFIG.playersStartWorldsAsGhosts && !player.isCreative() && !player.isSpectator() ) {
				if (PlayerStatUtil.getStatFromIdentifier(player, Stats.TOTAL_WORLD_TIME) <= 0) {
					if (!player.hasStatusEffect(playerGhostFeature.GHOST_EFFECT_REGISTRY)) {
						MessageUtil.sendChatMessage( player, Text.translatable( "text.villagerunknown-graveyardsandghosts.spawn.ghost" ).getString() );
						MessageUtil.sendChatMessage( player, Text.translatable( "text.villagerunknown-graveyardsandghosts.spawn.start" ).getString() );
						
						playerGhostFeature.applyGhostEffect(player);
					} // if
				} // if
			} // if
			
			if( player.hasStatusEffect( playerGhostFeature.GHOST_EFFECT_REGISTRY ) ) {
				if( !resurrectionCorpseTimers.containsKey( player.getUuid() ) ) {
					MessageUtil.sendChatMessage( player, Text.translatable( "text.villagerunknown-graveyardsandghosts.spawn.ghost" ).getString() );
					MessageUtil.sendChatMessage( player, Text.translatable( "text.villagerunknown-graveyardsandghosts.effect.remove" ).getString() );
					
					long currentTick = minecraftServer.getTicks();
					
					resurrectionCorpseTimers.put(player.getUuid(), new ServerTickTimer(currentTick, 0, corpseTimerFrequencyInSeconds));
					resurrectionPromptTimers.put(player.getUuid(), new ServerTickTimer(currentTick, 0, Graveyardsandghosts.CONFIG.resurrectionPromptFrequencyInSeconds));
					resurrectionPromptDelayTimers.put(player.getUuid(), new ServerTickTimer(currentTick, 0, Graveyardsandghosts.CONFIG.resurrectionPromptFrequencyInSeconds));
				} // if
			} // if
		});
		
		// # Player leaves the server
		ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, minecraftServer) -> {
			resurrectionCorpseTimers.remove(serverPlayNetworkHandler.player.getUuid());
			resurrectionPromptTimers.remove(serverPlayNetworkHandler.player.getUuid());
			resurrectionPromptDelayTimers.remove(serverPlayNetworkHandler.player.getUuid());
		});
		
		// # Ghost Ticks
		ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
			if( serverWorld.isClient() ) {
				return;
			}
			
			MinecraftServer minecraftServer = serverWorld.getServer();
			long currentTick = minecraftServer.getTicks();
			
			// # Players
			for (ServerPlayerEntity player : serverWorld.getServer().getPlayerManager().getPlayerList()) {
				if( player.hasStatusEffect( playerGhostFeature.GHOST_EFFECT_REGISTRY ) && player.isAlive() ) {
					ServerTickTimer corpseTimer = resurrectionCorpseTimers.get( player.getUuid() );
					ServerTickTimer promptTimer = resurrectionPromptTimers.get( player.getUuid() );
					
					if( null == corpseTimer ) {
						corpseTimer = new ServerTickTimer(currentTick, 0,corpseTimerFrequencyInSeconds);
					} else {
						corpseTimer.tick( currentTick );
					} // if, else
					
					if( !resurrectionPromptDelayTimers.containsKey( player.getUuid() ) ) {
						if( null == promptTimer ) {
							promptTimer = new ServerTickTimer(currentTick, 0,Graveyardsandghosts.CONFIG.resurrectionPromptFrequencyInSeconds);
						} else {
							promptTimer.tick( currentTick );
						} // if, else
					} else {
						ServerTickTimer promptDelayTimer = resurrectionPromptDelayTimers.get( player.getUuid() );
						promptDelayTimer.tick( currentTick );
						if( promptDelayTimer.isAlarmActivated() ) {
							resurrectionPromptDelayTimers.remove( player.getUuid() );
						} // if
					} // if, else
					
					// Allow resurrection at last death location
					GraveyardsandghostsPersistentPlayerData playerData = GraveyardsandghostsPersistentData.getPlayerState(player);

					Optional<GlobalPos> lastDeathPos = gson.fromJson( playerData.lastCorpsePos, new TypeToken<Optional<GlobalPos>>(){}.getType() );
					
					if( null == lastDeathPos || lastDeathPos.isEmpty() ) {
						lastDeathPos = player.getLastDeathPos();
					} // if
					
					if (null != lastDeathPos && lastDeathPos.isPresent()) {
						if(Objects.equals(lastDeathPos.get().dimension().toString(), serverWorld.getRegistryKey().toString())) {
							// # Player is in the same Dimension as their Last Death
							BlockPos safeSpawnPos = lastDeathPos.get().pos();
							
							// Set respawn position to ground if in air
							if( !PositionUtil.hasSafeBlockBelow( serverWorld, lastDeathPos.get().pos() ) ) {
								ServerWorld lastDeathDimension = serverWorld.getServer().getWorld(lastDeathPos.get().dimension());
								
								if( null == lastDeathDimension ) {
									lastDeathDimension = serverWorld;
								} // if
								
								if( null != lastDeathDimension ) {
									// Set to lowest block
									safeSpawnPos = PositionUtil.findSafeBlockBelow(lastDeathDimension, lastDeathPos.get().pos(), lastDeathDimension.getBottomY());
									
									if( !PositionUtil.hasSafeBlockBelow( serverWorld, safeSpawnPos ) ) {
										// Set to nearby block
										safeSpawnPos = PositionUtil.findSafeSpawnPosition(lastDeathDimension, lastDeathPos.get().pos(), Graveyardsandghosts.CONFIG.resurrectionSafeRespawnSearchRadius);
									} // if
								} // if
							} // if, else
							
							if (player.squaredDistanceTo(safeSpawnPos.getX(), safeSpawnPos.getY(), safeSpawnPos.getZ()) < Graveyardsandghosts.CONFIG.resurrectionPromptRangeInBlocks) {
								// Show prompt asking player if they'd like to respawn
								if (null != promptTimer && promptTimer.isAlarmActivated()) {
									if( Graveyardsandghosts.CONFIG.resurrectionRequiresSolidBlockBelow && Graveyardsandghosts.CONFIG.preventGhostCollisions ) {
										if( PositionUtil.hasSafeBlockBelow( serverWorld, player.getBlockPos() ) ) {
											promptPlayerResurrection(player);
										}  else {
											MessageUtil.showActionBarMessage( player, Text.translatable( "text.villagerunknown-graveyardsandghosts.effect.solid" ).getString() );
										} // if, else
									} else {
										promptPlayerResurrection(player);
									} // if, else
									
									promptTimer.resetAlarmActivation( currentTick );
								} // if
							} // if
							
							if (corpseTimer.isAlarmActivated()) {
								if (Graveyardsandghosts.CONFIG.enableParticles) {
									serverWorld.spawnParticles(player, ParticleTypes.SOUL, true, safeSpawnPos.getX() + 0.5, safeSpawnPos.getY() + 1.5, safeSpawnPos.getZ() + 0.5, 1, 0.25, 0.25, 0.25, 0.005F);
									serverWorld.spawnParticles(player, ParticleTypes.HEART, true, safeSpawnPos.getX() + 0.5, safeSpawnPos.getY() + 2.5, safeSpawnPos.getZ() + 0.5, 3, 0.25, 0.25, 0.25, 1F);
								} // if
								
								corpseTimer.resetAlarmActivation( currentTick );
							} // if
							
						} // if
					} // if
				} // if
			} // for
		});
		
		// # Show resurrection prompt on Resurrection block use
		UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
			if(playerEntity.isSpectator() || world.isClient()) {
				return ActionResult.PASS;
			}
			
			if( playerEntity.hasStatusEffect( playerGhostFeature.GHOST_EFFECT_REGISTRY ) ) {
				Block block = world.getBlockState( blockHitResult.getBlockPos() ).getBlock();
				BlockEntity blockEntity = world.getBlockEntity( blockHitResult.getBlockPos() );
				
				// Offer Resurrection
				if(
					(
						null != blockEntity
						&& blockEntity.getType().equals( graveyardBlocksFeature.BLOCK_ENTITY_TYPES.get("resurrection_block") )
					)
					|| block.equals( Blocks.RESPAWN_ANCHOR )
				) {
					ServerPlayerEntity serverPlayerEntity = world.getServer().getPlayerManager().getPlayer( playerEntity.getUuid() );
					if( null != serverPlayerEntity ) {
						if( Graveyardsandghosts.CONFIG.resurrectionRequiresSolidBlockBelow && Graveyardsandghosts.CONFIG.preventGhostCollisions ) {
							if( PositionUtil.hasSafeBlockBelow( world, serverPlayerEntity.getBlockPos() ) ) {
								promptPlayerResurrection(serverPlayerEntity);
							} else {
								MessageUtil.showActionBarMessage( serverPlayerEntity, Text.translatable( "text.villagerunknown-graveyardsandghosts.effect.solid" ).getString() );
							}
						} else {
							promptPlayerResurrection(serverPlayerEntity);
						} // if, else
						
						return ActionResult.FAIL;
					} // if
				} // if
			} // if
			
			return ActionResult.PASS;
		});
		
	}
	
	private static void init() {
		// # Register Network Payloads
		// Server 2 Client
		PayloadTypeRegistry.playS2C().register(ConfirmResurrectionPayload.ID, ConfirmResurrectionPayload.CODEC);
		// Client 2 Server
		PayloadTypeRegistry.playC2S().register(ConfirmResurrectionPayload.ID, ConfirmResurrectionPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(DeclineResurrectionPayload.ID, DeclineResurrectionPayload.CODEC);
		
		// Register Confirm Resurrection Payload Receiver
		ServerPlayNetworking.registerGlobalReceiver(ConfirmResurrectionPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				resurrectPlayer(context.player());
			});
		});
		
		// Register Decline Resurrection Payload Receiver
		ServerPlayNetworking.registerGlobalReceiver(DeclineResurrectionPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				resurrectionPromptDelayTimers.put( context.player().getUuid(), new ServerTickTimer( context.server().getTicks(), 0, Graveyardsandghosts.CONFIG.resurrectionPromptFrequencyInSeconds ) );
			});
		});
	}
	
	public static void resurrectPlayer(ServerPlayerEntity player) {
		if( Graveyardsandghosts.CONFIG.enableSounds ) {
			EntityUtil.playSound(player, SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.AMBIENT, Graveyardsandghosts.CONFIG.soundVolume, 1.0F, true);
			EntityUtil.playSound(player, SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.value(), SoundCategory.AMBIENT, Graveyardsandghosts.CONFIG.soundVolume, 1.0F, true);
		} // if
		
		if( Graveyardsandghosts.CONFIG.enableParticles ) {
			if( Graveyardsandghosts.CONFIG.enableFlashParticles ) {
				EntityUtil.spawnParticles( player, 1, ParticleTypes.FLASH, 1, 0, 0, 0, 1F );
			} // if
			
			EntityUtil.spawnParticles( player, 1, ParticleTypes.TOTEM_OF_UNDYING, 10, 0.25, 0.25, 0.25, 0.005F );
			EntityUtil.spawnParticles( player, 1, ParticleTypes.SOUL, 1, 0.25, 0.25, 0.25, 0.005F );
		} // if
		
		player.incrementStat( ghostRespawnFeature.TOTAL_RESURRECTIONS_ID );
		
		playerGhostFeature.clearGhostEffect( player );
		playerGhostFeature.applyHumanAbilities( player );
		
		resurrectionCorpseTimers.remove( player.getUuid() );
		resurrectionPromptTimers.remove( player.getUuid() );
	}
	
	public static void promptPlayerResurrection( ServerPlayerEntity serverPlayerEntity ) {
		ServerPlayNetworking.send(serverPlayerEntity, new ConfirmResurrectionPayload());
	}
	
}
