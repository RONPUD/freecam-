package com.example.freecam.mixin;

import com.example.freecam.Freecam;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {
	@Shadow
	protected abstract void setPos(double x, double y, double z);

	@Shadow
	protected abstract void setRotation(float yaw, float pitch);

	/** After vanilla positions the camera, overwrite it with the freecam position. */
	@Inject(method = "update", at = @At("TAIL"))
	private void freecam$update(BlockView area, Entity focusedEntity, boolean thirdPerson,
								boolean inverseView, float tickDelta, CallbackInfo ci) {
		if (!Freecam.isEnabled()) {
			return;
		}
		this.setRotation(Freecam.yaw, Freecam.pitch);
		this.setPos(
				Freecam.prevX + (Freecam.x - Freecam.prevX) * tickDelta,
				Freecam.prevY + (Freecam.y - Freecam.prevY) * tickDelta,
				Freecam.prevZ + (Freecam.z - Freecam.prevZ) * tickDelta);
	}

	/** Makes the game draw your own body (and hides the first-person hand) while detached. */
	@Inject(method = "isThirdPerson", at = @At("HEAD"), cancellable = true)
	private void freecam$thirdPerson(CallbackInfoReturnable<Boolean> cir) {
		if (Freecam.isEnabled()) {
			cir.setReturnValue(true);
		}
	}
}
