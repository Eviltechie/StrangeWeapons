package to.joe.strangeweapons;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class StrangeWeapons extends JavaPlugin implements Listener {
    
    private HashMap<Integer, String> weaponText = new HashMap<Integer, String>();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        for (String test: getConfig().getStringList("levels")) {
            String[] split = test.split(",");
            weaponText.put(Integer.parseInt(split[0]), split[1]);
        }
    }
    
    private String getWeaponName(int kills) {
        while (!weaponText.containsKey(kills)) {
            kills--;
            if (kills < 0)
                return "Sub-par";
        }
        return weaponText.get(kills);
    }
    
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player && ((Player)event.getEntity()).getHealth() - event.getDamage() <= 0) {
            NameableItem item = new NameableItem((CraftItemStack)((Player)event.getDamager()).getItemInHand());
            if (item.isStrange()) {
                int kills = item.getKills();
                kills++;
                item.setKills(kills);
                item.setName(ChatColor.GOLD + getWeaponName(kills));
                item.setLore(new String[]{ChatColor.WHITE + "Kills: " + kills});
            }
        }
    }
 }