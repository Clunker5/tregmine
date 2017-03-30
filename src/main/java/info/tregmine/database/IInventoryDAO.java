package info.tregmine.database;

import info.tregmine.api.GenericPlayer;
import info.tregmine.api.InventoryAccess;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IInventoryDAO {
    void createInventory(GenericPlayer player, String inventoryName, String type) throws DAOException;

    int fetchInventory(GenericPlayer player, String inventoryName, String type) throws DAOException;

    List<InventoryAccess> getAccessLog(int inventoryId, int count) throws DAOException;

    int getInventoryId(int playerId, InventoryType type) throws DAOException;

    int getInventoryId(Location loc) throws DAOException;

    ItemStack[] getStacks(int inventoryId, int size) throws DAOException;

    void insertAccessLog(GenericPlayer player, int inventoryId) throws DAOException;

    void insertChangeLog(GenericPlayer player, int inventoryId, int slot, ItemStack slotContent,
                         ChangeType type) throws DAOException;

    int insertInventory(GenericPlayer player, Location loc, InventoryType type) throws DAOException;

    void insertStacks(int inventoryId, ItemStack[] contents) throws DAOException;

    void loadInventory(GenericPlayer player, int inventoryID, String type) throws DAOException;

    void saveInventory(GenericPlayer player, int inventoryID, String type) throws DAOException;

    enum ChangeType {
        ADD, REMOVE
    }

    enum InventoryType {
        BLOCK, PLAYER, PLAYER_ARMOR
    }

}
