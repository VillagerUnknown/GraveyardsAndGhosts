package me.villagerunknown.graveyardsandghosts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static me.villagerunknown.graveyardsandghosts.Graveyardsandghosts.MOD_ID;

public class GraveyardsandghostsPersistentData extends PersistentState {
	
	public HashMap<UUID, GraveyardsandghostsPersistentPlayerData> players = new HashMap<>();
	
//	public Integer totalDirtBlocksBroken = 0;
	
	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
//		nbt.putInt("totalDirtBlocksBroken", totalDirtBlocksBroken);
		
		NbtCompound playersNbt = new NbtCompound();
		players.forEach((uuid, playerData) -> {
			NbtCompound playerNbt = new NbtCompound();
			
			playerNbt.putString("lastOverworldPos", playerData.lastOverworldPos);
			playerNbt.putString("lastCorpsePos", playerData.lastCorpsePos);
			playerNbt.putString("respawnPositions", playerData.respawnPositions);
			
			playersNbt.put(uuid.toString(), playerNbt);
		});
		nbt.put("players", playersNbt);
		
		return nbt;
	}
	
	public static GraveyardsandghostsPersistentData createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		GraveyardsandghostsPersistentData state = new GraveyardsandghostsPersistentData();
		
//		state.totalDirtBlocksBroken = tag.getInt("totalDirtBlocksBroken");
		
		NbtCompound playersNbt = tag.getCompound("players");
		playersNbt.getKeys().forEach(key -> {
			GraveyardsandghostsPersistentPlayerData playerData = new GraveyardsandghostsPersistentPlayerData();
			
			playerData.lastOverworldPos = playersNbt.getCompound(key).getString("lastOverworldPos");
			playerData.lastCorpsePos = playersNbt.getCompound(key).getString("lastCorpsePos");
			playerData.respawnPositions = playersNbt.getCompound(key).getString("respawnPositions");
			
			UUID uuid = UUID.fromString(key);
			state.players.put(uuid, playerData);
		});
		
		return state;
	}
	
	private static Type<GraveyardsandghostsPersistentData> type = new Type<>(
			GraveyardsandghostsPersistentData::new, // If there's no 'StateSaverAndLoader' yet create one
			GraveyardsandghostsPersistentData::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
			null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
	);
	
	public static GraveyardsandghostsPersistentData getServerState(MinecraftServer server) {
		// (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
		PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
		
		// The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
		// stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
		// 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
		GraveyardsandghostsPersistentData state = persistentStateManager.getOrCreate(type, MOD_ID);
		
		// If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
		// Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
		// of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
		// Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
		// there were no actual change to any of the mods state (INCREDIBLY RARE).
		state.markDirty();
		
		return state;
	}
	
	public static GraveyardsandghostsPersistentPlayerData getPlayerState(LivingEntity player) {
		GraveyardsandghostsPersistentData serverState = getServerState(player.getWorld().getServer());
		
		// Either get the player by the uuid, or we don't have data for them yet, make a new player state
		GraveyardsandghostsPersistentPlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new GraveyardsandghostsPersistentPlayerData());
		
		return playerState;
	}
	
}
