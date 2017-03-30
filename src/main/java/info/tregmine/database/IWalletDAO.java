package info.tregmine.database;

import info.tregmine.api.GenericPlayer;

public interface IWalletDAO {
    boolean add(GenericPlayer player, long amount) throws DAOException;

    long balance(GenericPlayer player) throws DAOException;

    String formattedBalance(GenericPlayer player) throws DAOException;

    void insertTransaction(int srcId, int recvId, int amount) throws DAOException;

    boolean take(GenericPlayer player, long amount) throws DAOException;
}
