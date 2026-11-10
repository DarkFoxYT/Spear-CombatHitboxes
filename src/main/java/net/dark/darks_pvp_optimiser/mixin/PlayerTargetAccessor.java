package net.dark.darks_pvp_optimiser.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerEntity.class)
public interface PlayerTargetAccessor {
    @Invoker("getCrosshairTarget")
    static HitResult regularTarget(Entity camera, double blockReach, double meleeReach, float tickProgress) {
        throw new AssertionError();
    }
}
