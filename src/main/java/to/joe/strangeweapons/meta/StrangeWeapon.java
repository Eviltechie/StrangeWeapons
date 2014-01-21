package to.joe.strangeweapons.meta;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.Part;
import to.joe.strangeweapons.Quality;
import to.joe.strangeweapons.StrangeWeapons;

public class StrangeWeapon implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(StrangeWeapon.class);
    }

    private String name;
    private String description; //TODO Make description multiline?
    private Quality quality;
    private Map<Part, Integer> parts;

    public StrangeWeapon(ItemStack item) {
        //TODO
    }

    public StrangeWeapon(Map<String, Object> map) { //TODO Null checks? Need to make sure we init as empty instead of null
        name = (String) map.get("name");
        description = (String) map.get("description");
        quality = StrangeWeapons.plugin.qualities.get(map.get("quality"));
        parts = new LinkedHashMap<Part, Integer>();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("name", name);
        map.put("description", description);
        map.put("quality", quality.toString());
        Map<String, Object> parts = new LinkedHashMap<String, Object>();
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        int count = 0;
        for (Entry<Part, Integer> entry : this.parts.entrySet()) {
            count++;
            parts.put(count + "", entry.getKey());
            values.put(count + "", entry.getValue());
        }
        map.put("parts", parts);
        map.put("values", values);
        return map;
    }

}