package me.villagerunknown.graveyardsandghosts.block.entity;

import me.villagerunknown.graveyardsandghosts.Graveyardsandghosts;
import me.villagerunknown.graveyardsandghosts.block.coffin.CoffinBlock;
import me.villagerunknown.graveyardsandghosts.feature.graveyardBlocksFeature;
import me.villagerunknown.graveyardsandghosts.helper.GraveyardMobHelper;
import me.villagerunknown.platform.timer.TickTimer;
import me.villagerunknown.platform.util.BoxUtil;
import me.villagerunknown.platform.util.MathUtil;
import me.villagerunknown.platform.util.TimeUtil;
import me.villagerunknown.platform.util.WorldUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static me.villagerunknown.graveyardsandghosts.Graveyardsandghosts.MOD_ID;
import static me.villagerunknown.graveyardsandghosts.block.coffin.CoffinBlock.PART;

public class CoffinBlockEntity extends LootableContainerBlockEntity {
	
	public static final float LOOT_FILL_CHANCE = 0.5F;
	public static final int INVENTORY_SIZE = 54;
	
	private DefaultedList<ItemStack> inventory;
	
	public static Map<BlockPos, TickTimer> spawnTimers = new HashMap<>();
	
	public static final RegistryKey<LootTable> CUSTOM_LOOT_TABLE = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MOD_ID, "chests/coffin_block"));
	
	public CoffinBlockEntity(BlockPos pos, BlockState state) {
		super(graveyardBlocksFeature.BLOCK_ENTITY_TYPES.get("coffin_block"), pos, state);
		this.inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
		if( super.hasWorld() && WorldUtil.getEntitiesByType( WorldUtil.getServerWorld( super.getWorld() ), BoxUtil.createBox( pos, 1 ), ServerPlayerEntity.class ).isEmpty() ) {
			if (MathUtil.hasChance(LOOT_FILL_CHANCE)) {
				this.setLootTable(CUSTOM_LOOT_TABLE);
			} // if
		} // if
	}
	
	protected CoffinBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		this.inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
		if( super.hasWorld() && WorldUtil.getEntitiesByType( WorldUtil.getServerWorld( super.getWorld() ), BoxUtil.createBox( pos, 1 ), ServerPlayerEntity.class ).isEmpty() ) {
			if (MathUtil.hasChance(LOOT_FILL_CHANCE)) {
				this.setLootTable(CUSTOM_LOOT_TABLE);
			} // if
		} // if
	}
	
	// These Methods are from the NamedScreenHandlerFactory Interface
	// createMenu creates the ScreenHandler itself
	// `getDisplayName` will Provide its name which is normally shown at the top
	
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		// We provide *this* to the screenHandler as our class Implements Inventory
		// Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
		return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this);
	}
	
	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return createMenu( syncId, playerInventory, playerInventory.player );
	}
	
	@Override
	public Text getDisplayName() {
		return Text.translatable(getCachedState().getBlock().getTranslationKey());
	}
	
	@Override
	protected Text getContainerName() {
		return Text.translatable("container." + MOD_ID + ".coffin");
	}
	
	protected DefaultedList<ItemStack> getHeldStacks() {
		return this.inventory;
	}
	
	protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
		this.inventory = inventory;
	}
	
	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.readLootTable(nbt)) {
			Inventories.readNbt(nbt, this.inventory, registryLookup);
		}
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		if (!this.writeLootTable(nbt)) {
			Inventories.writeNbt(nbt, this.inventory, registryLookup);
		}
	}
	
	@Override
	public int size() {
		return INVENTORY_SIZE;
	}
	
	public static void tick(World world, BlockPos pos, BlockState state, CoffinBlockEntity blockEntity) {
		if( null == world.getServer() || world.isClient() || !world.shouldTickBlocksInChunk( world.getWorldChunk( pos ).getPos().toLong() ) ) {
			return;
		}
		
		if( null != spawnTimers ) {
			spawnTimers.forEach((BlockPos timerPos, TickTimer timer) -> {
				timer.tick();
			});
		} // if
		
		MinecraftServer minecraftServer = world.getServer();
		ServerWorld serverWorld = minecraftServer.getWorld(world.getRegistryKey());
		
		if (Graveyardsandghosts.CONFIG.enableSounds && Graveyardsandghosts.CONFIG.enableGraveyardBlockSounds && state.get( CoffinBlock.MODEL ).equals(0) ) {
			float chance = Graveyardsandghosts.CONFIG.graveyardBlockMobSoundChance;
			
			if( TimeUtil.isNightTime( world ) ) {
				chance = chance * 2;
			} // if
			
			GraveyardMobHelper MobHelper = new GraveyardMobHelper(serverWorld);
			
			if( MathUtil.hasChance( chance ) ) {
				MobEntity mob = MobHelper.getMobByDimension( pos, false );
				mob.playAmbientSound();
			} // if
		} // if
		
		if( state.get(PART) == BedPart.FOOT ) {
			BlockState newState;
			
			int totalItems = 0;
			for (ItemStack itemStack : blockEntity.inventory) {
				totalItems += itemStack.getCount();
				if( totalItems > 0 ) {
					break;
				} // if
			} // for
			
			if( 0 == totalItems && null == blockEntity.getLootTable() ) {
				// Set coffin model to open
				if( state.get( CoffinBlock.MODEL ).equals(0) ) {
					world.playSound( null, pos, SoundEvents.BLOCK_BARREL_OPEN, SoundCategory.AMBIENT, Graveyardsandghosts.CONFIG.soundVolume, 1.0F );
				} // if
				newState = state.with( CoffinBlock.MODEL, 1 );
			} else {
				// Set coffin model to closed
				if( state.get( CoffinBlock.MODEL ).equals(1) ) {
					world.playSound( null, pos, SoundEvents.BLOCK_BARREL_CLOSE, SoundCategory.AMBIENT, Graveyardsandghosts.CONFIG.soundVolume, 1.0F );
				} // if
				newState = state.with( CoffinBlock.MODEL, 0 );
			} // if, else
			
			world.setBlockState( pos, newState );
		} // if, else
	}
	
}
