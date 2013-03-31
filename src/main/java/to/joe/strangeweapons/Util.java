package to.joe.strangeweapons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.exception.BadPlayerMatchException;

public class Util {

    public static StrangeWeapons plugin;

    public static Player getPlayer(String target, CommandSender searcher) throws BadPlayerMatchException {

        final List<Player> players = new ArrayList<Player>();

        for (final Player player : plugin.getServer().getOnlinePlayers()) {
            if (searcher instanceof Player && !((Player) searcher).canSee(player)) {
                continue;
            }
            if (player.getName().equalsIgnoreCase(target)) {
                return player;
            }
            if (player.getName().toLowerCase().contains(target.toLowerCase())) {
                players.add(player);
            }
        }
        if (players.size() > 1) {
            StringBuilder sb = new StringBuilder();
            for (Player player : players) {
                sb.append(player.getName());
                sb.append(", ");
            }
            sb.setLength(sb.length() - 2);
            throw new BadPlayerMatchException("Matches too many players (" + sb.toString() + ")");
        }
        if (players.size() == 0) {
            throw new BadPlayerMatchException("No players matched");
        }
        return players.get(0);
    }

    public static String toTitleCase(String string) {
        StringBuilder titleString = new StringBuilder();
        for (String s : string.split(" ")) {
            titleString.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }
        return titleString.substring(0, titleString.length() - 1);
    }

    public static String getWeaponName(int stat) {
        while (!plugin.config.weaponText.containsKey(stat)) {
            stat--;
            if (stat < 0)
                return "Sub-par";
        }
        return plugin.config.weaponText.get(stat);
    }

    public static String getWeaponName(ItemStack item, int stat) {
        return getWeaponName(stat) + " " + Util.toTitleCase(item.getType().toString().toLowerCase().replaceAll("_", " "));
    }
}