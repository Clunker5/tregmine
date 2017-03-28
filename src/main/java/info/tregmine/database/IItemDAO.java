package info.tregmine.database;

import org.bukkit.Material;

public interface IItemDAO {
    int getItemValue(Material item, byte itemData) throws DAOException;

    void repopulateDatabase() throws DAOException;
}
