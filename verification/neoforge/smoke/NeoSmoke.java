package smoke;

import net.dark.darks_pvp_optimiser.client.SpearConfig;
import net.dark.darks_pvp_optimiser.client.SpearTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;

@Mod(value = "spear_verification", dist = Dist.CLIENT)
public final class NeoSmoke {
    private int ticks;
    public NeoSmoke() {
        NeoForge.EVENT_BUS.addListener(this::tick);
    }
    private void tick(ClientTickEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || ++ticks != 40) return;
        try {
            EntityHitboxDebugRenderer.class.getDeclaredMethods();
            var container = ModList.get().getModContainerById("darks_pvp_optimiser").orElseThrow();
            var factory = IConfigScreenFactory.getForMod(container.getModInfo()).orElseThrow();
            var screen = (ClothConfigScreen) factory.createScreen(container, null);
            if (screen.getCategorizedEntries().keySet().stream().noneMatch(text -> text.getString().equals("Spear"))) {
                throw new AssertionError("Missing NeoForge spear config");
            }
            if (SpearConfig.get().accurateHitbox) throw new AssertionError("Accurate default must be disabled");
            mc.player.setPos(0.5, 200, 0.5);
            mc.player.setYRot(0);
            mc.player.setXRot(0);
            mc.player.getInventory().setItem(8, Items.IRON_SPEAR.getDefaultInstance());
            var target = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> type.getDescriptionId().equals("entity.minecraft.husk")).findFirst().orElseThrow().create(mc.level, EntitySpawnReason.COMMAND);
            target.setPos(0.5, 200, 5);
            target.setId(123456);
            mc.level.addEntity(target);
            var hit = SpearTarget.find(1);
            if (hit.entity() == null || hit.rgb() != 0xffff9000) throw new AssertionError("Spear range targeting failed: " + hit);
            target.setPos(0.5, 200, 3);
            hit = SpearTarget.find(1);
            if (hit.entity() == null || hit.rgb() != 0xffff0000) throw new AssertionError("Melee range targeting failed: " + hit);
            System.out.println("NEOFORGE_VERIFIED: config, renderer transformation, spear range, melee priority");
            mc.stop();
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        }
    }
}
