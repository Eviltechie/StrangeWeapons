package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.Quality;

public class SetQualityCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command");
            return true;
        }
        Player player = (Player) sender;
        ItemStack item = player.getItemInHand();
        Quality quality;

        if (args.length > 0) {
            try {
                quality = Quality.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Invalid quality");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Specify a weapon quality");
            return true;
        }

        return true;
    }
}