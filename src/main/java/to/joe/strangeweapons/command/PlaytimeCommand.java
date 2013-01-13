package to.joe.strangeweapons.command;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import to.joe.strangeweapons.StrangeWeapons;
import to.joe.strangeweapons.datastorage.DataStorageException;
import to.joe.strangeweapons.datastorage.PlayerDropData;

public class PlaytimeCommand implements CommandExecutor {

    private StrangeWeapons plugin;

    public PlaytimeCommand(StrangeWeapons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /playtime <name>");
            return true;
        }
        try {
            if (plugin.getDSI().playerDropDataExists(args[0])) {
                PlayerDropData data = plugin.getDSI().getPlayerDropData(args[0]);
                int seconds = data.getPlayTime();
                int years = seconds / 31536000;
                seconds = seconds % 31536000;
                int months = seconds / 2628000;
                seconds = seconds % 2628000;
                int weeks = seconds / 604800;
                seconds = seconds % 604800;
                int days = seconds / 86400;
                seconds = seconds % 86400;
                int hours = seconds / 3600;
                seconds = seconds % 3600;
                int minutes = seconds / 60;
                seconds = seconds % 60;
                StringBuilder timeString = new StringBuilder(ChatColor.GOLD + data.getPlayer() + ChatColor.YELLOW + " has played for " + ChatColor.AQUA);
                if (years != 0) {
                    timeString.append(years + " years ");
                }
                if (months != 0) {
                    timeString.append(months + " months");
                }
                if (weeks != 0) {
                    timeString.append(weeks + " weeks ");
                }
                if (days != 0) {
                    timeString.append(days + " days ");
                }
                if (hours != 0) {
                    timeString.append(hours + " hours ");
                }
                if (minutes != 0) {
                    timeString.append(minutes + " minutes ");
                }
                if (seconds != 0) {
                    timeString.append(seconds + " seconds ");
                }
                sender.sendMessage(timeString.toString());
            } else {
                sender.sendMessage(ChatColor.RED + "I haven't seen " + args[0]);
            }
        } catch (DataStorageException e) {
            plugin.getLogger().log(Level.SEVERE, "Error reading playtime for player " + args[0], e);
            sender.sendMessage(ChatColor.RED + "Something went wrong!");
        }
        return true;
    }

}
