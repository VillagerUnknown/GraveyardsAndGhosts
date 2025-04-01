package me.villagerunknown.graveyardsandghosts;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nimbusds.jose.shaded.gson.internal.bind.TypeAdapters;
import me.villagerunknown.platform.util.GsonUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Uuids;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static me.villagerunknown.graveyardsandghosts.Graveyardsandghosts.MOD_ID;

public class GraveyardsandghostsPersistentData extends PersistentState {
	
	public HashMap<UUID, GraveyardsandghostsPersistentPlayerData> players = new HashMap<>();
	
	private static final Codec<GraveyardsandghostsPersistentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(
					Uuids.CODEC,
					GraveyardsandghostsPersistentPlayerData.CODEC
			).fieldOf("players").forGetter( GraveyardsandghostsPersistentData::getPlayers )
	).apply( instance, GraveyardsandghostsPersistentData::new ));
	
//	private static final Codec<Map<UUID, GraveyardsandghostsPersistentPlayerData>> CODEC = Codec.unboundedMap(
//			Uuids.CODEC,
//			GraveyardsandghostsPersistentPlayerData.CODEC
//	);
	
//	public Integer totalDirtBlocksBroken = 0;
	
	GraveyardsandghostsPersistentData() {}
	
	public GraveyardsandghostsPersistentData(Map<UUID, GraveyardsandghostsPersistentPlayerData> playerDataMap) {
		players = new HashMap<>(playerDataMap);
	}
	
	public HashMap<UUID, GraveyardsandghostsPersistentPlayerData> getPlayers() {
		return players;
	}
	
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
		
		Optional<NbtCompound> playersNbt = tag.getCompound("players");
		
		playersNbt.ifPresent(nbtCompound -> nbtCompound.getKeys().forEach(key -> {
			GraveyardsandghostsPersistentPlayerData playerData = new GraveyardsandghostsPersistentPlayerData(
					nbtCompound.getCompound(key).flatMap(compound -> compound.getString("lastOverworldPos")).toString(),
					nbtCompound.getCompound(key).flatMap(compound -> compound.getString("lastCorpsePos")).toString(),
					nbtCompound.getCompound(key).flatMap(compound -> compound.getString("respawnPositions")).toString()
			);
			
			UUID uuid = UUID.fromString(key);
			state.players.put(uuid, playerData);
		}));
		
		return state;
	}
	
	//GraveyardsandghostsPersistentData::createFromNbt
	private static PersistentStateType<GraveyardsandghostsPersistentData> type = new PersistentStateType<>(
			MOD_ID,
			(context) -> new GraveyardsandghostsPersistentData(),
			ctx -> CODEC,
			null
	);
	
	public static GraveyardsandghostsPersistentData getServerState(MinecraftServer server) {
		// (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
		PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();
		
		// The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
		// stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
		// 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
		GraveyardsandghostsPersistentData state = persistentStateManager.getOrCreate(type);
		
		// If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
		// Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
		// of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
		// Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
		// there were no actual change to any of the mods state (INCREDIBLY RARE).
		state.markDirty();
		
		return state;
	}
	
	public static GraveyardsandghostsPersistentPlayerData getPlayerState(LivingEntity player) {
		World world = player.getWorld();
		
		if( null != world ) {
			MinecraftServer server = world.getServer();
			
			if( null != server ) {
				GraveyardsandghostsPersistentData serverState = getServerState(server);
				
				GraveyardsandghostsPersistentPlayerData playerData = new GraveyardsandghostsPersistentPlayerData();
				
				if( serverState.players.containsKey( player.getUuid() ) ) {
					return serverState.players.get( player.getUuid() );
				} // if
				
				serverState.players.put( player.getUuid(), playerData );
				
				// Either get the player by the uuid, or we don't have data for them yet, make a new player state
				return playerData;
//				return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new GraveyardsandghostsPersistentPlayerData());
			} // if
		} // if
		
		return new GraveyardsandghostsPersistentPlayerData();
	}
	
}
