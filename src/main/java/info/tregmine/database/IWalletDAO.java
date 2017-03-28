package info.tregmine.database;

import info.tregmine.api.TregminePlayer;

public interface IWalletDAO {
    boolean add(TregminePlayer player, long amount) throws DAOException;

    long balance(TregminePlayer player) throws DAOException;

    String formattedBalance(TregminePlayer player) throws DAOException;

    void insertTransaction(int srcId, int recvId, int amount) throws DAOException;

    boolean take(TregminePlayer player, long amount) throws DAOException;
}
