package com.example.freecam;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** All freecam state and movement logic. */
public final class Freecam {
	/** true = W flies along where you look (like most freecams); false = horizontal only. */
	private static final boolean FLY_WHERE_YOU_LOOK = false;

	private static final double MIN_SPEED = 0.1;
	private static final double MAX_SPEED = 8.0;

	private static boolean enabled;

	// Camera position (current tick and previous tick, for smooth rendering)
	public static double x, y, z;
	public static double prevX, prevY, prevZ;
	public static float yaw, pitch;

	// Blocks per tick (0.55 is about creative-flight speed)
	private static double speed = 0.55;

	private static Input savedInput;
	private static boolean savedCulling;

	private Freecam() {}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void toggle() {
		if (enabled) {
			disable();
		} else {
			enable();
		}
	}

	private static void enable() {
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayerEntity player = mc.player;
		if (player == null) {
			return;
		}

		Vec3d eye = player.getEyePos();
		x = prevX = eye.x;
		y = prevY = eye.y;
		z = prevZ = eye.z;
		yaw = player.getYaw();
		pitch = player.getPitch();

		// Swap in an empty Input so your body stands still while the camera flies.
		savedInput = player.input;
		player.input = new Input();

		// Spectator turns off cave culling; do the same so terrain doesn't vanish inside walls.
		savedCulling = mc.chunkCullingEnabled;
		mc.chunkCullingEnabled = false;
		mc.worldRenderer.scheduleTerrainUpdate();

		enabled = true;
		mc.player.sendMessage(Text.literal("Freecam: ON"), true);
	}

	private static void disable() {
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayerEntity player = mc.player;
		if (player != null && savedInput != null) {
			player.input = savedInput;
		}
		savedInput = null;

		mc.chunkCullingEnabled = savedCulling;
		if (mc.worldRenderer != null) {
			mc.worldRenderer.scheduleTerrainUpdate();
		}

		enabled = false;
		if (player != null) {
			player.sendMessage(Text.literal("Freecam: OFF"), true);
		}
	}

	/** Called on disconnect: the old player object is gone, so just drop state. */
	public static void reset() {
		if (!enabled) {
			return;
		}
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.chunkCullingEnabled = savedCulling;
		savedInput = null;
		enabled = false;
	}

	public static void changeSpeed(double factor) {
		speed = MathHelper.clamp(speed * factor, MIN_SPEED, MAX_SPEED);
		MinecraftClient mc = MinecraftClient.getInstance();
		if (enabled && mc.player != null) {
			mc.player.sendMessage(Text.literal(String.format("Freecam speed: %.2f", speed)), true);
		}
	}

	/** Mouse look, redirected from the player to the camera. */
	public static void look(double dx, double dy) {
		yaw += (float) dx * 0.15F;
		pitch = MathHelper.clamp(pitch + (float) dy * 0.15F, -90.0F, 90.0F);
	}

	/** Runs once per client tick while enabled. */
	public static void tick(MinecraftClient mc) {
		if (!enabled) {
			return;
		}
		if (mc.player == null || mc.world == null) {
			reset();
			return;
		}

		prevX = x;
		prevY = y;
		prevZ = z;

		GameOptions o = mc.options;
		double forward = (o.forwardKey.isPressed() ? 1 : 0) - (o.backKey.isPressed() ? 1 : 0);
		double strafe = (o.leftKey.isPressed() ? 1 : 0) - (o.rightKey.isPressed() ? 1 : 0);
		double vertical = (o.jumpKey.isPressed() ? 1 : 0) - (o.sneakKey.isPressed() ? 1 : 0);

		double yawRad = Math.toRadians(yaw);
		double pitchRad = Math.toRadians(pitch);
		double sinY = Math.sin(yawRad);
		double cosY = Math.cos(yawRad);

		double fx, fy, fz;
		if (FLY_WHERE_YOU_LOOK) {
			double cosP = Math.cos(pitchRad);
			fx = -sinY * cosP;
			fy = -Math.sin(pitchRad);
			fz = cosY * cosP;
		} else {
			fx = -sinY;
			fy = 0;
			fz = cosY;
		}

		// Left vector is always horizontal.
		double lx = cosY;
		double lz = sinY;

		double dx = fx * forward + lx * strafe;
		double dy = fy * forward + vertical;
		double dz = fz * forward + lz * strafe;

		double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
		if (len > 1.0E-6) {
			double s = speed * (o.sprintKey.isPressed() ? 3.0 : 1.0) / len;
			x += dx * s;
			y += dy * s;
			z += dz * s;
		}
	}
}
