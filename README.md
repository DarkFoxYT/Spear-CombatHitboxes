# Spear+CombatHitboxes

A client-side Fabric mod for Minecraft Java 1.21.11. [Download the mod](https://github.com/DarkFoxYT/Spear-CombatHitboxes/raw/refs/heads/main/downloads/Spear%2BCombatHitboxes-2.0.jar) and put the jar in your instance's `mods` folder. If you have Dark's PvP Optimiser, replace its jar. Press **F3+B** and put a spear in any hotbar slot. You can keep another item selected.

- **Red**: the living target under your crosshair is within ordinary melee reach.
- **Orange**: ordinary melee cannot reach the target, but a spear in your hotbar can.
- **Usual color**: neither check finds a target, or there is no spear in your hotbar.

Uses Minecraft's normal crosshair targeting and the spear item's attack range component, including its creative-mode range and hitbox margin. Colors indicate reach, not cooldown or guaranteed damage. This mod does not change targeting or attacks.

Works with vanilla hitboxes or [Combat Hitboxes 1.0.10](https://modrinth.com/mod/combat-hitboxes/version/xDTFU0cD). Combat Hitboxes keeps drawing its own boxes, line widths, outlines, eye and look lines. The range color takes priority over its normal target and hurt colors for the highlighted body.

Build with Java 21 and Gradle 9.6.1: run `gradle build` and install the non-sources jar from `build/libs/`.
