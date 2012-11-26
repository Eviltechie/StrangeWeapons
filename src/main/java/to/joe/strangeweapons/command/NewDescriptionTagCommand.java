package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import to.joe.strangeweapons.NameableItem;

public class NewDescriptionTagCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Players only");
            return true;
        }
        CraftItemStack descriptionTag = new CraftItemStack(Material.PAPER);
        NameableItem n = new NameableItem(descriptionTag);
        n.makeNameTag();
        n.setName(ChatColor.YELLOW + "Description Tag");
        n.setLore(new String[]{ChatColor.WHITE + "Changes the description of an item in your inventory",
                ChatColor.GREEN + "This is a limited use item. Uses: 1"});
        Player p = (Player) sender;
        int empty = p.getInventory().firstEmpty();
        if (empty == -1) {
            sender.sendMessage(ChatColor.RED + "No free room!");
            return true;
        }
        p.getInventory().setItem(empty, descriptionTag);
        return true;
    }
}