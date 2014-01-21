package to.joe.strangeweapons;

import org.bukkit.ChatColor;

public class Quality {

    private ChatColor color;
    private String prefix;

    public Quality(ChatColor color, String prefix) {
        this.color = color;
        this.prefix = prefix;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getPrefix() {
        return prefix;
    }

    //TODO Hardcode these in
    /*NORMAL(ChatColor.RESET + ""),
    UNIQUE(ChatColor.YELLOW + ""),
    VINTAGE(ChatColor.DARK_AQUA + "Vintage "),
    GENUINE(ChatColor.DARK_GREEN + "Genuine "),
    STRANGE(ChatColor.GOLD + ""),
    UNUSUAL(ChatColor.LIGHT_PURPLE + "Unusual "),
    HAUNTED(ChatColor.AQUA + "Haunted "),
    COMMUNITY(ChatColor.GREEN + "Community "),
    SELFMADE(ChatColor.GREEN + "Self-Made "),
    MOJANG(ChatColor.DARK_PURPLE + "Mojang ");*/

}