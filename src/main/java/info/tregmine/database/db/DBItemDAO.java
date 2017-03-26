package info.tregmine.database.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import info.tregmine.Tregmine;
import info.tregmine.database.DAOException;
import info.tregmine.database.IItemDAO;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;

public class DBItemDAO implements IItemDAO {
	private Connection conn;

	public DBItemDAO(Connection conn) {
		this.conn = conn;
	}

	@Override
	public int getItemValue(int itemId, byte itemData) throws DAOException {
		String sql = "SELECT * FROM item WHERE item_id = ? AND item_data = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, itemId);
			stmt.setByte(2, itemData);
			stmt.execute();

			try (ResultSet rs = stmt.getResultSet()) {
				if (!rs.next()) {
					return 0;
				}

				return rs.getInt("item_value");
			}
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}
	}

	@Override
	public void repopulateDatabase() throws DAOException {
		try(PreparedStatement stmt = conn.prepareStatement("TRUNCATE item")) {
			stmt.execute();
		} catch (SQLException e) {
			throw new DAOException("TRUNCATE item", e);
		};
		String sql = "INSERT INTO item (item_id, item_data, item_name, enchantable) VALUES " +
				"(?, ?, ?, ?)";

		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			for(Material material : Material.values()){
				ItemStack stack = new ItemStack(material);
				String enchantable = "no";
				for(Enchantment enchant : Enchantment.values()){
					if(enchant.canEnchantItem(stack)){
						enchantable = "yes";
						break;
					}
				}
				String prettyName = material.name();
				prettyName = prettyName.replaceAll("_", " ")
				.toLowerCase();
				prettyName = StringUtils.capitalize(prettyName);
				stmt.setString(1, material.name());
				stmt.setInt(2, stack.getData().getData());
				stmt.setString(3, prettyName);
				stmt.setString(4, enchantable);
				stmt.execute();
				Tregmine.LOGGER.info("[DATABASE] Added " + material.name() + " to item table.");
			}
			Tregmine.LOGGER.info("[DATABASE] Database repopulated.");
		}catch(SQLException e){
			throw new DAOException(sql, e);
		}
	}
}