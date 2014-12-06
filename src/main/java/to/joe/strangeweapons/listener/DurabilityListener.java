package to.joe.strangeweapons.listener;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import to.joe.strangeweapons.StrangeWeapons;
import to.joe.strangeweapons.meta.StrangeWeapon;

public class DurabilityListener implements Listener {

    public DurabilityListener(StrangeWeapons plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
                p.getItemInHand().setDurability((short) 0);
                p.updateInventory();
            }
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (p.getItemInHand().getAmount() > 0 && StrangeWeapon.isStrangeWeapon(p.getItemInHand())) {
            p.getItemInHand().setDurability((short) 0);
            p.updateInventory();
        }
    }


    @EventHandler
    public void onBowFire(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player && StrangeWeapon.isStrangeWeapon(event.getBow())) {
            event.getBow().setDurability((short) 0);
            ((Player) event.getEntity()).updateInventory();
        }
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }
        Material mat = event.getItem().getType();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (mat.equals(Material.WOOD_HOE) || mat.equals(Material.STONE_HOE) || mat.equals(Material.IRON_HOE) || mat.equals(Material.GOLD_HOE) || mat.equals(Material.DIAMOND_HOE)) && StrangeWeapon.isStrangeWeapon(event.getItem())) {
            event.getItem().setDurability((short) 0);
            event.getPlayer().updateInventory();
        }
    }


    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getPlayer().getItemInHand().getType().equals(Material.FISHING_ROD) && StrangeWeapon.isStrangeWeapon(event.getPlayer().getItemInHand())) {
            event.getPlayer().getItemInHand().setDurability((short) 0);
            event.getPlayer().updateInventory();
        }
    }
}