package to.joe.strangeweapons.meta;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

public class Key implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(Key.class);
    }

    private String name;
    private List<String> description;
    private List<Integer> series;

    public Key(ItemStack item) {
        //TODO
    }

    @SuppressWarnings("unchecked")
    public Key(Map<String, Object> map) { //TODO Null checks? Need to make sure we init as empty instead of null
        name = (String) map.get("name");
        description = (List<String>) map.get("description");
        series = (List<Integer>) map.get("series");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("name", name);
        map.put("description", description);
        map.put("series", series);
        return map;
    }
}