package me.villagerunknown.graveyardsandghosts.data.persistent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.villagerunknown.graveyardsandghosts.data.PlayerData;
import me.villagerunknown.platform.data.persistent.PersistentData;
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

import static me.villagerunknown.graveyardsandghosts.Graveyardsandghosts.MOD_ID;

public class PersistentPlayerData extends PersistentData {
	
	public HashMap<UUID, PlayerData> players = new HashMap<>();
	
	private static final Codec<PersistentPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(
					Uuids.CODEC,
					PlayerData.CODEC
			).fieldOf("players").forGetter( PersistentPlayerData::getPlayers )
	).apply( instance, PersistentPlayerData::new ));
	
	PersistentPlayerData() {}
	
	public PersistentPlayerData(Map<UUID, PlayerData> playerDataMap) {
		players = new HashMap<>(playerDataMap);
	}
	
	public HashMap<UUID, PlayerData> getPlayers() {
		return players;
	}
	
	private static PersistentStateType<PersistentPlayerData> type = new PersistentStateType<>(
			MOD_ID,
			(context) -> new PersistentPlayerData(),
			ctx -> CODEC,
			null
	);
	
	public static PersistentPlayerData getServerState(MinecraftServer server) {
		PersistentStateManager persistentStateManager = getStateManager( server );
		
		PersistentPlayerData state = persistentStateManager.getOrCreate(type);
		
		state.markDirty();
		
		return state;
	}
	
	public static PlayerData getPlayerState(LivingEntity player) {
		World world = player.getWorld();
		
		if( null != world ) {
			MinecraftServer server = world.getServer();
			
			if( null != server ) {
				PersistentPlayerData serverState = getServerState(server);
				
				PlayerData playerData = new PlayerData();
				
				if( serverState.players.containsKey( player.getUuid() ) ) {
					return serverState.players.get( player.getUuid() );
				} // if
				
				serverState.players.put( player.getUuid(), playerData );
				
				return playerData;
			} // if
		} // if
		
		return new PlayerData();
	}
	
}
