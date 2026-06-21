package rewrite.villagerhelper.network;

import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PoiCache {
    public record Entry(Optional<BlockPos> bedPos, Optional<BlockPos> jobPos) {}

    private static final Map<Integer, Entry> CACHE = new ConcurrentHashMap<>();

    public static void put(int entityId, Optional<BlockPos> bedPos, Optional<BlockPos> jobPos) {
        CACHE.put(entityId, new Entry(bedPos, jobPos));
    }

    public static Optional<Entry> get(int entityId) {
        return Optional.ofNullable(CACHE.get(entityId));
    }

    public static void remove(int entityId) {
        CACHE.remove(entityId);
    }

    public static void clear() {
        CACHE.clear();
    }
}
