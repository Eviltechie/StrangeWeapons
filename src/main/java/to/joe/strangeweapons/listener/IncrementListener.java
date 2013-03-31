package to.joe.strangeweapons.listener;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.Part;
import to.joe.strangeweapons.StrangeWeapons;
import to.joe.strangeweapons.Util;
import to.joe.strangeweapons.meta.StrangeWeapon;

public class IncrementListener implements Listener {

    private StrangeWeapons plugin;

    public IncrementListener(StrangeWeapons plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    private void increment(Player player, Part part) {
        ItemStack item = player.getItemInHand();
        if (item.getAmount() > 0 && StrangeWeapon.isStrangeWeapon(item)) {
            StrangeWeapon strange = new StrangeWeapon(item);
            Entry<Part, Integer> oldPrimary = strange.getPrimary();
            String oldName = Util.getWeaponName(item, (int) (oldPrimary.getValue() * oldPrimary.getKey().getMultiplier()));
            strange.incrementStat(part, 1);
            Entry<Part, Integer> newPrimary = strange.getPrimary();
            String newName = Util.getWeaponName(item, (int) (newPrimary.getValue() * newPrimary.getKey().getMultiplier()));
            if (!oldName.equals(newName)) {
                plugin.getServer().broadcastMessage(player.getDisplayName() + "'s " + Util.toTitleCase(item.getType().toString().toLowerCase().replaceAll("_", " ")) + ChatColor.WHITE + " has reached a new rank: " + ChatColor.GOLD + Util.getWeaponName((int) (newPrimary.getValue() * newPrimary.getKey().getMultiplier())));
            }
            player.setItemInHand(strange.getItemStack());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            increment(event.getEntity().getKiller(), Part.PLAYER_KILLS);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Player p = null;
        if (event.getDamager() instanceof Player) {
            p = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
            p = (Player) ((Arrow) event.getDamager()).getShooter();
        }
        if (p != null) {
            increment(p, Part.DAMAGE);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        increment(event.getPlayer(), Part.BLOCKS_BROKEN);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player p = event.getEntity().getKiller();
            if (p.getItemInHand().getAmount() > 0 && StrangeWeapon.isStrangeWeapon(p.getItemInHand())) {
                StrangeWeapon item = new StrangeWeapon(p.getItemInHand());
                Entry<Part, Integer> oldPrimary = item.getPrimary();
                String oldName = Util.getWeaponName(p.getItemInHand(), (int) (oldPrimary.getValue() * oldPrimary.getKey().getMultiplier()));
                Part thisKill;
                try {
                    thisKill = Part.valueOf(event.getEntityType().name());
                } catch (IllegalArgumentException e) {
                    return;
                }
                item.incrementStat(thisKill, 1);
                item.incrementStat(Part.MOB_KILLS, 1);
                Entry<Part, Integer> newPrimary = item.getPrimary();
                String newName = Util.getWeaponName(p.getItemInHand(), (int) (newPrimary.getValue() * newPrimary.getKey().getMultiplier()));
                if (!oldName.equals(newName)) {
                    plugin.getServer().broadcastMessage(p.getDisplayName() + "'s " + Util.toTitleCase(p.getItemInHand().getType().toString().toLowerCase().replaceAll("_", " ")) + ChatColor.WHITE + " has reached a new rank: " + Util.getWeaponName((int) (newPrimary.getValue() * newPrimary.getKey().getMultiplier())));
                }
                p.setItemInHand(item.getItemStack());
            }
        }
    }
}