package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.StrangeWeapons;
import to.joe.strangeweapons.meta.Crate;

public class NewCrateCommand implements CommandExecutor {

    private StrangeWeapons plugin;

    public NewCrateCommand(StrangeWeapons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command");
            return true;
        }
        int series;
        if (args.length > 0) {
            int quantity = 1;
            try {
                series = Integer.parseInt(args[0]);
                if (args.length > 1) {
                    quantity = Integer.parseInt(args[1]);
                    if (quantity > 64) {
                        quantity = 64;
                    }
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "That's not a number");
                return true;
            }
            if (!plugin.getConfig().contains("crates." + series + ".contents")) {
                sender.sendMessage(ChatColor.RED + "That crate series does not exist or contains no items");
                return true;
            }
            ItemStack crate = new Crate(series).getItemStack();
            crate.setAmount(quantity);
            ((Player) sender).getInventory().addItem(crate);
            sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + quantity + ChatColor.GOLD + " series " + ChatColor.AQUA + series + ChatColor.GOLD + " crate(s)");
        } else {
            sender.sendMessage(ChatColor.RED + "Please specify a crate series");
            return true;
        }
        return true;
    }

}
