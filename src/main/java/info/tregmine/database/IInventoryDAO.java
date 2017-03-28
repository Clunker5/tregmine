package info.tregmine.database;

import info.tregmine.api.InventoryAccess;
import info.tregmine.api.TregminePlayer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IInventoryDAO {
    void createInventory(TregminePlayer player, String inventoryName, String type) throws DAOException;

    int fetchInventory(TregminePlayer player, String inventoryName, String type) throws DAOException;

    List<InventoryAccess> getAccessLog(int inventoryId, int count) throws DAOException;

    int getInventoryId(int playerId, InventoryType type) throws DAOException;

    int getInventoryId(Location loc) throws DAOException;

    ItemStack[] getStacks(int inventoryId, int size) throws DAOException;

    void insertAccessLog(TregminePlayer player, int inventoryId) throws DAOException;

    void insertChangeLog(TregminePlayer player, int inventoryId, int slot, ItemStack slotContent,
                         ChangeType type) throws DAOException;

    int insertInventory(TregminePlayer player, Location loc, InventoryType type) throws DAOException;

    void insertStacks(int inventoryId, ItemStack[] contents) throws DAOException;

    void loadInventory(TregminePlayer player, int inventoryID, String type) throws DAOException;

    void saveInventory(TregminePlayer player, int inventoryID, String type) throws DAOException;

    enum ChangeType {
        ADD, REMOVE
    }

    enum InventoryType {
        BLOCK, PLAYER, PLAYER_ARMOR
    }

}
