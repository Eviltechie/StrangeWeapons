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

import org.bukkit.inventory.ItemStack;

import to.joe.strangeweapons.StrangeWeapons;

public class MySQLDataStorage implements DataStorageInterface {

    private StrangeWeapons plugin;
    private Connection conn;

    private void initTable(String table) throws SQLException {
        final ResultSet tableExists = conn.getMetaData().getTables(null, null, table, null);
        if (!tableExists.first()) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(plugin.getResource(table + ".sql")));
            final StringBuilder builder = new StringBuilder();
            String next;
            try {
                while ((next = reader.readLine()) != null) {
                    builder.append(next);
                }
                getFreshPreparedStatementColdFromTheRefrigerator(builder.toString()).execute();
            } catch (final IOException e) {
                throw new SQLException("Could not load default table creation text", e);
            }
        }
    }

    public MySQLDataStorage(StrangeWeapons plugin, String url, String username, String password) throws SQLException {
        this.plugin = plugin;
        conn = DriverManager.getConnection(url, username, password);

        initTable("dropdata");
        initTable("droprecords");
    }

    private PreparedStatement getFreshPreparedStatementColdFromTheRefrigerator(String query) throws SQLException {
        return conn.prepareStatement(query);
    }

    private PreparedStatement getFreshPreparedStatementWithGeneratedKeys(String query) throws SQLException {
        return conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public PlayerDropData getPlayerDropData(String player) throws DataStorageException {
        try {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("SELECT * FROM dropdata WHERE username = ?");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new PlayerDropData(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4));
            } else {
                ps = getFreshPreparedStatementColdFromTheRefrigerator("INSERT INTO dropdata (username, playtime, nextitemdrop, nextcratedrop) VALUES (?,0,0,0)");
                ps.setString(1, player);
                ps.execute();
                return new PlayerDropData(player, 0, 0, 0);
            }
        } catch (SQLException e) {
            throw new DataStorageException(e);
        }
    }

    @Override
    public void updatePlayerDropData(PlayerDropData data) throws DataStorageException {
        try {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("UPDATE dropdata SET playtime = ?, nextitemdrop = ?, nextcratedrop = ? WHERE username = ?");
            ps.setInt(1, data.getPlayTime());
            ps.setInt(2, data.getNextItemDrop());
            ps.setInt(3, data.getNextCrateDrop());
            ps.setString(4, data.getPlayer());
            ps.execute();
        } catch (SQLException e) {
            throw new DataStorageException(e);
        }
    }

    @Override
    public boolean itemCanDrop(PlayerDropData data) throws DataStorageException {
        try {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("SELECT count(*) FROM droprecords WHERE username = ? AND iscrate = 0 AND DATE_SUB(NOW(), INTERVAL " + plugin.config.itemDropReset * 2 + " MINUTE)");
            ps.setString(1, data.getPlayer());
            ResultSet rs = ps.executeQuery();
            rs.next();
            int numDrops = rs.getInt(1);
            if (numDrops < plugin.config.itemDropLimit) {
                return true;
            }
        } catch (SQLException e) {
            throw new DataStorageException(e);
        }
        return false;
    }

    @Override
    public boolean crateCanDrop(PlayerDropData data) throws DataStorageException {
        try {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("SELECT count(*) FROM droprecords WHERE username = ? AND iscrate = 1 AND DATE_SUB(NOW(), INTERVAL " + plugin.config.crateDropReset * 2 + " MINUTE)");
            ps.setString(1, data.getPlayer());
            ResultSet rs = ps.executeQuery();
            rs.next();
            int numDrops = rs.getInt(1);
            if (numDrops < plugin.config.crateDropLimit) {
                return true;
            }
        } catch (SQLException e) {
            throw new DataStorageException(e);
        }
        return false;
    }

    @Override
    public void recordDrop(String player, ItemStack item, boolean isCrate) throws DataStorageException {
        try {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("INSERT INTO droprecords (username, itemdropped, iscrate) VALUES (?,?,?)");
            ps.setString(1, player);
            ps.setString(2, item.serialize().toString());
            ps.setBoolean(3, isCrate);
            ps.execute();
        } catch (SQLException e) {
            throw new DataStorageException(e);
        }
    }

    @Override
    public boolean playerDropDataExists(String player) throws DataStorageException {
        try {
            PreparedStatement ps = getFreshPreparedStatementColdFromTheRefrigerator("SELECT count(*) FROM dropdata WHERE username = ?");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new DataStorageException(e);
        }
        return false;
    }
}
