package me.villagerunknown.graveyardsandghosts.client;

import me.villagerunknown.graveyardsandghosts.network.ConfirmResurrectionPayload;
import me.villagerunknown.graveyardsandghosts.network.DeclineResurrectionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.text.Text;

public class GraveyardsandghostsClientPayloads {
	
	public static void registerPayloads() {
		registerResurrectionPromptPayload();
	}
	
	public static void registerResurrectionPromptPayload() {
		// Register Confirm Resurrection Payload
		ClientPlayNetworking.registerGlobalReceiver(ConfirmResurrectionPayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				MinecraftClient client = MinecraftClient.getInstance();
				client.setScreen(new ConfirmScreen((confirm)->{
					if( confirm ) {
						ClientPlayNetworking.send(new ConfirmResurrectionPayload());
					} else {
						ClientPlayNetworking.send(new DeclineResurrectionPayload());
					} // if, else
					client.setScreen(null);
				},
						Text.translatable("text.villagerunknown-graveyardsandghosts.screen.title"),
						Text.translatable("text.villagerunknown-graveyardsandghosts.screen.message")
				));
			});
		});
	}
	
}
