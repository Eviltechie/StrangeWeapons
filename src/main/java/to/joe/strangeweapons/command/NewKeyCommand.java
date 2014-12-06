package to.joe.strangeweapons.command;

import java.util.Collection;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.MetaParser;
import to.joe.strangeweapons.Util;
import to.joe.strangeweapons.exception.BadPlayerMatchException;

public class NewKeyCommand implements CommandExecutor 
{

    /*
     * quantity
     * quantity target
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
    {
        int quantity = 1;
        Player target;

        if (args.length > 0) 
        {
            try {
                quantity = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "That's not a number");
                return true;
            }
            if (quantity < 1) {
                quantity = 1;
            } else if (quantity > 64) {
                quantity = 64;
            }
        }

        if (args.length > 1)
        {
            if(args[1].equals("*"))
            {
                this.giveKeysToAll(quantity, sender);
                return(true);
            }
            else
            {
                try
                {
                    target = Util.getPlayer(args[1], sender);
                }   
                catch (BadPlayerMatchException e) 
                {
                    sender.sendMessage(ChatColor.RED + e.getMessage());
                    return true;
                }
            }
        }
        else 
        {
            if (sender instanceof Player) 
            {
                target = (Player) sender;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "Specify a player to give the key to");
                return true;
            }
        }

        ItemStack key = MetaParser.makeKey();
        key.setAmount(quantity);
        target.getInventory().addItem(key);

        if (target.equals(sender))
        {
            sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + "you " + quantity + ChatColor.GOLD + " keys(s)");
        } 
        else 
        {
            sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + target.getName() + " " + quantity + ChatColor.GOLD + " keys(s)");
            target.sendMessage(ChatColor.GOLD + "You" + ChatColor.WHITE + " have been given: " + quantity + ChatColor.GOLD + " Keys!" ); //Tell player privately
        }
        return true;
    }
    private void giveKeysToAll(int quantity, CommandSender sender)
    {
        Collection<? extends Player> playerlist = Bukkit.getServer().getOnlinePlayers();
        for (Player p : playerlist)
        {
            ItemStack key = MetaParser.makeKey();
            key.setAmount(quantity);
            Map<Integer, ItemStack> fail =p.getInventory().addItem(key);
            sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + p.getName() + " " + quantity + ChatColor.GOLD + " keys(s)");
            p.sendMessage(ChatColor.GOLD + "You" + ChatColor.WHITE + " have been given: " + quantity + ChatColor.GOLD + " Keys!" ); //Tell player privately
            if(!fail.isEmpty())
            {
                for (ItemStack failedItem : fail.values()) 
                { //We will loop through the failed drops...
                    p.getWorld().dropItem(p.getLocation(), failedItem); //And drop each at the player's location
                    
                }
                p.sendMessage(ChatColor.RED + "Key: " + ChatColor.AQUA + "Your Inventory was full so key/s were dropped at your feet!"); //So we remind the player to have free space
                p.sendMessage(ChatColor.GOLD + "TIP: " + ChatColor.AQUA + "Make sure you have at least one empty spot in your inventory to receive random drops!"); 
            }
            
        }
        return;
    }

}
