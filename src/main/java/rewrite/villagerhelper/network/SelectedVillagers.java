package rewrite.villagerhelper.network;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SelectedVillagers {
    private static final Set<Integer> selected = new HashSet<>();

    public static boolean toggle(int entityId) {
        if (selected.contains(entityId)) {
            selected.remove(entityId);
            return false;
        } else {
            selected.add(entityId);
            return true;
        }
    }

    public static boolean isSelected(int entityId) {
        return selected.contains(entityId);
    }

    public static Set<Integer> getSelected() {
        return Collections.unmodifiableSet(selected);
    }

    public static void clear() {
        selected.clear();
    }
}
