package to.joe.strangeweapons.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.MetaParser;
import to.joe.strangeweapons.Part;
import to.joe.strangeweapons.meta.Crate;
import to.joe.strangeweapons.meta.StrangePart;
import to.joe.strangeweapons.meta.StrangeWeapon;

public class StrangeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command");
            return true;
        }
        Player player = (Player) sender;
        ItemStack item = player.getItemInHand();
        Part chosenPart = Part.PLAYER_KILLS;
        if (args.length >= 1 && player.hasPermission("strangeweapons.command.strange.specifypart")) {
            try {
                chosenPart = Part.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Invalid part");
                return true;
            }
        }
        if (!item.getType().equals(Material.AIR)) {
            if (StrangeWeapon.isStrangeWeapon(item)) {
                sender.sendMessage(ChatColor.RED + "That item is already strange!");
                return true;
            } else if (Crate.isCrate(item) || MetaParser.isKey(item) || StrangePart.isPart(item) || MetaParser.isNameTag(item) || MetaParser.isDescriptionTag(item)) {
                sender.sendMessage(ChatColor.RED + "I can't make that strange!");
                return true;
            } else if (item.getAmount() != 1) {
                sender.sendMessage(ChatColor.RED + "I can only make one thing strange at a time!");
                return true;
            } else {
                player.setItemInHand(new StrangeWeapon(item, chosenPart).getItemStack());
                sender.sendMessage(ChatColor.GOLD + "POOF!");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "I can't make strange air!");
            return true;
        }
    }
}