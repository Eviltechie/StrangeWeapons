package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.StrangeWeapons;
import to.joe.strangeweapons.Util;
import to.joe.strangeweapons.exception.BadPlayerMatchException;
import to.joe.strangeweapons.meta.Crate;

public class NewCrateCommand implements CommandExecutor {

    private StrangeWeapons plugin;

    public NewCrateCommand(StrangeWeapons plugin) {
        this.plugin = plugin;
    }

    /*
     * series
     * series quantity
     * series quantity target
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        int series;
        int quantity = 1;
        Player target;

        try {
            if (args.length > 0) { //Here we try for the series.
                series = Integer.parseInt(args[0]);
                if (!plugin.getConfig().contains("crates." + series + ".contents")) { //Here it doesn't exist.
                    sender.sendMessage(ChatColor.RED + "That crate series does not exist or contains no items");
                    return true;
                }
            } else { //And here they forgot it.
                sender.sendMessage(ChatColor.RED + "Specify a crate series");
                return true;
            }

            if (args.length > 1) { //Try for the quantity. This is optional.
                quantity = Integer.parseInt(args[1]);
                if (quantity < 1) {
                    quantity = 1;
                } else if (quantity > 64) {
                    quantity = 64;
                }
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "That's not a number");
            return true;
        }

        if (args.length > 2) { //Here we try for the target.
            try {
                target = Util.getPlayer(args[2], sender); //Argument provided, hopefully it matches
            } catch (BadPlayerMatchException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
        } else { //Looks like they left it off, better check to make sure they're not console.
            if (sender instanceof Player) { //Not console!
                target = (Player) sender;
            } else { //Console :(
                sender.sendMessage(ChatColor.RED + "Specify a player to give the crate to");
                return true;
            }
        }

        ItemStack crate = new Crate(series).getItemStack();
        crate.setAmount(quantity);
        target.getInventory().addItem(crate);

        if (target.equals(sender)) {
            sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + "you " + quantity + ChatColor.GOLD + " series " + ChatColor.AQUA + series + ChatColor.GOLD + " crate(s)");
        } else {
            sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + target.getName() + " " + quantity + ChatColor.GOLD + " series " + ChatColor.AQUA + series + ChatColor.GOLD + " crate(s)");
        }
        return true;
    }
}