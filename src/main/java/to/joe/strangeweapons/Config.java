package to.joe.strangeweapons;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public final Map<Integer, String> weaponText = new HashMap<Integer, String>();

    public final int tagLengthLimit;
    public final int maxParts;

    public final boolean durability;
    public boolean dropAtFeet;

    public final int itemDropLimit;
    public final int itemDropReset;
    public final int itemDropRollMaxTime;
    public final int itemDropRollMinTime;

    public final int crateDropLimit;
    public final int crateDropReset;
    public final int crateDropRollMaxTime;
    public final int crateDropRollMinTime;

    public Config(FileConfiguration config) {
        for (String level : config.getConfigurationSection("levels").getKeys(false)) {
            weaponText.put(Integer.parseInt(level), config.getString("levels." + level));
        }

        tagLengthLimit = config.getInt("taglengthlimit", 50);
        int mParts = config.getInt("maxparts", 3);
        if (mParts != 0) {
            mParts++;
        }
        maxParts = mParts;

        durability = config.getBoolean("durability", true);
        dropAtFeet = config.getBoolean("dropitemiffull", false);

        itemDropLimit = config.getInt("dropconfig.itemDropLimit", 9);
        itemDropReset = config.getInt("dropconfig.itemDropReset", 10080);
        itemDropRollMaxTime = config.getInt("dropconfig.itemDropRollMaxTime", 70);
        itemDropRollMinTime = config.getInt("dropconfig.itemDropRollMinTime", 30);

        crateDropLimit = config.getInt("dropconfig.crateDropLimit", 3);
        crateDropReset = config.getInt("dropconfig.crateDropReset", 10080);
        crateDropRollMaxTime = config.getInt("dropconfig.crateDropRollMaxTime", 70);
        crateDropRollMinTime = config.getInt("dropconfig.crateDropRollMinTime", 30);
    }

}