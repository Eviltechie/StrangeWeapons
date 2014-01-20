package to.joe.strangeweapons.meta;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.Part;
import to.joe.strangeweapons.Quality;

public class StrangeWeapon implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(StrangeWeapon.class);
    }

    private String name;
    private String description;
    private Quality quality;
    private Map<Part, Integer> parts;

    public StrangeWeapon(ItemStack item) {
        //TODO
    }

    public StrangeWeapon(Map<String, Object> map) {
        //TODO
    }

    @Override
    public Map<String, Object> serialize() { //TODO
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("name", name);
        map.put("description", description);
        map.put("quality", quality.toString());
        return map;
    }

}