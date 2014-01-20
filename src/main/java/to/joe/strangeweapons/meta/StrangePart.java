package to.joe.strangeweapons.meta;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.Part;

public class StrangePart implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(StrangePart.class);
    }

    private Part part;

    public StrangePart(ItemStack item) {
        //TODO
    }

    public StrangePart(Map<String, Object> map) {
        part = Part.valueOf((String) map.get("part"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("part", part.toString());
        return map;
    }

}