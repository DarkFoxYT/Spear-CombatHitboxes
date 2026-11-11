package smoke.mixin;
import smoke.DrawCapture;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Gizmos.class)
abstract class CaptureMixin {
    @Inject(method = "cuboid(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/gizmos/GizmoStyle;)Lnet/minecraft/gizmos/GizmoProperties;", at = @At("HEAD"))
    private static void capture(AABB box, GizmoStyle style, CallbackInfoReturnable<?> ci) {
        if (DrawCapture.boxes.size() > 10000) DrawCapture.boxes.clear();
        DrawCapture.boxes.add(new DrawCapture.Box(box, style));
    }
}
