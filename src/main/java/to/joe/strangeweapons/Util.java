package to.joe.strangeweapons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
}