package to.joe.strangeweapons;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

public class QualityManager {

    private Map<String, String> defaultQualities = new HashMap<>();
    private Map<String, String> customQualities;

    public QualityManager(Map<String, String> customQualities) {
        this.customQualities = customQualities;
        defaultQualities.put("NORMAL", ChatColor.RESET + "%s");
        defaultQualities.put("UNIQUE", ChatColor.YELLOW + "%s");
        defaultQualities.put("VINTAGE", ChatColor.DARK_AQUA + "Vintage %s");
        defaultQualities.put("GENUINE", ChatColor.DARK_GREEN + "Genuine %s");
        defaultQualities.put("STRANGE", ChatColor.GOLD + "%s");
        defaultQualities.put("UNUSUAL", ChatColor.LIGHT_PURPLE + "Unusual %s");
        defaultQualities.put("HAUNTED", ChatColor.AQUA + "Haunted %s");
        defaultQualities.put("COLLECTORS", ChatColor.DARK_RED + "Collectors %s");
        defaultQualities.put("COMMUNITY", ChatColor.GREEN + "Community %s");
        defaultQualities.put("SELFMADE", ChatColor.GREEN + "Self-Made %s");
        defaultQualities.put("MOJANG", ChatColor.DARK_PURPLE + "Mojang %s");
    }

    public String getQualityFormat(String quality) {
        for (String customQuality : customQualities.keySet()) {
            if (customQuality.equals(quality)) {
                return customQualities.get(customQuality);
            }
        }
        for (String defaultQuality : defaultQualities.keySet()) {
            if (defaultQuality.equals(quality)) {
                return defaultQualities.get(defaultQuality);
            }
        }
        return defaultQualities.get("VINTAGE");
    }

    public Map<String, String> getDefaultQualities() {
        return Collections.unmodifiableMap(defaultQualities);
    }

    public Map<String, String> getCustomQualities() {
        return Collections.unmodifiableMap(customQualities);
    }
}