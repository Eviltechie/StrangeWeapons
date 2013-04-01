package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.MetaParser;
import to.joe.strangeweapons.Util;
import to.joe.strangeweapons.exception.BadPlayerMatchException;

public class NewNameTagCommand implements CommandExecutor {

    /*
     * quantity
     * quantity target
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        int quantity = 1;
        Player target;

        if (args.length > 0) {
            try {
                quantity = Integer.parseInt(args[0]);
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

        if (args.length > 1) {
            try {
                target = Util.getPlayer(args[1], sender);
            } catch (BadPlayerMatchException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
        } else {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Specify a player to give the name tag to");
                return true;
            }
        }

        ItemStack tag = MetaParser.makeNameTag();
        tag.setAmount(quantity);
        target.getInventory().addItem(tag);

        if (target.equals(sender)) {
            sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + "you " + quantity + ChatColor.GOLD + " name tags(s)");
        } else {
            sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + target.getName() + " " + quantity + ChatColor.GOLD + " name tags(s)");
        }
        return true;
    }
}