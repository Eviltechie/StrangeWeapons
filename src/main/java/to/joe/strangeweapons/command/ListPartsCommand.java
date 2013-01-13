package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import to.joe.strangeweapons.Part;

public class ListPartsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (Part p : Part.values()) {
            sender.sendMessage(ChatColor.GOLD + p.toString() + " " + ChatColor.AQUA + p.getName());
        }
        return true;
    }

}
