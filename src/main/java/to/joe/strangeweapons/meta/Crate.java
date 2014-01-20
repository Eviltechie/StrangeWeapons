package to.joe.strangeweapons.meta;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

public class Crate implements ConfigurationSerializable {

    /*
     * Name
     * Series
     * Description
     * Special Key? - stored in the key, not the crate
     * Map of items/weight
     * TODO Level?
     */

    static {
        ConfigurationSerialization.registerClass(Crate.class);
    }

    private String name;
    private int series;
    private boolean usesSpecialKey;
    private List<String> description;
    private Map<ItemStack, Double> contents;

    public Crate(ItemStack item) {
        //TODO
    }

    @SuppressWarnings("unchecked")
    public Crate(Map<String, Object> map) { //TODO Null checks? Need to make sure we init as empty instead of null
        name = (String) map.get("name");
        series = (int) map.get("series");
        usesSpecialKey = (boolean) map.get("special-key");
        description = (List<String>) map.get("description");
        contents = new LinkedHashMap<ItemStack, Double>();
        Map<String, Object> items = (Map<String, Object>) map.get("item-contents");
        Map<String, Object> weights = (Map<String, Object>) map.get("item-weights");
        for (String s : items.keySet()) {
            contents.put((ItemStack) items.get(s), (Double) weights.get(s));
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("name", name);
        map.put("series", series);
        map.put("special-key", usesSpecialKey);
        map.put("description", description);
        Map<String, Object> items = new LinkedHashMap<String, Object>();
        Map<String, Object> weights = new LinkedHashMap<String, Object>();
        int count = 0;
        for (Entry<ItemStack, Double> entry : contents.entrySet()) {
            count++;
            items.put(count + "", entry.getKey());
            weights.put(count + "", entry.getValue());
        }
        map.put("item-contents", map);
        map.put("item-weights", weights);
        return map;
    }

}