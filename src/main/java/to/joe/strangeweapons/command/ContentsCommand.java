package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.strangeweapons.meta.Crate;

public class ContentsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command");
            return true;
        }
        Player player = (Player) sender;
        if (Crate.isCrate(player.getItemInHand())) {
            Crate crate = new Crate(player.getItemInHand());
            sender.sendMessage(crate.generateLore().toArray(new String[crate.generateLore().size()]));
        } else {
            sender.sendMessage(ChatColor.RED + "That's not a crate");
        }
        return true;
    }

}
