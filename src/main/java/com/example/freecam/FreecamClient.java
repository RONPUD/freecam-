package com.example.freecam;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class FreecamClient implements ClientModInitializer {
	private static final KeyBinding.Category CATEGORY =
			KeyBinding.Category.create(Identifier.of("freecam", "main"));

	private static KeyBinding toggleKey;
	private static KeyBinding fasterKey;
	private static KeyBinding slowerKey;

	@Override
	public void onInitializeClient() {
		toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.freecam.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F7, CATEGORY));
		fasterKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.freecam.faster", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, CATEGORY));
		slowerKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.freecam.slower", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, CATEGORY));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.wasPressed()) {
				Freecam.toggle();
			}
			while (fasterKey.wasPressed()) {
				Freecam.changeSpeed(1.25);
			}
			while (slowerKey.wasPressed()) {
				Freecam.changeSpeed(0.8);
			}
			Freecam.tick(client);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> Freecam.reset());
	}
}
