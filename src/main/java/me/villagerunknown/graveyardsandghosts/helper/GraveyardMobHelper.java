package me.villagerunknown.graveyardsandghosts.helper;

import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import me.villagerunknown.platform.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.List;
import java.util.Random;

public class GraveyardMobHelper {
	
	private static World WORLD = null;
	
	private static LivingEntity INSTIGATOR = null;
	
	private static List<MobEntity> OVERWORLD;
	
	private static List<MobEntity> NETHER;
	
	private static List<MobEntity> END;
	
	private static final Random rand = new Random();
	
	public GraveyardMobHelper(World world, LivingEntity instigator) {
		INSTIGATOR = instigator;
		
		init( world );
	}
	
	public GraveyardMobHelper(World world) {
		init( world );
	}
	
	private static void init( World world ) {
		WORLD = world;
		
		OVERWORLD = List.of(
				new BatEntity(EntityType.BAT, world),
				new SilverfishEntity(EntityType.SILVERFISH, world),
				new SpiderEntity(EntityType.SPIDER, world),
				new ZombieEntity(EntityType.ZOMBIE, world),
				new ZombieVillagerEntity(EntityType.ZOMBIE_VILLAGER, world),
				new SkeletonEntity(EntityType.SKELETON, world),
				new BoggedEntity(EntityType.BOGGED, world),
				new StrayEntity(EntityType.STRAY, world),
				new HuskEntity(EntityType.HUSK, world),
				new CaveSpiderEntity(EntityType.CAVE_SPIDER, world)
		);
		
		NETHER = List.of(
				new SkeletonEntity(EntityType.SKELETON, world),
				new ZombifiedPiglinEntity(EntityType.ZOMBIFIED_PIGLIN, world),
				new ZoglinEntity(EntityType.ZOGLIN, world),
				new WitherSkeletonEntity(EntityType.WITHER_SKELETON, world)
		);
		
		END = List.of(
				new EndermiteEntity(EntityType.ENDERMITE, world),
				new EndermanEntity(EntityType.ENDERMAN, world)
		);
	}
	
	public boolean spawnMobByDimension(BlockPos pos, boolean includePresets ) {
		Entity entity = getMobByDimension( pos, includePresets );
		return WORLD.spawnEntity( entity );
	}
	
	public MobEntity getMobByDimension( BlockPos pos, boolean includePresets ) {
		RegistryEntry<DimensionType> dimension = WORLD.getDimensionEntry();
		String dimensionStringID = dimension.getIdAsString();
		
		MobEntity mob;
		
		switch (dimensionStringID) {
			case "minecraft:the_end" -> mob = getMobForEnd();
			case "minecraft:the_nether" -> mob = getMobForNether();
			default -> mob = getMobForOverworld();
		};
		
		if( includePresets ) {
			return getMobWithPresets(mob, pos);
		} // if
		
		return getMob(mob, pos);
	}
	
	public MobEntity getMobForOverworld() {
		return (MobEntity) OVERWORLD.get( rand.nextInt( OVERWORLD.size() ) ).getType().create( WORLD );
	}
	
	public MobEntity getMobForNether() {
		return (MobEntity) NETHER.get( rand.nextInt( NETHER.size() ) ).getType().create( WORLD );
	}
	
	public MobEntity getMobForEnd() {
		return (MobEntity) END.get( rand.nextInt( END.size() ) ).getType().create( WORLD );
	}
	
	public boolean spawnMob(MobEntity mob, BlockPos pos, boolean includePresets ) {
		Entity entity;
		
		if( includePresets ) {
			entity = getMobWithPresets(mob, pos);
		} else {
			entity = mob;
		} // if, else
		
		return WORLD.spawnEntity( entity );
	}
	
	public MobEntity getMob(MobEntity mob, BlockPos pos ) {
		mob.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		mob.setHealth((float) (mob.getMaxHealth() * 0.5));
		mob.setDespawnCounter((int) ((Graveyardsandghosts.CONFIG.graveyardBlockMobSpawnDelayInMinutes * 60 * 20) * 0.5));
		
		if(null != INSTIGATOR) {
			mob.setTarget(INSTIGATOR);
		} // if
		
		return mob;
	}
	
	public MobEntity getMobWithPresets(MobEntity mob, BlockPos pos ) {
		mob = getMob( mob, pos );
		
		switch( mob.getType().toString().replace("entity.minecraft.","") ) {
			case "zombie":
			case "zombified_piglin":
				if(MathUtil.hasChance( 0.01F ) ) {
					mob.setBaby( true );
				} // if
				break;
			case "skeleton":
			case "stray":
			case "bogged":
				mob = addPresetBasicBow( mob );
				break;
			case "vex":
			case "wither_skeleton":
				mob = addPresetBasicSword( mob );
				break;
		}
		
		return mob;
	}
	
	private MobEntity addPresetBasicBow( MobEntity mob ) {
		Hand hand = mob.getActiveHand();
		ItemStack itemStack = new ItemStack( Items.BOW, 1 );
		mob.setStackInHand( hand, itemStack );
		return mob;
	}
	
	private MobEntity addPresetBasicSword( MobEntity mob ) {
		Hand hand = mob.getActiveHand();
		ItemStack itemStack = new ItemStack( Items.STONE_SWORD, 1 );
		mob.setStackInHand( hand, itemStack );
		return mob;
	}
	
}
