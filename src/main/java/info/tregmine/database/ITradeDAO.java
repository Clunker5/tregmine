package info.tregmine.database;

import org.bukkit.inventory.ItemStack;

public interface ITradeDAO {
    int getAmountofTrades(int id) throws DAOException;

    void insertStacks(int tradeId, ItemStack[] contents) throws DAOException;

    int insertTrade(int srcId, int recvId, int amount) throws DAOException;
}
