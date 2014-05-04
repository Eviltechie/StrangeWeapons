package to.joe.strangeweapons;

import java.util.Collections;
import java.util.Map;

public class LevelManager {

    private Map<Integer, String> levels;

    public LevelManager(Map<Integer, String> levels) {
        this.levels = levels;
    }

    public String getLevelText(int stat) {
        while (!levels.containsKey(stat)) {
            stat--;
            if (stat < 0) {
                return "Sub-par";
            }
        }
        return levels.get(stat);
    }

    public Map<Integer, String> getLevels() {
        return Collections.unmodifiableMap(levels);
    }
}