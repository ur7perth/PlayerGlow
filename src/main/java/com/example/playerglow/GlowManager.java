package com.example.playerglow;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlowManager {

    public static final int[] COLORS = {
            0xFF3333, // أحمر
            0xFF9900, // برتقالي
            0xFFFF33, // أصفر
            0x33FF33, // أخضر
            0x33FFFF, // سماوي
            0x3377FF, // أزرق
            0xBB44FF, // بنفسجي
            0xFFFFFF  // أبيض
    };

    public static int selectedColor = COLORS[4];

    private static final Map<UUID, Integer> TRACKED = new HashMap<>();

    /** يفعّل، أو يغيّر اللون، أو يلغي إذا كان نفس اللون */
    public static void apply(UUID id) {
        Integer current = TRACKED.get(id);
        if (current != null && current == selectedColor) {
            TRACKED.remove(id);
        } else {
            TRACKED.put(id, selectedColor);
        }
    }

    public static boolean isTracked(UUID id) {
        return TRACKED.containsKey(id);
    }

    public static int getColor(UUID id) {
        return TRACKED.getOrDefault(id, 0xFFFFFF);
    }

    public static void clear() {
        TRACKED.clear();
    }

    public static boolean shouldGlow(Entity entity) {
        if (TRACKED.isEmpty()) return false;
        if (!(entity instanceof PlayerEntity target)) return false;
        if (!TRACKED.containsKey(target.getUuid())) return false;

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
