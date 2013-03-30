package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.Part;
import to.joe.strangeweapons.Util;
import to.joe.strangeweapons.exception.BadPlayerMatchException;
import to.joe.strangeweapons.meta.StrangePart;

public class NewPartCommand implements CommandExecutor {

    /*
     * part
     * part quantity
     * part quantity target
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        int quantity = 1;
        Part chosenPart;
        Player target;

        if (args.length > 0) {
            try {
                chosenPart = Part.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Invalid part");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Specify a part name");
            return true;
        }

        if (args.length > 1) {
            try {
                quantity = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "That's not a number");
                return true;
            }
            if (quantity < 1) {
                quantity = 1;
            } else if (quantity > 64) {
                quantity = 64;
            }
        }

        if (args.length > 2) {
            try {
                target = Util.getPlayer(args[3], sender);
            } catch (BadPlayerMatchException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
        } else {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Specify a player to give the part to");
                return true;
            }
        }

        ItemStack part = new StrangePart(chosenPart).getItemStack();
        part.setAmount(quantity);
        target.getInventory().addItem(part);
        
        if (target.equals(sender)) {
            sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + "you " + quantity + ChatColor.GOLD + " strange part(s) of type \"" + ChatColor.AQUA + chosenPart.getName() + ChatColor.GOLD + "\"");
        } else {
            sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + target.getName() + " " + quantity + ChatColor.GOLD + " strange part(s) of type \"" + ChatColor.AQUA + chosenPart.getName() + ChatColor.GOLD + "\"");
        }
        return true;
    }
}
