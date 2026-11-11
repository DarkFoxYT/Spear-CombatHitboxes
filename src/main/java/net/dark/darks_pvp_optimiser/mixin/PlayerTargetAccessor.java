package net.dark.darks_pvp_optimiser.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LocalPlayer.class)
public interface PlayerTargetAccessor {
    @Invoker("pick")
    static HitResult regularTarget(Entity camera, double blockReach, double meleeReach, float tickProgress) {
        throw new AssertionError();
    }
}
