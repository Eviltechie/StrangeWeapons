package to.joe.strangeweapons;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class StrangeWeapons extends JavaPlugin implements Listener {

    private HashMap<Integer, String> weaponText = new HashMap<Integer, String>();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("strange").setExecutor(new StrangeCommand(this));
        getServer().getPluginManager().registerEvents(this, this);

        for (String test : getConfig().getStringList("levels")) {
            String[] split = test.split(",");
            weaponText.put(Integer.parseInt(split[0]), split[1]);
        }
    }

    String toTitleCase(String string) {
        StringBuilder titleString = new StringBuilder();
        for (String s : string.split(" ")) {
            titleString.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }
        return titleString.substring(0, titleString.length() - 1);
    }

    String getWeaponName(int kills) {
        while (!weaponText.containsKey(kills)) {
            kills--;
            if (kills < 0)
                return "Sub-par";
        }
        return weaponText.get(kills);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player p = event.getEntity().getKiller();
            NameableItem item = new NameableItem((CraftItemStack) p.getItemInHand());
            if (p.getItemInHand() != null && !p.getItemInHand().equals(Material.AIR) && item.isStrange()) {
                int kills = item.getKills();
                kills++;
                item.setKills(kills);
                String oldName = item.getName();
                item.setName(ChatColor.GOLD + getWeaponName(kills) + " " + toTitleCase(p.getItemInHand().getType().toString().toLowerCase().replaceAll("_", " ")));
                item.setLore(new String[] { ChatColor.WHITE + "Kills: " + kills });
                if (!oldName.equals(item.getName())) {
                    getServer().broadcastMessage(ChatColor.GOLD + p.getName() + "'s " + oldName + " has reached a new rank: " + getWeaponName(kills));
                }
            }
        }
    }
}