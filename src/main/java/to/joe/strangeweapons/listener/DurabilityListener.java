package to.joe.strangeweapons.listener;

import org.bukkit.event.Listener;

import to.joe.strangeweapons.StrangeWeapons;

public class DurabilityListener implements Listener {

    private StrangeWeapons plugin;

    public DurabilityListener(StrangeWeapons plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }
}