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
        plugin.tags.put(sender.getName(), sb.toString());
        return true;
    }

}
