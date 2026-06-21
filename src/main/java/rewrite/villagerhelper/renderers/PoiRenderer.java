package rewrite.villagerhelper.renderers;

import rewrite.villagerhelper.config.Configs;
import rewrite.villagerhelper.network.PoiCache;
import rewrite.villagerhelper.network.SelectedVillagers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class PoiRenderer {

    public static void render() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null) return;

        // Build a map of selected villager entity positions (those in render range)
        Map<Integer, Vec3> villagerPositions = new HashMap<>();
        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof Villager villager)) continue;
            int id = villager.getId();
            if (!SelectedVillagers.isSelected(id)) continue;
            villagerPositions.put(id, villager.position().add(0, villager.getBbHeight() / 2.0, 0));
            Gizmos.cuboid(villager.getBoundingBox(), GizmoStyle.strokeAndFill(0xFFFFAA00, 4f, 0x33FFAA00)).setAlwaysOnTop();
        }

        // Render all selected villagers that have POI data, whether or not the entity is visible
        for (int id : SelectedVillagers.getSelected()) {
            PoiCache.get(id).ifPresent(entry -> {
                Vec3 anchor = villagerPositions.getOrDefault(id, null);
                entry.bedPos().ifPresent(pos -> {
                    Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    Gizmos.cuboid(pos, GizmoStyle.strokeAndFill(0xFF4488FF, 4f, 0x334488FF)).setAlwaysOnTop();
                    if (anchor != null) Gizmos.line(center, anchor, 0xFF4488FF, 6f).setAlwaysOnTop();
                });
                entry.jobPos().ifPresent(pos -> {
                    Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    Gizmos.cuboid(pos, GizmoStyle.strokeAndFill(0xFFFF4444, 4f, 0x33FF4444)).setAlwaysOnTop();
                    if (anchor != null) Gizmos.line(center, anchor, 0xFFFF4444, 6f).setAlwaysOnTop();
                });
                // If both POIs known, draw a line between them regardless of villager visibility
                if (anchor == null && entry.bedPos().isPresent() && entry.jobPos().isPresent()) {
                    Vec3 bed = new Vec3(entry.bedPos().get().getX() + 0.5, entry.bedPos().get().getY() + 0.5, entry.bedPos().get().getZ() + 0.5);
                    Vec3 job = new Vec3(entry.jobPos().get().getX() + 0.5, entry.jobPos().get().getY() + 0.5, entry.jobPos().get().getZ() + 0.5);
                    Gizmos.line(bed, job, 0xFFFFFFFF, 3f).setAlwaysOnTop();
                }
            });
        }
    }
}
