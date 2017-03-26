package info.tregmine.database;

import org.bukkit.Material;

public interface IItemDAO {
	public int getItemValue(int itemId, byte itemData) throws DAOException;
	public void repopulateDatabase() throws DAOException;
}
