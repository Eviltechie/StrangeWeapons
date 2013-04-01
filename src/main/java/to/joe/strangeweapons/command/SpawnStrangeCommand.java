package to.joe.strangeweapons.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.Part;
import to.joe.strangeweapons.Quality;
import to.joe.strangeweapons.Util;
import to.joe.strangeweapons.exception.BadPlayerMatchException;
import to.joe.strangeweapons.meta.StrangeWeapon;

public class SpawnStrangeCommand implements CommandExecutor {

    /*
     * quality item:damage "part..."
     * quality item:damage "part..." player
     * quality "name" item:damage "part..."
     * quality "name" item:damage "part..." player
     * quality "name" "description" item:damage "part..."
     * quality "name" "description" item:damage "part..." player
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Iterator<String> i = Arrays.asList(args).iterator();
        final List<String> combinedArgs = new ArrayList<String>();
        try {
            while (i.hasNext()) {
                String s = i.next();
                if (s.matches("\".*?\"")) {
                    combinedArgs.add(s);
                } else if (s.contains("\"")) {
                    final StringBuilder sb = new StringBuilder();
                    do {
                        sb.append(s).append(" ");
                        s = i.next();
                    } while (!s.contains("\""));
                    sb.append(s);
                    combinedArgs.add(sb.toString());
                } else {
                    combinedArgs.add(s);
                }
            }
        } catch (final NoSuchElementException e) {
            sender.sendMessage(ChatColor.RED + "Invalid input, check your quotes");
            return true;
        }

        int position = 0;

        Quality quality;
        ItemStack item;
        String name = null;
        String description = null;
        LinkedHashMap<Part, Integer> parts = new LinkedHashMap<Part, Integer>();
        Player target;

        if (combinedArgs.size() > position) {
            try {
                quality = Quality.valueOf(combinedArgs.get(position).toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Invalid quality");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Specify a weapon quality");
            return true;
        }

        position++;

        if (combinedArgs.size() > position) {
            if (combinedArgs.get(position).charAt(0) == '"') {
                if (!combinedArgs.get(position).equals("\"\"")) {
                    name = combinedArgs.get(position).substring(1, combinedArgs.get(position).length() - 1);
                }
                position++;
                if (combinedArgs.get(position).charAt(0) == '"') {
                    description = combinedArgs.get(position).substring(1, combinedArgs.get(position).length() - 1);
                    position++;
                }
            }
        }

        if (combinedArgs.size() > position) {
            String[] split = combinedArgs.get(position).split(":");
            try {
                item = new ItemStack(Integer.parseInt(split[0]));
                if (split.length > 1) {
                    item.setDurability(Short.parseShort(split[1]));
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "That's not a number");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Specify an item id to spawn");
            return true;
        }

        position++;

        if (combinedArgs.size() > position) {
            String[] split = combinedArgs.get(position).substring(1, combinedArgs.get(position).length() - 1).split(" ");
            for (String part : split) {
                try {
                    parts.put(Part.valueOf(part), 0);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid part");
                    return true;
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Specify at least one strange part to add");
            return true;
        }

        position++;

        if (combinedArgs.size() > position) {
            try {
                target = Util.getPlayer(combinedArgs.get(position), sender);
            } catch (BadPlayerMatchException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
        } else {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Specify a player to give the weapon to");
                return true;
            }
        }

        StrangeWeapon strange = new StrangeWeapon(item, Part.DAMAGE);
        strange.setQuality(quality);
        if (name != null) {
            strange.setCustomName(name);
        }
        if (description != null) {
            strange.setDescription(description);
        }
        strange.setParts(parts);

        ItemStack result = strange.getItemStack();

        target.getInventory().addItem(result);

        if (target.equals(sender)) {
            sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + "you " + ChatColor.GOLD + "a " + result.getItemMeta().getDisplayName());
        } else {
            sender.sendMessage(ChatColor.GOLD + "Given " + ChatColor.AQUA + target.getName() + ChatColor.GOLD + "a " + result.getItemMeta().getDisplayName());
        }

        return true;
    }
}