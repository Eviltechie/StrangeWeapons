package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.StrangeWeapons;

public class DropsCommand implements CommandExecutor {

    private StrangeWeapons plugin;

    public DropsCommand(StrangeWeapons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command");
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (!plugin.getConfig().contains("drops")) {
                    sender.sendMessage(ChatColor.RED + "No drops exist yet");
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "The following items can drop");
                ConfigurationSection cs = plugin.getConfig().getConfigurationSection("drops");
                for (String item : cs.getKeys(false)) {
                    ConfigurationSection i = cs.getConfigurationSection(item);
                    sender.sendMessage(ChatColor.AQUA + item + ChatColor.GOLD + " | " + ChatColor.AQUA + ChatColor.stripColor(i.getItemStack("item").serialize().toString()) + ChatColor.GOLD + " with weight " + ChatColor.AQUA + i.getDouble("weight"));
                }
            }
            return true;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                double weight = 1;
                try {
                    weight = Double.parseDouble(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "That's not a number");
                    return true;
                }
                if (weight <= 0) {
                    sender.sendMessage(ChatColor.RED + "Weight must be greater than zero");
                    return true;
                }
                ConfigurationSection cs = plugin.getConfig().getConfigurationSection("drops");
                int maxItem = 0;
                if (plugin.getConfig().contains("drops")) {
                    for (String c : cs.getKeys(false)) {
                        int crate = Integer.parseInt(c);
                        if (crate > maxItem) {
                            maxItem = crate;
                        }
                    }
                }
                maxItem++;
                ItemStack item = ((Player) sender).getItemInHand().clone();
                plugin.getConfig().set("drops." + maxItem + ".item", item);
                plugin.getConfig().set("drops." + maxItem + ".weight", weight);
                sender.sendMessage(ChatColor.GOLD + "Added " + ChatColor.AQUA + ChatColor.stripColor(item.serialize().toString()) + ChatColor.GOLD + " with weight " + weight);
                plugin.saveConfig();
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                int item = 1;
                try {
                    item = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "That's not a number");
                    return true;
                }
                plugin.getConfig().set("drops." + item, null);
                plugin.saveConfig();
                sender.sendMessage(ChatColor.GOLD + "Removed item " + ChatColor.AQUA + item + ChatColor.GOLD + " from the drop list");
                return true;
            }
        }
        sender.sendMessage(new String[] { ChatColor.RED + "Valid commands are:", ChatColor.GOLD + "list " + ChatColor.AQUA + "- " + ChatColor.RED + "List what items can drop and their weights", ChatColor.GOLD + "add " + ChatColor.YELLOW + "<weight> " + ChatColor.AQUA + "- " + ChatColor.RED + "Add the item you are holding to the drops list", ChatColor.GOLD + "remove " + ChatColor.YELLOW + "<id> " + ChatColor.AQUA + "- " + ChatColor.RED + "Remove the specified item from the drops list" });
        return true;
    }
}