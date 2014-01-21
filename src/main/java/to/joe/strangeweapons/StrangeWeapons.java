package to.joe.strangeweapons;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

public class StrangeWeapons extends JavaPlugin implements Listener {

    public static StrangeWeapons plugin;
    public Config config;
    public Map<String, Quality> qualities = new LinkedHashMap<String, Quality>(); //TODO Populate this
    public final Map<String, String> tags = new HashMap<String, String>();
    final Map<String, Long> joinTimes = new HashMap<String, Long>();
    public final Random random = new Random();
    private DataStorageInterface dataStorage;

    @Override
    public void onEnable() {
        plugin = this;
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
    }

    @Override
    public void onDisable() {
        plugin = null;
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