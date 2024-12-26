package me.villagerunknown.graveyardsandghosts.client;

import net.fabricmc.api.ClientModInitializer;

public class GraveyardsandghostsClient implements ClientModInitializer {
	
	@Override
	public void onInitializeClient() {
		GraveyardsandghostsClientPayloads.registerPayloads();
	}
	
}
