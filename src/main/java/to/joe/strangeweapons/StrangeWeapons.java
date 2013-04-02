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
import to.joe.strangeweapons.command.SetQualityCommand;
import to.joe.strangeweapons.command.SpawnStrangeCommand;
import to.joe.strangeweapons.command.StrangeCommand;
import to.joe.strangeweapons.command.TagCommand;
import to.joe.strangeweapons.datastorage.Cache;
import to.joe.strangeweapons.datastorage.DataStorageException;
import to.joe.strangeweapons.datastorage.DataStorageInterface;
import to.joe.strangeweapons.datastorage.MySQLDataStorage;
import to.joe.strangeweapons.datastorage.PlayerDropData;
import to.joe.strangeweapons.datastorage.YamlDataStorage;
import to.joe.strangeweapons.listener.DestructionListener;
import to.joe.strangeweapons.listener.DurabilityListener;
import to.joe.strangeweapons.listener.IncrementListener;
import to.joe.strangeweapons.listener.InventoryListener;
import to.joe.strangeweapons.meta.Crate;
import to.joe.strangeweapons.meta.StrangeWeapon;

public class StrangeWeapons extends JavaPlugin implements Listener {

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
        getCommand("setquality").setExecutor(new SetQualityCommand());
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

        if (config.itemDropRollMaxTime - config.itemDropRollMinTime < 1) {
            getLogger().severe("itemDropRollMaxTime must be greater than itemDropRollMinTime");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (config.crateDropRollMaxTime - config.crateDropRollMinTime < 1) {
            getLogger().severe("crateDropRollMaxTime must be greater than crateDropRollMinTime");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!config.durability) {
            new DurabilityListener(this);
        }

        new IncrementListener(this);
        new DestructionListener(this);
        new InventoryListener(this);

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
        for (Entry<String, Long> time : plugin.joinTimes.entrySet()) { //We loop through the list of players and their join times. This should be everybody on the server.
            try {
                PlayerDropData data = plugin.getDSI().getPlayerDropData(time.getKey()); //We get (or create) the dropdata for the player.
                data.setPlayTime(data.getPlayTime() + (int) ((System.currentTimeMillis() - time.getValue()) / 1000)); //We get the playtime, then add the current time minus the last update time.
                if (data.getPlayTime() >= data.getNextItemDrop() && plugin.getDSI().itemCanDrop(data)) { //If their playtime is greater than or equal to their next scheduled item drop AND they have not reached their item drop limit, we will attempt to drop an item.
                    Map<ItemStack, Double> map = new HashMap<ItemStack, Double>(); //We create a new map of items and their drop weights.
                    if (plugin.getConfig().contains("drops")) { //If the config contains a drops section...
                        ConfigurationSection cs = plugin.getConfig().getConfigurationSection("drops"); //We get the drops section...
                        for (String item : cs.getKeys(false)) { //And loop through it
                            ConfigurationSection i = cs.getConfigurationSection(item); //We get the CS that has the item and it's weight...
                            map.put(i.getItemStack("item"), i.getDouble("weight")); //And then add both to the map above
                        }
                    }

                    RandomCollection<ItemStack> rc = new RandomCollection<ItemStack>(plugin.random); //We create a new random collection for the items
                    for (Entry<ItemStack, Double> i : map.entrySet()) { //We loop through each item we read above
                        rc.add(i.getValue(), i.getKey()); //And add it and it's weight to the collection
                    }

                    if (map.isEmpty()) { //If the map above is empty, there is nothing that can be dropped...
                        plugin.getLogger().warning("There are no items that can be dropped!"); //So we throw a warning in console
                    } else { //The map is not empty
                        Player player = plugin.getServer().getPlayerExact(time.getKey()); //We fetch the player object from the outer loop
                        if (player.hasPermission("strangeweapons.drop.dropitems")) { //Make sure they can receive items
                            ItemStack item = rc.next(); //We select the item they will receive
                            if (StrangeWeapon.isStrangeWeapon(item)) { //If the item is strange...
                                item = new StrangeWeapon(item).clone(); //We must clone it
                            }
                            Map<Integer, ItemStack> fail = player.getInventory().addItem(item); //We attempt to give the item to the player. If the player has a full inventory, we skip this drop for them or drop it at their feet
                            if (plugin.config.dropAtFeet) { //If the config allows dropping at feet...
                                for (ItemStack failedItem : fail.values()) { //We will loop through the failed drops...
                                    player.getWorld().dropItem(player.getLocation(), failedItem); //And drop each at the player's location
                                }
                            }
                            if (fail.isEmpty() || plugin.config.dropAtFeet) { //If the fail map is empty or we dropped the fail at their feet, we know the drop was ok.
                                plugin.getDSI().recordDrop(player.getName(), item, false); //We will tell the DSI to record the player, their drop, and fact that it was not a crate 
                                String lootName; //Let's make a string for the item name so that we can announce the drop
                                if (item.getItemMeta().hasDisplayName()) { //If the item has a display name...
                                    lootName = item.getItemMeta().getDisplayName(); //We will use it
                                } else { //Otherwise...
                                    lootName = ChatColor.YELLOW + Util.toTitleCase(item.getType().toString().toLowerCase().replaceAll("_", " ")); //We will try to get the natural name of the item
                                }
                                if (player.hasPermission("strangeweapons.drop.announceexempt")) { //If the player has this perm, we don't broadcast their drops in case they may be vanished...
                                    player.sendMessage(ChatColor.GOLD + "You" + ChatColor.WHITE + " have found: " + ChatColor.YELLOW + lootName); //And we then only tell them privately
                                } else { //But if they don't have the perm
                                    plugin.getServer().broadcastMessage(player.getDisplayName() + ChatColor.WHITE + " has found: " + ChatColor.YELLOW + lootName); //We broadcast to the entire server!
                                }
                            } else { //Since we have fail items and we didn't drop at their feet, we know the drop did not work...
                                player.sendMessage(ChatColor.GOLD + "TIP: " + ChatColor.AQUA + "Make sure you have at least one empty spot in your inventory to receive random drops!"); //So we remind the player to have free space
                            }
                        }
                    }

                    data.rollItem(); //Regardless if they got the item or not, we will roll for their next item drop time should be (regardless if they have reached their limit)
                }
                if (data.getPlayTime() >= data.getNextCrateDrop() && plugin.getDSI().crateCanDrop(data)) { //If their playtime is greater than or equal to their next scheduled crate drop AND they have not reached their crate drop limit, we will attempt to drop a crate.
                    Set<String> allCrates; //We create a new set of crates
                    if (plugin.getConfig().contains("crates")) { //If the config contains a crates section...
                        allCrates = plugin.getConfig().getConfigurationSection("crates").getKeys(false); //We get the crates section...
                    } else { //Or if it doesn't...
                        allCrates = new HashSet<String>(); //We just make a new empty set
                    }
                    Iterator<String> i = allCrates.iterator(); //An iterator for looping through the crates
                    while (i.hasNext()) { //While there are crates left..
                        String crate = i.next(); //We get the crate...
                        if (!plugin.getConfig().getBoolean("crates." + crate + ".drops")) { //And if it set to not drop...
                            i.remove(); //We remove it
                        }
                    }
                    if (allCrates.isEmpty()) { //If the set above is empty, there is nothing that can be dropped...
                        plugin.getLogger().warning("There are no crates that can be dropped!"); //So we throw a warning in console
                    } else { //The set is not empty
                        ArrayList<String> crates = new ArrayList<String>(allCrates);
                        Collections.shuffle(crates);
                        ItemStack item = new Crate(Integer.parseInt(crates.get(0))).getItemStack();
                        Player player = plugin.getServer().getPlayerExact(time.getKey());
                        if (player.hasPermission("strangeweapons.drop.dropcrates")) { //Make sure the player can receive crates
                            Map<Integer, ItemStack> fail = player.getInventory().addItem(item); //If the player has a full inventory, we skip this drop for them or drop it at their feet
                            if (plugin.config.dropAtFeet) { //If the config allows dropping at feet...
                                for (ItemStack failedItem : fail.values()) { //We will loop through the failed drops...
                                    player.getWorld().dropItem(player.getLocation(), failedItem); //And drop each at the player's location
                                }
                            }
                            if (fail.isEmpty() || plugin.config.dropAtFeet) { //If the fail map is empty or we dropped the fail at their feet, we know the drop was ok.
                                plugin.getDSI().recordDrop(player.getName(), item, true); //We will tell the DSI to record the player, their drop, and fact that it was a crate 
                                if (player.hasPermission("strangeweapons.drop.announceexempt")) { //If the player has this perm, we don't broadcast their drops in case they may be vanished...
                                    player.sendMessage(ChatColor.GOLD + "You" + ChatColor.WHITE + " have found: " + item.getItemMeta().getDisplayName()); //And we then only tell them privately
                                } else { //But if they don't have the perm
                                    plugin.getServer().broadcastMessage(player.getDisplayName() + ChatColor.WHITE + " has found: " + item.getItemMeta().getDisplayName()); //We broadcast to the entire server!
                                }
                            } else { //Since we have fail items and we didn't drop at their feet, we know the drop did not work...
                                player.sendMessage(ChatColor.GOLD + "TIP: " + ChatColor.AQUA + "Make sure you have at least one empty spot in your inventory to receive random drops!"); //So we remind the player to have free space
                            }
                        }
                    }
                    data.rollCrate(); //Regardless if they got the item or not, we will roll for their next crate drop time should be (regardless if they have reached their limit)
                }
                plugin.getDSI().updatePlayerDropData(data); //Now that all the drop nonsense is done, we can update their data
            } catch (DataStorageException e) { //Just in case something goes wrong while updating...
                plugin.getLogger().log(Level.SEVERE, "Error reading/saving data for " + time.getKey(), e); //We'll spew the error into console
            }
        }
        long currentTime = System.currentTimeMillis(); //Let's get the current time so that we don't have to call this more than necessary
        ArrayList<String> onlinePlayers = new ArrayList<String>(); //Here we make a string list to store the names of players online 
        for (Player p : plugin.getServer().getOnlinePlayers()) { //Here we loop through the list of online players...
            onlinePlayers.add(p.getName()); //And then add their names to our list
        }
        Iterator<Entry<String, Long>> iter = plugin.joinTimes.entrySet().iterator(); //Here we create an iterator in preperation to loop though the list of players in our join times map. This is just in case something screwed up and the quit or join event failed.
        while (iter.hasNext()) { //Here we loop though the list of players in our join times map using the iterator
            Entry<String, Long> player = iter.next();
            if (onlinePlayers.contains(player.getKey())) { //If they are in our set...
                player.setValue(currentTime); //We update their join time with the current time
            } else { //And if they are not
                iter.remove(); //We remove them 
            }
        }
    }
}