package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import to.joe.strangeweapons.NameableItem;

public class NewKeyCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Players only");
            return true;
        }
        CraftItemStack key = new CraftItemStack(Material.BLAZE_ROD);
        NameableItem n = new NameableItem(key);
        n.makeKey();
        n.setName(ChatColor.YELLOW + "Steve Co. Supply Crate Key");
        String[] lore = {ChatColor.WHITE + "Used to open locked supply crates.",
                ChatColor.GREEN + "This is a limited use item. Uses: 1"};
        n.setLore(lore);
        Player p = (Player) sender;
        int empty = p.getInventory().firstEmpty();
        if (empty == -1) {
            sender.sendMessage(ChatColor.RED + "No free room!");
            return true;
        }
        p.getInventory().setItem(empty, key);
        return true;
    }

}
