package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import to.joe.strangeweapons.Quality;

public class ListQualitiesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        for (Quality q : Quality.values()) {
            sender.sendMessage(ChatColor.GOLD + q.toString() + " " + q.getPrefix() + "Diamond Sword");
        }
        return true;
    }
}