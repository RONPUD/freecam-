package com.example.freecam.mixin;

import com.example.freecam.Freecam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
	/** Send mouse movement to the camera instead of turning your body. */
	@Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
	private void freecam$look(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
		if (Freecam.isEnabled() && (Object) this == MinecraftClient.getInstance().player) {
			Freecam.look(cursorDeltaX, cursorDeltaY);
			ci.cancel();
		}
	}
}
