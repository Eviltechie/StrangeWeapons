package to.joe.strangeweapons.datastorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.Part;
import to.joe.strangeweapons.Quality;
import to.joe.strangeweapons.StrangeWeapons;

public class MySQLDataStorage implements DataStorageInterface
{

    private StrangeWeapons plugin;
    private Connection conn;

    private void initTable(String table) throws SQLException
    {
        final ResultSet tableExists = conn.getMetaData().getTables(null, null, table, null);
        if (!tableExists.first())
        {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(plugin.getResource(table + ".sql")));
            final StringBuilder builder = new StringBuilder();
            String next;
            try
            {
                while ((next = reader.readLine()) != null)
                {
                    builder.append(next);
                }
                getFreshPreparedStatementColdFromTheRefrigerator(builder.toString()).execute();
            }
            catch (final IOException e)
            {
                throw new SQLException("Could not load default table creation text", e);
            }
        }
    }

    public MySQLDataStorage(StrangeWeapons plugin, String url, String username, String password) throws SQLException
    {
        this.plugin = plugin;
        conn = DriverManager.getConnection(url, username, password);

        initTable("weapons");
        initTable("parts");
        initTable("dropdata");
        initTable("droprecords");
    }

    private PreparedStatement getFreshPreparedStatementColdFromTheRefrigerator(String query) throws SQLException
    {
        return conn.prepareStatement(query);
    }

    private PreparedStatement getFreshPreparedStatementWithGeneratedKeys(String query) throws SQLException
    {
        return conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    }

    public WeaponData getWeaponData(int id) throws DataStorageException
    {
        WeaponData data = new WeaponData();
        data.setWeaponId(id);

        try
        {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("SELECT * FROM weapons WHERE weaponid = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            rs.next();
            data.setQuality(Quality.valueOf(rs.getString(2)));
            data.setCustomName(rs.getString(3));
            data.setDescription(rs.getString(4));

            ps = getFreshPreparedStatementColdFromTheRefrigerator("SELECT * FROM parts WHERE weaponid = ? ORDER BY partorder");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            LinkedHashMap<Part, Integer> parts = new LinkedHashMap<Part, Integer>();
            while (rs.next())
            {
                parts.put(Part.valueOf(rs.getString(2)), rs.getInt(3));
            }
            data.setParts(parts);

        }
        catch (SQLException e)
        {
            throw new DataStorageException(e);
        }
        return data;
    }

    private void updateParts(int weaponID, LinkedHashMap<Part, Integer> parts) throws SQLException
    {
        PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("DELETE FROM parts WHERE weaponid = ?");
        ps.setInt(1, weaponID);
        ps.execute();

        if (parts != null)
        {
            int position = 0;
            for (Entry<Part, Integer> part : parts.entrySet())
            {
                ps = getFreshPreparedStatementColdFromTheRefrigerator("INSERT INTO parts (weaponid, part, stat, partorder) VALUES (?,?,?,?)");
                ps.setInt(1, weaponID);
                ps.setString(2, part.getKey().toString());
                ps.setInt(3, part.getValue());
                ps.setInt(4, position++);
                ps.execute();
            }
        }
    }

    public WeaponData saveNewWeaponData(WeaponData data) throws DataStorageException
    {
        try
        {
            PreparedStatement ps = getFreshPreparedStatementWithGeneratedKeys("INSERT INTO weapons (quality, customname, description) VALUES (?,?,?)");
            if (data.getQuality() == null)
            {
                ps.setNull(1, Types.VARCHAR);
            }
            else
            {
                ps.setString(1, data.getQuality().toString());
            }
            if (data.getCustomName() == null)
            {
                ps.setNull(2, Types.VARCHAR);
            }
            else
            {
                ps.setString(2, data.getCustomName());
            }
            if (data.getDescription() == null)
            {
                ps.setNull(3, Types.VARCHAR);
            }
            else
            {
                ps.setString(3, data.getDescription());
            }
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int weaponID = rs.getInt(1);
            data.setWeaponId(weaponID);

            updateParts(weaponID, data.getParts());

        }
        catch (SQLException e)
        {
            throw new DataStorageException(e);
        }
        return data;
    }

    public void updateWeaponData(WeaponData data) throws DataStorageException
    {
        try
        {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("UPDATE weapons SET quality = ?, customname = ?, description = ? WHERE weaponid = ?");
            ps.setString(1, data.getQuality().toString());
            if (data.getCustomName() == null)
            {
                ps.setNull(2, Types.VARCHAR);
            }
            else
            {
                ps.setString(2, data.getCustomName());
            }
            if (data.getDescription() == null)
            {
                ps.setNull(3, Types.VARCHAR);
            }
            else
            {
                ps.setString(3, data.getDescription());
            }
            ps.setInt(4, data.getWeaponId());
            ps.execute();
            updateParts(data.getWeaponId(), data.getParts());
        }
        catch (SQLException e)
        {
            throw new DataStorageException(e);
        }
    }

    public PlayerDropData getPlayerDropData(String player) throws DataStorageException
    {
        try
        {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("SELECT * FROM dropdata WHERE username = ?");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                return new PlayerDropData(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4));
            }
            else
            {
                ps = getFreshPreparedStatementColdFromTheRefrigerator("INSERT INTO dropdata (username, playtime, nextitemdrop, nextcratedrop) VALUES (?,0,0,0)");
                ps.setString(1, player);
                ps.execute();
                return new PlayerDropData(player, 0, 0, 0);
            }
        }
        catch (SQLException e)
        {
            throw new DataStorageException(e);
        }
    }

    public void updatePlayerDropData(PlayerDropData data) throws DataStorageException
    {
        try
        {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("UPDATE dropdata SET playtime = ?, nextitemdrop = ?, nextcratedrop = ? WHERE username = ?");
            ps.setInt(1, data.getPlayTime());
            ps.setInt(2, data.getNextItemDrop());
            ps.setInt(3, data.getNextCrateDrop());
            ps.setString(4, data.getPlayer());
            ps.execute();
        }
        catch (SQLException e)
        {
            throw new DataStorageException(e);
        }
    }

    public boolean itemCanDrop(PlayerDropData data) throws DataStorageException
    {
        try
        {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("SELECT count(*) FROM droprecords WHERE username = ? AND iscrate = 0 AND DATE_SUB(NOW(), INTERVAL "
                    + plugin.config.itemDropReset * 2 + " MINUTE)");
            ps.setString(1, data.getPlayer());
            ResultSet rs = ps.executeQuery();
            rs.next();
            int numDrops = rs.getInt(1);
            if (numDrops < plugin.config.itemDropLimit)
            {
                return true;
            }
        }
        catch (SQLException e)
        {
            throw new DataStorageException(e);
        }
        return false;
    }

    public boolean crateCanDrop(PlayerDropData data) throws DataStorageException
    {
        try
        {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("SELECT count(*) FROM droprecords WHERE username = ? AND iscrate = 1 AND DATE_SUB(NOW(), INTERVAL "
                    + plugin.config.crateDropReset * 2 + " MINUTE)");
            ps.setString(1, data.getPlayer());
            ResultSet rs = ps.executeQuery();
            rs.next();
            int numDrops = rs.getInt(1);
            if (numDrops < plugin.config.crateDropLimit)
            {
                return true;
            }
        }
        catch (SQLException e)
        {
            throw new DataStorageException(e);
        }
        return false;
    }

    public void recordDrop(String player, ItemStack item, boolean isCrate) throws DataStorageException
    {
        try
        {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("INSERT INTO droprecords (username, itemdropped, iscrate) VALUES (?,?,?)");
            ps.setString(1, player);
            ps.setString(2, item.serialize().toString());
            ps.setBoolean(3, isCrate);
            ps.execute();
        }
        catch (SQLException e)
        {
            throw new DataStorageException(e);
        }
    }

    public boolean playerDropDataExists(String player) throws DataStorageException
    {
        try
        {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("SELECT count(*) FROM dropdata WHERE username = ?");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0)
            {
                return true;
            }
        }
        catch (SQLException e)
        {
            throw new DataStorageException(e);
        }
        return false;
    }
}
