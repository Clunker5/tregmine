package info.tregmine.database;

import info.tregmine.api.FishyBlock;
import info.tregmine.api.GenericPlayer;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.Map;

public interface IFishyBlockDAO {
    void delete(FishyBlock fishyBlock) throws DAOException;

    void insert(FishyBlock fishyBlock) throws DAOException;

    void insertCostChange(FishyBlock fishyBlock, int oldCost) throws DAOException;

    void insertTransaction(FishyBlock fishyBlock, GenericPlayer player, TransactionType type, int amount)
            throws DAOException;

    Map<Location, FishyBlock> loadFishyBlocks(Server server) throws DAOException;

    void update(FishyBlock fishyBlock) throws DAOException;

    enum TransactionType {
        DEPOSIT, WITHDRAW, BUY
    }
}
