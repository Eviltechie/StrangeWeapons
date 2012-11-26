package to.joe.strangeweapons.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.NameableItem;

public class NewCrateCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Players only");
            return true;
        }
        CraftItemStack crate = new CraftItemStack(Material.CHEST);
        NameableItem n = new NameableItem(crate);
        n.makeCrate();
        n.setName(ChatColor.YELLOW + "Steve Co. Supply Crate");
        String[] lore = {ChatColor.AQUA + "Crate series #1",
                ChatColor.WHITE + "You need a Steve Co. Supply Crate Key to open this.",
                ChatColor.WHITE + "You can pick one up at the Steve Co. Store."};
        n.setLore(lore);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        items.add(new ItemStack(Material.DIAMOND_SWORD));
        items.add(new ItemStack(Material.BREWING_STAND_ITEM));
        items.add(new ItemStack(Material.DIRT));
        n.setCrateContents(items);
        Player p = (Player) sender;
        int empty = p.getInventory().firstEmpty();
        if (empty == -1) {
            sender.sendMessage(ChatColor.RED + "No free room!");
            return true;
        }
        p.getInventory().setItem(empty, crate);
        return true;
    }

}
