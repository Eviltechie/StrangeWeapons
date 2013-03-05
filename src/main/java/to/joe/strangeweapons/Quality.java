package to.joe.strangeweapons;

import org.bukkit.ChatColor;

public enum Quality {

    NORMAL(ChatColor.RESET + ""),
    UNIQUE(ChatColor.YELLOW + ""),
    VINTAGE(ChatColor.DARK_AQUA + "Vintage "),
    GENUINE(ChatColor.DARK_GREEN + "Genuine "),
    STRANGE(ChatColor.GOLD + ""),
    UNUSUAL(ChatColor.LIGHT_PURPLE + "Unusual "),
    HAUNTED(ChatColor.AQUA + "Haunted "),
    COMMUNITY(ChatColor.GREEN + "Community "),
    SELFMADE(ChatColor.GREEN + "Self-Made "),
    MOJANG(ChatColor.DARK_PURPLE + "Mojang ");

    private String prefix;

    private Quality(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}
