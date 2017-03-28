package info.tregmine.database.db;

import info.tregmine.database.DAOException;
import info.tregmine.database.IBlockDAO;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBBlockDAO implements IBlockDAO {
    private Connection conn;

    public DBBlockDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public int blockValue(Block a) throws DAOException {
        String sql = "SELECT * FROM item WHERE item_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, a.getType().name());
            stmt.execute();
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                return rs.getInt("mine_value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    @Override
    public Map<Material, Integer> loadBlockMinePrices() {
        Map<Material, Integer> prices = new HashMap<>();
        String sql = "SELECT DISTINCT `item_id`,`mine_value` FROM item";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                prices.put(Material.getMaterial(rs.getString("item_id")), rs.getInt("mine_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prices;
    }

}
