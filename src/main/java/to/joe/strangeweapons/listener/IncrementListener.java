package to.joe.strangeweapons.listener;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.Part;
import to.joe.strangeweapons.StrangeWeapons;
import to.joe.strangeweapons.Util;
import to.joe.strangeweapons.meta.Crate;
import to.joe.strangeweapons.meta.StrangeWeapon;

public class IncrementListener implements Listener {

    private StrangeWeapons plugin;

    public IncrementListener(StrangeWeapons plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player p = event.getEntity().getKiller();
            if (p.getItemInHand().getAmount() > 0 && StrangeWeapon.isStrangeWeapon(p.getItemInHand())) {
                StrangeWeapon item = new StrangeWeapon(p.getItemInHand());
                Entry<Part, Integer> oldPrimary = item.getPrimary();
                String oldName = Util.getWeaponName(p.getItemInHand(), (int) (oldPrimary.getValue() * oldPrimary.getKey().getMultiplier()));
                item.incrementStat(Part.PLAYER_KILLS, 1);
                Entry<Part, Integer> newPrimary = item.getPrimary();
                String newName = Util.getWeaponName(p.getItemInHand(), (int) (newPrimary.getValue() * newPrimary.getKey().getMultiplier()));
                if (!oldName.equals(newName)) {
                    plugin.getServer().broadcastMessage(p.getDisplayName() + "'s " + Util.toTitleCase(p.getItemInHand().getType().toString().toLowerCase().replaceAll("_", " ")) + ChatColor.WHITE + " has reached a new rank: " + ChatColor.GOLD + Util.getWeaponName((int) (newPrimary.getValue() * newPrimary.getKey().getMultiplier())));
                }
                p.setItemInHand(item.getItemStack());
            }
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
            if (p.getItemInHand().getAmount() > 0 && StrangeWeapon.isStrangeWeapon(p.getItemInHand())) {
                StrangeWeapon item = new StrangeWeapon(p.getItemInHand());
                Entry<Part, Integer> oldPrimary = item.getPrimary();
                String oldName = Util.getWeaponName(p.getItemInHand(), (int) (oldPrimary.getValue() * oldPrimary.getKey().getMultiplier()));
                item.incrementStat(Part.DAMAGE, event.getDamage());
                Entry<Part, Integer> newPrimary = item.getPrimary();
                String newName = Util.getWeaponName(p.getItemInHand(), (int) (newPrimary.getValue() * newPrimary.getKey().getMultiplier()));
                if (!oldName.equals(newName)) {
                    plugin.getServer().broadcastMessage(p.getDisplayName() + "'s " + Util.toTitleCase(p.getItemInHand().getType().toString().toLowerCase().replaceAll("_", " ")) + ChatColor.WHITE + " has reached a new rank: " + Util.getWeaponName((int) (newPrimary.getValue() * newPrimary.getKey().getMultiplier())));
                }
                p.setItemInHand(item.getItemStack());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (p.getItemInHand().getAmount() > 0 && StrangeWeapon.isStrangeWeapon(p.getItemInHand())) {
            StrangeWeapon item = new StrangeWeapon(p.getItemInHand());
            Entry<Part, Integer> oldPrimary = item.getPrimary();
            String oldName = Util.getWeaponName(p.getItemInHand(), (int) (oldPrimary.getValue() * oldPrimary.getKey().getMultiplier()));
            item.incrementStat(Part.BLOCKS_BROKEN, 1);
            Entry<Part, Integer> newPrimary = item.getPrimary();
            String newName = Util.getWeaponName(p.getItemInHand(), (int) (newPrimary.getValue() * newPrimary.getKey().getMultiplier()));
            if (!oldName.equals(newName)) {
                plugin.getServer().broadcastMessage(p.getDisplayName() + "'s " + Util.toTitleCase(p.getItemInHand().getType().toString().toLowerCase().replaceAll("_", " ")) + ChatColor.WHITE + " has reached a new rank: " + Util.getWeaponName((int) (newPrimary.getValue() * newPrimary.getKey().getMultiplier())));
            }
            p.setItemInHand(item.getItemStack());
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getPlayer().getItemInHand();
        if (event.getRightClicked() instanceof PoweredMinecart && item.getType() == Material.COAL && StrangeWeapon.isStrangeWeapon(p.getItemInHand())) {
            event.setCancelled(true);
            p.updateInventory();
            p.sendMessage(ChatColor.RED + "You may not use that in a powered minecart.");
        }
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

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Crate.isCrate(event.getItemInHand())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You may not place Steve Co. Supply Crates");
        } else if (event.getItemInHand().getType().isBlock() && StrangeWeapon.isStrangeWeapon(event.getItemInHand())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You may not place strange weapons");
        }
    }
}