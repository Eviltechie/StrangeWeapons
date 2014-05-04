package to.joe.strangeweapons;

import org.bukkit.plugin.java.JavaPlugin;

public class StrangeWeapons extends JavaPlugin {

    public static StrangeWeapons plugin;

    @Override
    public void onEnable() {
        plugin = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

}