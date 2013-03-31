package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.strangeweapons.StrangeWeapons;

public class TagCommand implements CommandExecutor {

    StrangeWeapons plugin;

    public TagCommand(StrangeWeapons weapons) {
        plugin = weapons;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Players only");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/tag <usage/description>");
            return true;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s).append(" ");
        }
        if (sb.length() - 1 > plugin.config.tagLengthLimit) {
            sender.sendMessage(ChatColor.RED + "Tags must be shorter than " + plugin.config.tagLengthLimit + " characters");
            return true;
        }
        plugin.tags.put(sender.getName(), sb.toString().substring(0, sb.length() - 1));
        sender.sendMessage(ChatColor.GOLD + "Tag set to \"" + ChatColor.AQUA + sb.toString().substring(0, sb.length() - 1) + ChatColor.GOLD + "\"");
        sender.sendMessage(ChatColor.GOLD + "Craft a strange weapon with a name or description tag to apply this tag");
        return true;
    }

}
