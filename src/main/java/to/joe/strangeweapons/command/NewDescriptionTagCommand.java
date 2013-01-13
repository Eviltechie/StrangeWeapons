package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.MetaParser;

public class NewDescriptionTagCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command");
            return true;
        }
        int quantity = 1;
        if (args.length > 0) {
            try {
                quantity = Integer.parseInt(args[0]);
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
        ItemStack tag = MetaParser.makeDescriptionTag();
        tag.setAmount(quantity);
        ((Player) sender).getInventory().addItem(tag);
        sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + quantity + ChatColor.GOLD + " description tag(s)");
        return true;
    }
}