package me.villagerunknown.graveyardsandghosts;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "villagerunknown-graveyardsandghosts")
public class GraveyardsandghostsConfigData implements me.shedaniel.autoconfig.ConfigData {
	
	/**
	 * General
	 */
	
	@ConfigEntry.Category("General")
	public boolean enableParticles = true;
	
	@ConfigEntry.Category("General")
	public boolean enableFlashParticles = true;
	
	@ConfigEntry.Category("General")
	public boolean enableSounds = true;
	
	@ConfigEntry.Category("General")
	public float soundVolume = 1F;
	
	/**
	 * Graveyards
	 */
	
	@ConfigEntry.Category("Graveyards")
	public boolean enableGraveyardRespawnPoints = true;
	
	@ConfigEntry.Category("Graveyards")
	public int graveyardDiscoveryRadius = 64;
	
	@ConfigEntry.Category("Graveyards")
	public boolean enableGraveyardBlockParticles = true;
	
	@ConfigEntry.Category("Graveyards")
	public boolean enableGraveyardBlockSounds = true;
	
	@ConfigEntry.Category("Graveyards")
	public float graveyardBlockMobSoundChance = 0.0015F;
	
	@ConfigEntry.Category("Graveyards")
	public boolean enableGraveyardBlockMobSpawns = true;
	
	@ConfigEntry.Category("Graveyards")
	public float graveyardBlockMobSpawnChance = 0.25F;
	
	@ConfigEntry.Category("Graveyards")
	public int graveyardBlockMobSpawnDelayInMinutes = 5;
	
	@ConfigEntry.Category("Graveyards")
	public int graveyardBlockMobSpawnSearchRadius = 1;
	
	/**
	 * Ghosts
	 */
	
	@ConfigEntry.Category("Ghosts")
	public boolean playersStartWorldsAsGhosts = false;
	
	@ConfigEntry.Category("Ghosts")
	public boolean enablePlayerGhostOnDeath = true;
	
	@ConfigEntry.Category("Ghosts")
	public boolean enablePlayerGhostParticles = true;
	
	@ConfigEntry.Category("Ghosts")
	public boolean preventGhostCollisions = true;
	
	@ConfigEntry.Category("Ghosts")
	public boolean preventGhostBlockAttack = true;
	
	@ConfigEntry.Category("Ghosts")
	public boolean preventGhostBlockUse = true;
	
	@ConfigEntry.Category("Ghosts")
	public boolean preventGhostBlockBreak = true;
	
	@ConfigEntry.Category("Ghosts")
	public boolean preventGhostEntityInteraction = true;
	
	@ConfigEntry.Category("Ghosts")
	public boolean preventGhostEntityAttack = true;
	
	@ConfigEntry.Category("Ghosts")
	public boolean preventGhostItemUse = true;
	
	@ConfigEntry.Category("Ghosts")
	public boolean preventGhostItemPickup = true;
	
	/**
	 * Resurrections
	 */
	
	@ConfigEntry.Category("Resurrections")
	public boolean resurrectionRequiresSolidBlockBelow = true;
	
	@ConfigEntry.Category("Resurrections")
	public int resurrectionPromptRangeInBlocks = 8;
	
	@ConfigEntry.Category("Resurrections")
	public int resurrectionPromptFrequencyInSeconds = 5;
	
	@ConfigEntry.Category("Resurrections")
	public int resurrectionSafeRespawnSearchRadius = 3;
	
}
