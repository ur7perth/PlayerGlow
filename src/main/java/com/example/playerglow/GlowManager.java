Enterpackage com.example.playerglow;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GlowManager {

    /** لون الـ glow (RGB) - غيّره كما تريد */
    public static final int GLOW_COLOR = 0x00FFAA;

    private static final Set<UUID> TRACKED = new HashSet<>();

    public static boolean toggle(UUID id) {
        if (TRACKED.remove(id)) return false;
        TRACKED.add(id);
        return true;
    }

    public static boolean isTracked(UUID id) {
        return TRACKED.contains(id);
    }

    public static void clear() {
        TRACKED.clear();
    }

    /** هل يجب رسم الـ glow على هذا الكيان الآن؟ (فقط إذا كان مرئيًا بدون حواجز) */
    public static boolean shouldGlow(Entity entity) {
        if (TRACKED.isEmpty()) return false;
        if (!(entity instanceof PlayerEntity target)) return false;
        if (!TRACKED.contains(target.getUuid())) return false;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || target == mc.player) return false;

        return canSee(mc, target);
    }

    private static boolean canSee(MinecraftClient mc, PlayerEntity target) {
        Vec3d from = mc.gameRenderer.getCamera().getPos();

        double x = target.getX();
        double z = target.getZ();
        double y = target.getY();
        double h = target.getHeight();

        // نفحص ثلاث نقاط: الرأس، منتصف الجسم، القدمين
        Vec3d[] points = {
                new Vec3d(x, y + h * 0.9, z),
                new Vec3d(x, y + h * 0.5, z),
                new Vec3d(x, y + 0.1, z)
        };

        for (Vec3d point : points) {
            RaycastContext ctx = new RaycastContext(
                    from, point,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    mc.player
            );
            if (mc.world.raycast(ctx).getType() == HitResult.Type.MISS) {
                return true;
            }
        }
        return false;
    }
}
