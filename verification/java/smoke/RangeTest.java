package smoke;
import net.dark.darks_pvp_optimiser.client.SpearTarget;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
public class RangeTest implements FabricClientGameTest {
    private net.minecraft.client.gui.screens.Screen settings;
    public void runTest(ClientGameTestContext c) {
        c.runOnClient(mc -> {
            if (net.dark.darks_pvp_optimiser.client.SpearConfig.get().accurateHitbox) throw new AssertionError("Default must be disabled");
            var screen = (me.shedaniel.clothconfig2.gui.ClothConfigScreen)
                    new net.dark.darks_pvp_optimiser.client.SpearModMenu().getModConfigScreenFactory().create(null);
            var categories = screen.getCategorizedEntries();
            
            var spear = categories.entrySet().stream().filter(e -> e.getKey().getString().equals("Spear")).findFirst().orElseThrow().getValue();
            if (spear.size() != 6) throw new AssertionError("Missing spear entries");
            ((me.shedaniel.clothconfig2.gui.entries.ColorEntry)spear.get(0)).setValue(0xff00ff00);
            ((me.shedaniel.clothconfig2.gui.entries.FloatListEntry)spear.get(2)).setValue("5.0");
            screen.saveAll(false);
            if (net.dark.darks_pvp_optimiser.client.SpearConfig.get().spearColor != 0xff00ff00) throw new AssertionError("Color did not save");
            if (net.dark.darks_pvp_optimiser.client.SpearConfig.get().lineWidth != 5) throw new AssertionError("Width did not save");
            var ownScreen = (me.shedaniel.clothconfig2.gui.ClothConfigScreen)
                    new net.dark.darks_pvp_optimiser.client.SpearModMenu().getModConfigScreenFactory().create(null);
            if (ownScreen.getCategorizedEntries().size() != categories.size()) throw new AssertionError("Own Mod Menu entry failed to route");
            screen.selectedCategoryIndex = categories.size() - 1;
            settings = screen;
        });
        c.setScreen(() -> settings);
        c.takeScreenshot("spear-settings");
        c.setScreen(() -> null);
        try (var world = c.worldBuilder().create()) {
            var s = world.getServer();
            s.runCommand("fill -3 199 -2 3 199 12 stone");
            s.runCommand("tp @p 0.5 200 0.5 0 0");
            s.runCommand("gamemode survival @p");
            s.runCommand("item replace entity @p hotbar.8 with minecraft:iron_spear");
            s.runCommand("summon husk 0.5 200 5.0 {NoAI:1b,Silent:1b,Invulnerable:1b}");
            c.waitTicks(30);
            check(c, 0xff00ff00, "custom color on unselected spear");
            c.getInput().holdKey(options -> options.keyDebugModifier);
            c.getInput().pressKey(options -> options.keyDebugShowHitboxes);
            c.getInput().releaseKey(options -> options.keyDebugModifier);
            c.waitTicks(5);
            c.takeScreenshot("custom-spear-width");
            c.runOnClient(mc -> {
                if (DrawCapture.boxes.stream().noneMatch(box -> box.style().stroke() == 0xff00ff00 && box.style().strokeWidth() == 5)) throw new AssertionError("Custom target was not rendered");
                DrawCapture.boxes.clear();
            });
            c.runOnClient(mc -> {
                var config = net.dark.darks_pvp_optimiser.client.SpearConfig.get();
                config.spearColor = 0xffff9000;
                config.accurateHitbox = true;
            });
            c.waitTicks(5);
            c.takeScreenshot("accurate-spear-hitbox");
            c.runOnClient(mc -> {
                if (DrawCapture.boxes.stream().noneMatch(box -> box.style().stroke() == 0xffffffff && Math.abs(box.bounds().getXsize()-0.6) < 0.01)) throw new AssertionError("White body box missing");
                if (DrawCapture.boxes.stream().noneMatch(box -> box.style().stroke() == 0xffff9000 && box.bounds().getXsize() > 0.6)) throw new AssertionError("Expanded spear box missing");
            });
            s.runCommand("setblock 0 201 2 stone");
            check(c, 0, "wall");
            s.runCommand("setblock 0 201 2 air");
            s.runCommand("item replace entity @p hotbar.0 with minecraft:diamond_spear");
            check(c, 0xffff9000, "selected spear");
            s.runCommand("tp @e[type=husk,limit=1,sort=nearest] 0.5 200 3.0");
            check(c, 0xffff0000, "melee priority");
            s.runCommand("tp @e[type=husk,limit=1,sort=nearest] 0.5 200 10.0");
            check(c, 0, "outside spear reach");
            s.runCommand("tp @e[type=husk,limit=1,sort=nearest] 0.5 200 5.0");
            check(c, 0xffff9000, "back in spear range");
            s.runCommand("clear @p");
            check(c, 0, "no spear");
        }
    }
    void check(ClientGameTestContext c, int rgb, String label) {
        c.waitTicks(6);
        c.waitFor(mc -> SpearTarget.find(1).rgb() == rgb, 100);
        System.out.println("PASSED: " + label);
    }
}
