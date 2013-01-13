package to.joe.strangeweapons.datastorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.StrangeWeapons;

public class Cache implements DataStorageInterface {

    private StrangeWeapons plugin;
    private Map<Integer, WeaponData> cachedWeaponData = new HashMap<Integer, WeaponData>();
    private Map<String, PlayerDropData> cachedPlayerDropData = new HashMap<String, PlayerDropData>();
    private DataStorageInterface DSI;

    public void shutdown() {
        for (WeaponData data : cachedWeaponData.values()) {
            if (!data.isUpdated()) {
                try {
                    DSI.updateWeaponData(data);
                } catch (DataStorageException e) {
                    plugin.getLogger().log(Level.SEVERE, "Error writing weapon data cache to storage on shutdown", e);
                }
            }
        }
        for (PlayerDropData data : cachedPlayerDropData.values()) {
            if (!data.isUpdated()) {
                try {
                    DSI.updatePlayerDropData(data);
                } catch (DataStorageException e) {
                    plugin.getLogger().log(Level.SEVERE, "Error writing player data cache to storage on shutdown", e);
                }
            }
        }
    }

    private void writeUpdates() {
        final Set<WeaponData> weaponDataToUpdate = new HashSet<WeaponData>();
        final Set<PlayerDropData> playerDropDataToUpdate = new HashSet<PlayerDropData>();
        for (WeaponData data : cachedWeaponData.values()) {
            if (!data.isUpdated()) {
                weaponDataToUpdate.add(data.clone());
                data.setUpdated(true);
            }
        }
        for (PlayerDropData data : cachedPlayerDropData.values()) {
            if (!data.isUpdated()) {
                playerDropDataToUpdate.add(data.clone());
                data.setUpdated(true);
            }
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for (WeaponData data : weaponDataToUpdate) {
                    try {
                        DSI.updateWeaponData(data);
                        data.setUpdated(true);
                    } catch (DataStorageException e) {
                        plugin.getLogger().log(Level.SEVERE, "Error writing weapon data cache to storage", e);
                    }
                }
                for (PlayerDropData data : playerDropDataToUpdate) {
                    try {
                        DSI.updatePlayerDropData(data);
                        data.setUpdated(true);
                    } catch (DataStorageException e) {
                        plugin.getLogger().log(Level.SEVERE, "Error writing player data cache to storage", e);
                    }
                }
            }
        });
    }

    public Cache(StrangeWeapons plugin, DataStorageInterface dataInterface) {
        DSI = dataInterface;
        this.plugin = plugin;
        this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                writeUpdates();
            }
        }, 1200, 1200);
    }

    @Override
    public WeaponData getWeaponData(int id) throws DataStorageException {
        if (cachedWeaponData.containsKey(id)) {
            return cachedWeaponData.get(id);
        } else {
            return DSI.getWeaponData(id);
        }
    }

    @Override
    public WeaponData saveNewWeaponData(WeaponData data) throws DataStorageException {
        data = DSI.saveNewWeaponData(data);
        cachedWeaponData.put(data.getWeaponId(), data);
        return data;
    }

    @Override
    public void updateWeaponData(WeaponData data) throws DataStorageException {
        cachedWeaponData.put(data.getWeaponId(), data);
    }

    @Override
    public PlayerDropData getPlayerDropData(String player) throws DataStorageException {
        if (cachedPlayerDropData.containsKey(player)) {
            return cachedPlayerDropData.get(player);
        } else {
            return DSI.getPlayerDropData(player);
        }
    }

    @Override
    public void updatePlayerDropData(PlayerDropData data) throws DataStorageException {
        cachedPlayerDropData.put(data.getPlayer(), data);
    }

    @Override
    public boolean itemCanDrop(PlayerDropData data) throws DataStorageException {
        return DSI.itemCanDrop(data);
    }

    @Override
    public boolean crateCanDrop(PlayerDropData data) throws DataStorageException {
        return DSI.crateCanDrop(data);
    }

    @Override
    public void recordDrop(String player, ItemStack item, boolean isCrate) throws DataStorageException {
        DSI.recordDrop(player, item, isCrate);
    }

    @Override
    public boolean playerDropDataExists(String player) throws DataStorageException {
        for (String s : cachedPlayerDropData.keySet()) {
            if (s.equalsIgnoreCase(player)) {
                return true;
            }
        }
        return DSI.playerDropDataExists(player);
    }
}
