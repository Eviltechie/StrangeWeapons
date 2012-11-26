package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import to.joe.strangeweapons.NameableItem;
import to.joe.strangeweapons.Part;

public class NewPartCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Players only");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "/newpart <partname>");
            return true;
        }
        Part chosenPart;
        try {
            chosenPart = Part.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid part");
            return true;
        }
        CraftItemStack part = new CraftItemStack(Material.PAPER);
        NameableItem n = new NameableItem(part);
        n.makePart(chosenPart);
        n.setName(ChatColor.YELLOW + "Strange Part: " + chosenPart.getName());
        String[] lore = {ChatColor.WHITE + "Adding this Strange Part to a Strange-quality weapon",
                ChatColor.WHITE + "will enable it to track an additional new statistic.",
                ChatColor.GREEN + "This is a limited use item. Uses: 1"};
        n.setLore(lore);
        Player p = (Player) sender;
        int empty = p.getInventory().firstEmpty();
        if (empty == -1) {
            sender.sendMessage(ChatColor.RED + "No free room!");
            return true;
        }
        p.getInventory().setItem(empty, part);
        return true;
    }
}
