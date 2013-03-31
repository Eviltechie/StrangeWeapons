package to.joe.strangeweapons;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.strangeweapons.command.ContentsCommand;
import to.joe.strangeweapons.command.CratesCommand;
import to.joe.strangeweapons.command.DropsCommand;
import to.joe.strangeweapons.command.ListPartsCommand;
import to.joe.strangeweapons.command.ListQualitiesCommand;
import to.joe.strangeweapons.command.NewCrateCommand;
import to.joe.strangeweapons.command.NewDescriptionTagCommand;
import to.joe.strangeweapons.command.NewKeyCommand;
import to.joe.strangeweapons.command.NewNameTagCommand;
import to.joe.strangeweapons.command.NewPartCommand;
import to.joe.strangeweapons.command.PlaytimeCommand;
import to.joe.strangeweapons.command.SpawnStrangeCommand;
import to.joe.strangeweapons.command.StrangeCommand;
import to.joe.strangeweapons.command.TagCommand;
import to.joe.strangeweapons.datastorage.Cache;
import to.joe.strangeweapons.datastorage.DataStorageException;
import to.joe.strangeweapons.datastorage.DataStorageInterface;
import to.joe.strangeweapons.datastorage.MySQLDataStorage;
import to.joe.strangeweapons.datastorage.PlayerDropData;
import to.joe.strangeweapons.datastorage.YamlDataStorage;
import to.joe.strangeweapons.listener.DurabilityListener;
import to.joe.strangeweapons.listener.IncrementListener;
import to.joe.strangeweapons.meta.Crate;
import to.joe.strangeweapons.meta.StrangeWeapon;

public class StrangeWeapons extends JavaPlugin implements Listener { //TODO Setquality

    public Config config;
    public final Map<String, String> tags = new HashMap<String, String>();
    final Map<String, Long> joinTimes = new HashMap<String, Long>();
    public final Random random = new Random();
    private DataStorageInterface dataStorage;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("strange").setExecutor(new StrangeCommand());
        getCommand("newcrate").setExecutor(new NewCrateCommand(this));
        getCommand("newkey").setExecutor(new NewKeyCommand());
        getCommand("newpart").setExecutor(new NewPartCommand());
        getCommand("newnametag").setExecutor(new NewNameTagCommand());
        getCommand("newdescriptiontag").setExecutor(new NewDescriptionTagCommand());
        getCommand("tag").setExecutor(new TagCommand(this));
        getCommand("crates").setExecutor(new CratesCommand(this));
        getCommand("contents").setExecutor(new ContentsCommand());
        getCommand("drops").setExecutor(new DropsCommand(this));
        getCommand("playtime").setExecutor(new PlaytimeCommand(this));
        getCommand("listparts").setExecutor(new ListPartsCommand());
        getCommand("spawnstrange").setExecutor(new SpawnStrangeCommand());
        getCommand("listqualities").setExecutor(new ListQualitiesCommand());
        getServer().getPluginManager().registerEvents(this, this);

        for (String s : getConfig().getStringList("idstrings")) {
            StrangeWeapon.idStrings.add(s.replace("{#}", "([0-9]+)"));
        }

        try {
            String storageType = getConfig().getString("datastorage");
            if (storageType.equalsIgnoreCase("mysql")) {
                dataStorage = new Cache(this, new MySQLDataStorage(this, getConfig().getString("database.url"), getConfig().getString("database.username"), getConfig().getString("database.password")));
                getLogger().info("Using cached mySQL for datastorage");
            } else if (storageType.equalsIgnoreCase("mysql_nocache")) {
                dataStorage = new MySQLDataStorage(this, getConfig().getString("database.url"), getConfig().getString("database.username"), getConfig().getString("database.password"));
                getLogger().warning("Using uncached mySQL for datastorage. Expect poor performance!");
            } else if (storageType.equalsIgnoreCase("yaml")) {
                dataStorage = new YamlDataStorage(this);
                getLogger().info("Using yaml for datastorage");
            } else {
                getLogger().severe("No datastorage selected!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Error connecting to database", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error loading yaml file", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Crate.plugin = this;
        StrangeWeapon.plugin = this;
        PlayerDropData.plugin = this;
        Util.plugin = this;

        config = new Config(getConfig());

        if (!config.durability) {
            new DurabilityListener(this);
        }

        new IncrementListener(this);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new PlaytimeRecorder(this), 600, 1200);
    }

    @Override
    public void onDisable() {
        if (dataStorage instanceof Cache) {
            ((Cache) dataStorage).shutdown();
        }
        if (dataStorage instanceof YamlDataStorage) {
            ((YamlDataStorage) dataStorage).shutdown();
        }
    }

    public DataStorageInterface getDSI() {
        return dataStorage;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        joinTimes.put(event.getPlayer().getName(), System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        try {
            PlayerDropData data = dataStorage.getPlayerDropData(event.getPlayer().getName());
            data.setPlayTime(data.getPlayTime() + (int) ((System.currentTimeMillis() - joinTimes.get(event.getPlayer().getName())) / 1000));
            dataStorage.updatePlayerDropData(data);
            joinTimes.remove(event.getPlayer().getName());
        } catch (DataStorageException e) {
            getLogger().log(Level.SEVERE, "Error saving playtime on leave", e);
        }
    }
}

class PlaytimeRecorder implements Runnable {

    StrangeWeapons plugin;

    public PlaytimeRecorder(StrangeWeapons plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Entry<String, Long> time : plugin.joinTimes.entrySet()) {
            try {
                PlayerDropData data = plugin.getDSI().getPlayerDropData(time.getKey());
                data.setPlayTime(data.getPlayTime() + (int) ((System.currentTimeMillis() - time.getValue()) / 1000));
                if (data.getPlayTime() >= data.getNextItemDrop() && plugin.getDSI().itemCanDrop(data)) {
                    Map<ItemStack, Double> map = new HashMap<ItemStack, Double>();
                    if (plugin.getConfig().contains("drops")) {
                        ConfigurationSection cs = plugin.getConfig().getConfigurationSection("drops");
                        for (String item : cs.getKeys(false)) {
                            ConfigurationSection i = cs.getConfigurationSection(item);
                            map.put(i.getItemStack("item"), i.getDouble("weight"));
                        }
                    }

                    RandomCollection<ItemStack> rc = new RandomCollection<ItemStack>(plugin.random);
                    for (Entry<ItemStack, Double> i : map.entrySet()) {
                        rc.add(i.getValue(), i.getKey());
                    }

                    if (map.isEmpty()) {
                        plugin.getLogger().warning("There are no items that can be dropped!");
                    } else {
                        Player player = plugin.getServer().getPlayerExact(time.getKey());
                        if (player.hasPermission("strangeweapons.drop.dropitems")) { //Make sure they can receive items
                            ItemStack item = rc.next();
                            if (StrangeWeapon.isStrangeWeapon(item)) {
                                item = new StrangeWeapon(item).clone();
                            }
                            Map<Integer, ItemStack> fail = player.getInventory().addItem(item); //If the player has a full inventory, we skip this drop for them or drop it at their feet
                            if (plugin.config.dropAtFeet) {
                                for (ItemStack failedItem : fail.values()) {
                                    player.getWorld().dropItem(player.getLocation(), failedItem);
                                }
                            }
                            if (fail.isEmpty() || plugin.config.dropAtFeet) {
                                plugin.getDSI().recordDrop(player.getName(), item, false);
                                String lootName;
                                if (item.getItemMeta().hasDisplayName()) {
                                    lootName = item.getItemMeta().getDisplayName();
                                } else {
                                    lootName = ChatColor.YELLOW + Util.toTitleCase(item.getType().toString().toLowerCase().replaceAll("_", " "));
                                }
                                if (player.hasPermission("strangeweapons.drop.announceexempt")) { //If the player has this perm, we don't announce their drops in case they may be vanished
                                    player.sendMessage(ChatColor.GOLD + "You" + ChatColor.WHITE + " have found: " + ChatColor.YELLOW + lootName);
                                } else {
                                    plugin.getServer().broadcastMessage(player.getDisplayName() + ChatColor.WHITE + " has found: " + ChatColor.YELLOW + lootName);
                                }
                            } else {
                                player.sendMessage(ChatColor.GOLD + "TIP: " + ChatColor.AQUA + "Make sure you have at least one empty spot in your inventory to receive random drops!");
                            }
                        }
                    }

                    data.rollItem();
                }
                if (data.getPlayTime() >= data.getNextCrateDrop() && plugin.getDSI().crateCanDrop(data)) {
                    Set<String> allCrates;
                    if (plugin.getConfig().contains("crates")) {
                        allCrates = plugin.getConfig().getConfigurationSection("crates").getKeys(false);
                    } else {
                        allCrates = new HashSet<String>();
                    }
                    Iterator<String> i = allCrates.iterator();
                    while (i.hasNext()) {
                        String crate = i.next();
                        if (!plugin.getConfig().getBoolean("crates." + crate + ".drops")) {
                            i.remove();
                        }
                    }
                    if (allCrates.isEmpty()) {
                        plugin.getLogger().warning("There are no crates that can be dropped!");
                    } else {
                        ArrayList<String> crates = new ArrayList<String>(allCrates);
                        Collections.shuffle(crates);
                        ItemStack item = new Crate(Integer.parseInt(crates.get(0))).getItemStack();
                        Player player = plugin.getServer().getPlayerExact(time.getKey());
                        if (player.hasPermission("strangeweapons.drop.dropcrates")) { //Make sure the player can receive crates
                            Map<Integer, ItemStack> fail = player.getInventory().addItem(item); //If the player has a full inventory, we skip this drop for them or drop it at their feet
                            if (plugin.config.dropAtFeet) {
                                for (ItemStack failedItem : fail.values()) {
                                    player.getWorld().dropItem(player.getLocation(), failedItem);
                                }
                            }
                            if (fail.isEmpty() || plugin.config.dropAtFeet) {
                                plugin.getDSI().recordDrop(player.getName(), item, true);
                                if (player.hasPermission("strangeweapons.drop.announceexempt")) { //If the player has this perm, we don't announce their drops in case they may be vanished
                                    player.sendMessage(ChatColor.GOLD + "You" + ChatColor.WHITE + " have found: " + item.getItemMeta().getDisplayName());
                                } else {
                                    plugin.getServer().broadcastMessage(player.getDisplayName() + ChatColor.WHITE + " has found: " + item.getItemMeta().getDisplayName());
                                }
                            } else {
                                player.sendMessage(ChatColor.GOLD + "TIP: " + ChatColor.AQUA + "Make sure you have at least one empty spot in your inventory to receive random drops!");
                            }
                        }
                    }
                    data.rollCrate();
                }
                plugin.getDSI().updatePlayerDropData(data);
            } catch (DataStorageException e) {
                plugin.getLogger().log(Level.SEVERE, "Error reading/saving data for " + time.getKey(), e);
            }
        }
        long currentTime = System.currentTimeMillis();
        ArrayList<String> onlinePlayers = new ArrayList<String>();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            onlinePlayers.add(p.getName());
        }
        for (String player : plugin.joinTimes.keySet()) {
            if (onlinePlayers.contains(player)) {
                plugin.joinTimes.put(player, currentTime);
            } else {
                plugin.joinTimes.remove(player);
            }
        }
    }
}