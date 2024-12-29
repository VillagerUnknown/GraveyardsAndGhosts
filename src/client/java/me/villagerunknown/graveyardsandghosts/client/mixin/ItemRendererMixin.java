package me.villagerunknown.graveyardsandghosts.client.mixin;

import me.villagerunknown.graveyardsandghosts.feature.playerGhostFeature;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
	
	@Inject(method = "renderItem*", at = @At("HEAD"), cancellable = true)
	public void renderItem(@Nullable LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed, CallbackInfo ci ) {
		if( null != entity && entity.hasStatusEffect(playerGhostFeature.GHOST_EFFECT_REGISTRY ) ) {
			ci.cancel();
		} // if
	}
	
}