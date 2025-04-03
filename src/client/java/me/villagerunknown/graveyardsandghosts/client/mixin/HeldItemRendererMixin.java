package me.villagerunknown.graveyardsandghosts.client.mixin;

import me.villagerunknown.graveyardsandghosts.feature.playerGhostFeature;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	@Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
	public void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, CallbackInfo ci ) {
		if( null != entity && entity.hasStatusEffect(playerGhostFeature.GHOST_EFFECT_REGISTRY ) ) {
			ci.cancel();
		} // if
	}
	
	@Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At("HEAD"), cancellable = true)
	public void renderItem(float tickProgress, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci ) {
		if( null != player && player.hasStatusEffect(playerGhostFeature.GHOST_EFFECT_REGISTRY ) ) {
			ci.cancel();
		} // if
	}
	
	@Inject(method = "renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Arm;)V", at = @At("HEAD"), cancellable = true)
	public void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm, CallbackInfo ci) {
		ClientPlayerEntity player = this.client.player;
		if( null != player && player.hasStatusEffect(playerGhostFeature.GHOST_EFFECT_REGISTRY  ) ) {
			ci.cancel();
		}
	}
	
}
