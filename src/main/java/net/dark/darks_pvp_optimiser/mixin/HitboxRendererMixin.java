package net.dark.darks_pvp_optimiser.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.dark.darks_pvp_optimiser.client.SpearTarget;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.EntityHitboxDebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.debug.DebugDataStore;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityHitboxDebugRenderer.class, priority = 1100)
abstract class HitboxRendererMixin {
    @Unique private static SpearTarget darks$target = SpearTarget.NONE;

    @Inject(method = "render", at = @At("HEAD"))
    private void darks$findTarget(double x, double y, double z, DebugDataStore data, Frustum frustum,
                                  float tickProgress, CallbackInfo ci) {
        darks$target = SpearTarget.find(tickProgress);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void darks$clearTarget(CallbackInfo ci) {
        darks$target = SpearTarget.NONE;
    }

    @ModifyArg(method = "drawHitbox", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/DrawStyle;stroked(I)Lnet/minecraft/client/render/DrawStyle;", ordinal = 0))
    private int darks$vanillaColor(int original, @Local(argsOnly = true) Entity entity) {
        return darks$target.color(entity, original);
    }

    // Combat Hitboxes cancels vanilla drawing and merges this helper into the renderer.
    @Dynamic
    @ModifyExpressionValue(method = "render_1_21_1_boxes", remap = false, require = 0,
            at = @At(value = "INVOKE", target = "Ljava/awt/Color;getRGB()I", ordinal = 0))
    private static int darks$combatColor(int original, @Local(argsOnly = true) Entity entity) {
        return darks$target.color(entity, original);
    }
}
