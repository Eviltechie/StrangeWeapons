package to.joe.strangeweapons;

import org.bukkit.plugin.java.JavaPlugin;

public class StrangeWeapons extends JavaPlugin {

    public static StrangeWeapons plugin;
    private QualityManager qualityManager;
    private LevelManager levelManager;

    @Override
    public void onEnable() {
        plugin = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        //TODO Init quality and level managers
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

    public QualityManager getQualityManager() {
        return qualityManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

}