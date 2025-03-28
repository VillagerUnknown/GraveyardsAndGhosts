package me.villagerunknown.graveyardsandghosts.feature;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import me.villagerunknown.graveyardsandghosts.GraveyardsandghostsPersistentData;
import me.villagerunknown.graveyardsandghosts.GraveyardsandghostsPersistentPlayerData;
import me.villagerunknown.graveyardsandghosts.statuseffect.GhostEffect;
import me.villagerunknown.platform.timer.TickTimer;
import me.villagerunknown.platform.util.EntityUtil;
import me.villagerunknown.platform.util.GsonUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;

import java.util.*;

import static me.villagerunknown.graveyardsandghosts.Graveyardsandghosts.MOD_ID;

public class playerGhostFeature {
	
	public static List<Block> allowedGhostAttackBlocks = List.of(
			Blocks.TORCH,
			Blocks.WALL_TORCH,
			Blocks.SOUL_TORCH,
			Blocks.SOUL_WALL_TORCH
	);
	
	public static List<Block> allowedGhostInteractionBlocks = List.of(
			Blocks.RESPAWN_ANCHOR,
			// Doors
			Blocks.ACACIA_DOOR,
			Blocks.DARK_OAK_DOOR,
			Blocks.BAMBOO_DOOR,
			Blocks.BIRCH_DOOR,
			Blocks.CHERRY_DOOR,
			Blocks.COPPER_DOOR,
			Blocks.CRIMSON_DOOR,
			Blocks.EXPOSED_COPPER_DOOR,
			Blocks.IRON_DOOR,
			Blocks.JUNGLE_DOOR,
			Blocks.MANGROVE_DOOR,
			Blocks.OAK_DOOR,
			Blocks.OXIDIZED_COPPER_DOOR,
			Blocks.SPRUCE_DOOR,
			Blocks.WAXED_COPPER_DOOR,
			Blocks.WAXED_EXPOSED_COPPER_DOOR,
			Blocks.WAXED_OXIDIZED_COPPER_DOOR,
			Blocks.WEATHERED_COPPER_DOOR,
			// Trapdoors
			Blocks.ACACIA_TRAPDOOR,
			Blocks.BAMBOO_TRAPDOOR,
			Blocks.BIRCH_TRAPDOOR,
			Blocks.CHERRY_TRAPDOOR,
			Blocks.COPPER_TRAPDOOR,
			Blocks.ACACIA_TRAPDOOR,
			Blocks.CRIMSON_TRAPDOOR,
			Blocks.DARK_OAK_TRAPDOOR,
			Blocks.EXPOSED_COPPER_TRAPDOOR,
			Blocks.IRON_TRAPDOOR,
			Blocks.JUNGLE_TRAPDOOR,
			Blocks.MANGROVE_TRAPDOOR,
			Blocks.OAK_TRAPDOOR,
			Blocks.OXIDIZED_COPPER_TRAPDOOR,
			Blocks.SPRUCE_TRAPDOOR,
			Blocks.WARPED_TRAPDOOR,
			Blocks.WAXED_COPPER_TRAPDOOR,
			Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR,
			Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR,
			Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR,
			Blocks.WEATHERED_COPPER_TRAPDOOR,
			// Gates
			Blocks.ACACIA_FENCE_GATE,
			Blocks.BAMBOO_FENCE_GATE,
			Blocks.BIRCH_FENCE_GATE,
			Blocks.CHERRY_FENCE_GATE,
			Blocks.CRIMSON_FENCE_GATE,
			Blocks.DARK_OAK_FENCE_GATE,
			Blocks.JUNGLE_FENCE_GATE,
			Blocks.MANGROVE_FENCE_GATE,
			Blocks.OAK_FENCE_GATE,
			Blocks.SPRUCE_FENCE_GATE,
			Blocks.WARPED_FENCE_GATE,
			// Buttons
			Blocks.ACACIA_BUTTON,
			Blocks.BAMBOO_BUTTON,
			Blocks.BIRCH_BUTTON,
			Blocks.CHERRY_BUTTON,
			Blocks.CRIMSON_BUTTON,
			Blocks.DARK_OAK_BUTTON,
			Blocks.JUNGLE_BUTTON,
			Blocks.MANGROVE_BUTTON,
			Blocks.OAK_BUTTON,
			Blocks.POLISHED_BLACKSTONE_BUTTON,
			Blocks.SPRUCE_BUTTON,
			Blocks.STONE_BUTTON,
			Blocks.WARPED_BUTTON,
			// Candles
			Blocks.CANDLE,
			Blocks.RED_CANDLE,
			Blocks.GREEN_CANDLE,
			Blocks.BLUE_CANDLE,
			Blocks.ORANGE_CANDLE,
			Blocks.CYAN_CANDLE,
			Blocks.PURPLE_CANDLE,
			Blocks.PINK_CANDLE,
			Blocks.BROWN_CANDLE,
			Blocks.LIGHT_BLUE_CANDLE,
			Blocks.LIGHT_GRAY_CANDLE,
			Blocks.GRAY_CANDLE,
			Blocks.BLACK_CANDLE,
			Blocks.LIME_CANDLE,
			Blocks.MAGENTA_CANDLE,
			Blocks.YELLOW_CANDLE,
			Blocks.WHITE_CANDLE,
			// Misc.
			Blocks.LEVER,
			Blocks.NOTE_BLOCK,
			Blocks.LECTERN,
			Blocks.BELL,
			Blocks.CHISELED_BOOKSHELF
	);
	
	public static List<EntityType> allowedGhostInteractionEntities = List.of(
			EntityType.PLAYER,
			EntityType.HORSE,
			EntityType.SKELETON_HORSE,
			EntityType.ZOMBIE_HORSE,
			EntityType.DONKEY,
			EntityType.MULE,
			EntityType.LLAMA,
			EntityType.TRADER_LLAMA,
			EntityType.PIG,
			EntityType.STRIDER,
			EntityType.CAMEL,
			EntityType.MINECART,
			
			EntityType.ACACIA_BOAT,
			EntityType.JUNGLE_BOAT,
			EntityType.BIRCH_BOAT,
			EntityType.CHERRY_BOAT,
			EntityType.DARK_OAK_BOAT,
			EntityType.JUNGLE_BOAT,
			EntityType.MANGROVE_BOAT,
			EntityType.OAK_BOAT,
			
			EntityType.ACACIA_CHEST_BOAT,
			EntityType.JUNGLE_CHEST_BOAT,
			EntityType.BIRCH_CHEST_BOAT,
			EntityType.CHERRY_CHEST_BOAT,
			EntityType.DARK_OAK_CHEST_BOAT,
			EntityType.JUNGLE_CHEST_BOAT,
			EntityType.MANGROVE_CHEST_BOAT,
			EntityType.OAK_CHEST_BOAT
	);
	
	public static List<Item> allowedGhostUseItems = List.of(
			Items.ENDER_PEARL,
			Items.FISHING_ROD
	);
	
	public static List<EntityType> allowedGhostAttackEntities = List.of(
			EntityType.PLAYER
	);
	
	public static List<Item> allowedGhostPickupItems = List.of(
			Items.ENDER_PEARL
	);
	
	public static final StatusEffect GHOST_EFFECT = new GhostEffect();
	public static RegistryEntry<StatusEffect> GHOST_EFFECT_REGISTRY;
	
	public static Potion GHOST_POTION;
	public static RegistryEntry<Potion> GHOST_POTION_REGISTRY;
	
	private static final Gson gson = GsonUtil.gsonWithAdapters();
	
	private static TickTimer soundLoopTimer = new TickTimer(0, 30);
	private static TickTimer soundAdditionsTimer = new TickTimer(0, 30);
	
	public static void execute() {
		init();
		
		// Tick for Ghosts
		tickGhostEffect();
		
		// Prevent interactions as a g-g-g-ghost!
		preventPlayerInteractions();
	}
	
	private static void init() {
		// Add custom Status Effects
		registerStatusEffects();
		
		// Add custom Potions
		registerPotions();
	}
	
	private static void registerStatusEffects() {
		registerGhostStatusEffect();
	}
	
	private static void registerGhostStatusEffect() {
		Identifier ghostEffectId = Identifier.of(MOD_ID, "ghost");
		Registry.register(Registries.STATUS_EFFECT, ghostEffectId, GHOST_EFFECT);
		GHOST_EFFECT_REGISTRY = Registries.STATUS_EFFECT.getEntry(GHOST_EFFECT);
	}
	
	private static void registerPotions() {
		registerGhostPotion();
	}
	
	private static void registerGhostPotion() {
		Potion potion = new Potion(
				Text.translatable( "item.minecraft.potion.effect.ghost" ).toString(),
				new StatusEffectInstance(
						Registries.STATUS_EFFECT.getEntry(GHOST_EFFECT),
						-1,
						0,
						false,
						false,
						true
				)
		);
		GHOST_POTION = Registry.register( Registries.POTION, Identifier.of(MOD_ID, "ghost"), potion );
		GHOST_POTION_REGISTRY = Registries.POTION.getEntry(GHOST_POTION);
		
		FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
			builder.registerPotionRecipe(
					Potions.AWKWARD,
					Items.TOTEM_OF_UNDYING,
					GHOST_POTION_REGISTRY
			);
		});
	}
	
	public static void applyGhostEffect( ServerPlayerEntity serverPlayerEntity ) {
		if(serverPlayerEntity.getWorld().isClient()) {
			return;
		}
		
		EntityUtil.addStatusEffect( serverPlayerEntity, GHOST_EFFECT_REGISTRY, -1, 0, false, false, true );
	}
	
	public static void clearGhostEffect( ServerPlayerEntity serverPlayerEntity ) {
		if(serverPlayerEntity.getWorld().isClient()) {
			return;
		}
		
		EntityUtil.removeStatusEffect( serverPlayerEntity, GHOST_EFFECT_REGISTRY );
		serverPlayerEntity.setMovementSpeed(0F);
	}
	
	public static void applyGhostAbilities(LivingEntity entity, int amplifier) {
		if(entity.getWorld().isClient()) {
			return;
		}
		
		PlayerAbilities abilities = ((ServerPlayerEntity) entity).getAbilities();
		abilities.allowFlying = true;
		abilities.flying = true;
		((ServerPlayerEntity) entity).sendAbilitiesUpdate();
		
		HungerManager hungerManager = ((ServerPlayerEntity) entity).getHungerManager();
		if( hungerManager.isNotFull() ) {
			hungerManager.add(1, 1);
		}
		
		entity.setHealth( entity.getMaxHealth() );
		entity.setOnFire(false);
		entity.setInvisible(true);
		entity.setInvulnerable(true);
	}
	
	public static void applyHumanAbilities(LivingEntity entity) {
		if(entity.getWorld().isClient()) {
			return;
		}
		
		PlayerAbilities abilities = ((ServerPlayerEntity) entity).getAbilities();
		abilities.allowFlying = false;
		abilities.flying = false;
		((ServerPlayerEntity) entity).sendAbilitiesUpdate();
		
		if( !entity.hasStatusEffect( StatusEffects.INVISIBILITY ) ) {
			entity.setInvisible(false);
		} // if
		
		entity.setHealth( entity.getMaxHealth() );
		entity.setAir( entity.getMaxAir() );
		entity.setOnFire(false);
		entity.setInvulnerable(false);
	}
	
	private static void tickGhostEffect() {
		ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
			if( serverWorld.isClient() ) {
				return;
			} // if
			
			soundLoopTimer.tick();
			soundAdditionsTimer.tick();
			
			for (ServerPlayerEntity player : serverWorld.getPlayers()) {
				if( player.hasStatusEffect( GHOST_EFFECT_REGISTRY ) ) {
					GraveyardsandghostsPersistentPlayerData playerData = GraveyardsandghostsPersistentData.getPlayerState(player);
					Optional<GlobalPos> lastDeathPos = gson.fromJson( playerData.lastCorpsePos, new TypeToken<Optional<GlobalPos>>(){}.getType() );
					
					if( soundLoopTimer.isAlarmActivated() ) {
						player.playSoundToPlayer(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP.value(), SoundCategory.AMBIENT, Graveyardsandghosts.CONFIG.soundVolume, 1F);
						
						soundLoopTimer.resetAlarmActivation();
					} // if
					
					if( soundAdditionsTimer.isAlarmActivated() ) {
						player.playSoundToPlayer(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS.value(), SoundCategory.AMBIENT, Graveyardsandghosts.CONFIG.soundVolume, 1F);
						
						soundAdditionsTimer.resetAlarmActivation();
					} // if
					
					// Particles for all Ghosts to see
					if( null == lastDeathPos || !lastDeathPos.isPresent() ) {
						playerData.lastCorpsePos = gson.toJson( player.getLastDeathPos(), new TypeToken<Optional<GlobalPos>>(){}.getType() );
					} // if
					
					List<ServerPlayerEntity> players = serverWorld.getServer().getPlayerManager().getPlayerList();
					for (ServerPlayerEntity serverPlayerEntity : players) {
						if( serverPlayerEntity.hasStatusEffect( GHOST_EFFECT_REGISTRY ) ) {
							if( Graveyardsandghosts.CONFIG.enableParticles ) {
								EntityUtil.spawnParticles(serverPlayerEntity, 1, ParticleTypes.SOUL, 1, 0.025, 0.025, 0.025, 0.0005);
							} // if
						} // if
					} // for
					
				} // if
			} // for
		});
	}
	
	public static void preventPlayerInteractions() {
		// # Prevent the player from left-clicking on blocks
		AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
			if( playerEntity.isSpectator() ) {
				return ActionResult.FAIL;
			} // if
			
			if( Graveyardsandghosts.CONFIG.preventGhostBlockAttack ) {
				if( playerEntity.hasStatusEffect(GHOST_EFFECT_REGISTRY) ) {
					Block block = world.getBlockState(blockPos).getBlock();
					
					if( allowedGhostAttackBlocks.contains(block) ) {
						return ActionResult.PASS;
					} // if
					return ActionResult.FAIL;
				} // if
			} // if
			
			return ActionResult.PASS;
		});
		
		// # Prevent the player from right-clicking blocks
		UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
			if( playerEntity.isSpectator() ) {
				return ActionResult.FAIL;
			} // if
			
			if( Graveyardsandghosts.CONFIG.preventGhostBlockUse && playerEntity.hasStatusEffect(GHOST_EFFECT_REGISTRY) ) {
				Block block = world.getBlockState(blockHitResult.getBlockPos()).getBlock();
				BlockEntity blockEntity = world.getBlockEntity(blockHitResult.getBlockPos());
				
				boolean allowedInteraction = (
							(
								null != blockEntity
								&& blockEntity.getType().equals( graveyardBlocksFeature.BLOCK_ENTITY_TYPES.get("resurrection_block") )
							)
							|| allowedGhostInteractionBlocks.contains(block)
						);
				
				if( !allowedInteraction ) {
					return ActionResult.FAIL;
				} // if
			} // if
			
			return ActionResult.PASS;
		});
		
		// # Prevent the player from interacting with entities
		UseEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
			if( playerEntity.isSpectator() ) {
				return ActionResult.FAIL;
			} // if
			
			if( Graveyardsandghosts.CONFIG.preventGhostEntityInteraction ) {
				if( playerEntity.hasStatusEffect(GHOST_EFFECT_REGISTRY) ) {
					if( allowedGhostInteractionEntities.contains(entity.getType()) ) {
						return ActionResult.PASS;
					} // if
					return ActionResult.FAIL;
				} // if
			} // if
			
			return ActionResult.PASS;
		});
		
		// # Prevent the player from using items
		UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
			if( playerEntity.isSpectator() ) {
				return ActionResult.PASS;
			} // if
			
			if( Graveyardsandghosts.CONFIG.preventGhostItemUse ) {
				ItemStack handStack = playerEntity.getMainHandStack();
				if( handStack.isEmpty() ) {
					handStack = playerEntity.getOffHandStack();
				} // if
				
				if(handStack.isEmpty() ) {
					return ActionResult.PASS;
				} // if
				
				if( playerEntity.hasStatusEffect(GHOST_EFFECT_REGISTRY) ) {
					if( allowedGhostUseItems.contains( handStack.getItem() ) ) {
						return ActionResult.PASS;
					} // if
					return ActionResult.PASS;
				} // if
			} // if
			return ActionResult.PASS;
		});
		
		// # Prevent the player from breaking blocks
		PlayerBlockBreakEvents.BEFORE.register((world, playerEntity, blockPos, blockState, blockEntity) -> {
			if( playerEntity.isSpectator() ) {
				return false;
			} // if
			
			if( Graveyardsandghosts.CONFIG.preventGhostBlockBreak ) {
				if( playerEntity.hasStatusEffect(GHOST_EFFECT_REGISTRY) ) {
					return allowedGhostAttackBlocks.contains( blockState.getBlock() );
				} // if
			} // if
			return true;
		});
		
		// # Prevent the player from attacking entities
		AttackEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
			if( playerEntity.isSpectator() ) {
				return ActionResult.FAIL;
			} // if
			
			if( Graveyardsandghosts.CONFIG.preventGhostEntityAttack ) {
				if( playerEntity.hasStatusEffect(GHOST_EFFECT_REGISTRY) ) {
					if( allowedGhostAttackEntities.contains(entity.getType()) ) {
						return ActionResult.PASS;
					} // if
					return ActionResult.FAIL;
				} // if
			} // if
			return ActionResult.PASS;
		});
		
		// # Prevent mobs from tracking the player
		EntityTrackingEvents.START_TRACKING.register((entity, serverPlayerEntity) -> {
			if(entity instanceof MobEntity mobEntity) {
				if( null != mobEntity && serverPlayerEntity.hasStatusEffect( GHOST_EFFECT_REGISTRY ) && !allowedGhostAttackEntities.contains( entity.getType() ) ) {
					mobEntity.setTarget(null);
				} // if
			} // if
		});
	}
	
}
