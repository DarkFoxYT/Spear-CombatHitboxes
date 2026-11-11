package net.dark.darks_pvp_optimiser.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.dark.darks_pvp_optimiser.client.SpearConfig;
import net.dark.darks_pvp_optimiser.client.SpearTarget;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityHitboxDebugRenderer.class, priority = 1100)
abstract class HitboxRendererMixin {
    @Unique private static SpearTarget darks$target = SpearTarget.NONE;
    @Unique private static float darks$margin;

    @Inject(method = "emitGizmos", at = @At("HEAD"))
    private void darks$findTarget(double x, double y, double z, DebugValueAccess data, Frustum frustum,
                                  float tickProgress, CallbackInfo ci) {
        darks$target = SpearTarget.find(tickProgress);
        var range = SpearTarget.hotbarRange();
        darks$margin = range != null && SpearConfig.get().accurateHitbox ? range.hitboxMargin() : 0;
    }

    @Inject(method = "emitGizmos", at = @At("RETURN"))
    private void darks$clearTarget(CallbackInfo ci) {
        darks$target = SpearTarget.NONE;
        darks$margin = 0;
    }

    @Unique
    private static int darks$bodyColor(Entity entity, int original) {
        if (darks$margin > 0 && entity instanceof LivingEntity) return 0xffffffff;
        return darks$target.color(entity, original);
    }

    @Unique
    private static void darks$drawMargin(Entity entity, float tickProgress) {
        if (darks$margin <= 0 || !(entity instanceof LivingEntity) || !entity.isPickable()) return;
        var box = entity.getBoundingBox().inflate(darks$margin)
                .move(entity.getPosition(tickProgress).subtract(entity.position()));
        var config = SpearConfig.get();
        int color = darks$target.color(entity, config.marginColor);
        float width = entity == darks$target.entity() ? config.lineWidth : config.marginLineWidth;
        Gizmos.cuboid(box, GizmoStyle.stroke(color, width));
    }

    @Inject(method = "showHitboxes", at = @At("HEAD"))
    private void darks$vanillaMargin(Entity entity, float tickProgress, boolean server, CallbackInfo ci) {
        if (!server) darks$drawMargin(entity, tickProgress);
    }

    @ModifyExpressionValue(method = "showHitboxes", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/gizmos/GizmoStyle;stroke(I)Lnet/minecraft/gizmos/GizmoStyle;", ordinal = 0))
    private GizmoStyle darks$vanillaStyle(GizmoStyle original, @Local(argsOnly = true) Entity entity,
                                        @Local(argsOnly = true) boolean server) {
        if (server) return original;
        if (darks$margin > 0 && entity instanceof LivingEntity) return GizmoStyle.stroke(0xffffffff);
        if (entity == darks$target.entity()) return GizmoStyle.stroke(darks$target.rgb(), SpearConfig.get().lineWidth);
        return original;
    }

    // Combat Hitboxes merges this helper into the renderer and cancels vanilla drawing.
    @Dynamic
    @Inject(method = "render_1_21_1_boxes", remap = false, require = 0, at = @At("HEAD"))
    private static void darks$combatMargin(float width, Entity entity, float tickProgress, java.awt.Color eye,
            java.awt.Color look, java.awt.Color body, java.awt.Color target, java.awt.Color hurt,
            java.awt.Color outline, boolean changeTarget, boolean hitHurt, boolean eyeHeight,
            boolean lookDir, boolean lineLook, boolean outlined, float outlineMultiplier, CallbackInfo ci) {
        darks$drawMargin(entity, tickProgress);
    }

    @Dynamic
    @ModifyExpressionValue(method = "render_1_21_1_boxes", remap = false, require = 0,
            at = @At(value = "INVOKE", target = "Ljava/awt/Color;getRGB()I", ordinal = 0))
    private static int darks$combatColor(int original, @Local(argsOnly = true) Entity entity) {
        return darks$bodyColor(entity, original);
    }

    @Dynamic
    @ModifyArg(method = "render_1_21_1_boxes", remap = false, require = 0, index = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/gizmos/GizmoStyle;stroke(IF)Lnet/minecraft/gizmos/GizmoStyle;",
                    ordinal = 1, remap = true))
    private static float darks$combatWidth(float original, @Local(argsOnly = true) Entity entity) {
        return darks$margin <= 0 && entity == darks$target.entity() ? SpearConfig.get().lineWidth : original;
    }

    @Dynamic
    @Inject(method = "truewulf$renderEntityBox", remap = false, require = 0, at = @At("HEAD"))
    private static void darks$plusMargin(CallbackInfo ci, @Local(argsOnly = true) Entity entity,
                                        @Local(argsOnly = true, ordinal = 0) float tickProgress) {
        darks$drawMargin(entity, tickProgress);
    }

    @Dynamic
    @ModifyVariable(method = "truewulf$renderEntityBox", remap = false, require = 0,
            at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static int darks$plusColor(int original, @Local(argsOnly = true) Entity entity) {
        return darks$bodyColor(entity, original);
    }

    @Dynamic
    @ModifyVariable(method = "truewulf$renderEntityBox", remap = false, require = 0,
            at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private static float darks$plusWidth(float original, @Local(argsOnly = true) Entity entity) {
        return darks$margin <= 0 && entity == darks$target.entity() ? SpearConfig.get().lineWidth : original;
    }
}
