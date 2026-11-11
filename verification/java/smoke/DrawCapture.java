package smoke;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.world.phys.AABB;
import java.util.ArrayList;
import java.util.List;
public final class DrawCapture {
    public record Box(AABB bounds, GizmoStyle style) {}
    public static final List<Box> boxes = new ArrayList<>();
}
