package to.joe.strangeweapons.datastorage;

import org.bukkit.inventory.ItemStack;

public interface DataStorageInterface {

    /**
     * Gets the {@link WeaponData} with the specified id from the database
     * 
     * @param id
     *            the id from the database
     * @return the specified {@link WeaponData}
     */
    public WeaponData getWeaponData(int id) throws DataStorageException;

    /**
     * Stores {@link WeaponData} in the database for the first time
     * 
     * @param data
     *            the data to insert
     * @return the new data with an updated id
     */
    public WeaponData saveNewWeaponData(WeaponData data) throws DataStorageException;

    /**
     * Updates the {@link WeaponData} stored to the one provided. The weaponid will not be updated.
     * 
     * @param data
     *            The data to update
     * @throws DataStorageException
     */
    public void updateWeaponData(WeaponData data) throws DataStorageException;

    /**
     * Gets the {@link PlayerDropData} for the specified player from the database. Creates it if it does not exist.
     * 
     * @param player
     *            The player whose data should be fetched
     * @return
     * @throws DataStorageException
     */
    public PlayerDropData getPlayerDropData(String player) throws DataStorageException;

    /**
     * Checks to see if a {@link PlayerDropData} exists for the provided player
     * 
     * @param player
     * @return true if data exists
     * @throws DataStorageException
     */
    public boolean playerDropDataExists(String player) throws DataStorageException;

    /**
     * Updates the specified {@link PlayerDropData}
     * 
     * @param data
     *            The data to update
     * @throws DataStorageException
     */
    public void updatePlayerDropData(PlayerDropData data) throws DataStorageException;

    /**
     * Checks if the player has reached their item drop limit and thus if an item <b>can</b> drop.
     * Does not check if an item <b>should</b> drop.
     * 
     * @param data
     * @return
     */
    public boolean itemCanDrop(PlayerDropData data) throws DataStorageException;

    /**
     * Checks if the player has reached their crate drop limit and thus if a crate can drop.
     * Does not check if a crate should drop.
     * 
     * @param data
     * @return
     */
    public boolean crateCanDrop(PlayerDropData data) throws DataStorageException;

    /**
     * Record that a player received a random item drop
     * 
     * @param player
     * @param item
     * @param isCrate
     * @throws DataStorageException
     */
    public void recordDrop(String player, ItemStack item, boolean isCrate) throws DataStorageException;

}
