# Freecam (Fabric, Minecraft 1.21.11)

Client-side freecam. Press **F7** to detach the camera and fly through walls like spectator mode while your body stays where it is. Press **F7** again to snap back.

## Controls

| Key | Action |
| --- | --- |
| F7 | Toggle freecam |
| W / A / S / D | Move |
| Space / Shift | Up / down |
| Ctrl (sprint) | 3x speed |
| ] / [ | Increase / decrease speed |

All keys can be rebound under Options > Controls > Freecam.

## Build

Requires Java 21.

1. Copy the `gradle/` folder, `gradlew` and `gradlew.bat` from the official Fabric example mod (https://github.com/FabricMC/fabric-example-mod, branch for 1.21.11) into this folder. They aren't included here.
2. Run `./gradlew build` (`gradlew.bat build` on Windows).
3. Take `build/libs/freecam-1.0.0.jar` (not the `-sources` one) and put it in your `mods` folder together with Fabric API.

If Gradle can't resolve a version, check https://fabricmc.net/develop for 1.21.11 and update `gradle.properties`.

## Tweaks

In `Freecam.java`: `FLY_WHERE_YOU_LOOK` makes W fly along your look direction instead of horizontally, and `speed` sets the default speed.

## Notes

- Your body doesn't move while freecam is on, but gravity and mobs still affect it. Don't use it somewhere dangerous.
- Freecam is purely visual and client-side. It only shows chunks the server has already sent you, so you can't see past your render distance.
- Some servers (including anticheat-heavy ones) disallow freecam in their rules. Check before using it.
- With Sodium or other renderer mods, terrain may still be culled by their own occlusion culling when you're inside blocks.
