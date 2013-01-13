package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.Part;
import to.joe.strangeweapons.meta.StrangePart;

public class NewPartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command");
            return true;
        }
        if (args.length < 1) {
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
        int quantity = 1;
        if (args.length > 1) {
            try {
                quantity = Integer.parseInt(args[1]);
                if (quantity < 1) {
                    quantity = 1;
                }
                if (quantity > 64) {
                    quantity = 64;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "That's not a number");
                return true;
            }
        }
        ItemStack part = new StrangePart(chosenPart).getItemStack();
        part.setAmount(quantity);
        ((Player) sender).getInventory().addItem(part);
        sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + quantity + ChatColor.GOLD + " strange part(s) of type \"" + ChatColor.AQUA + chosenPart.getName() + ChatColor.GOLD + "\"");
        return true;
    }
}
