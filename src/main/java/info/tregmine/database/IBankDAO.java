package info.tregmine.database;

import info.tregmine.api.Account;
import info.tregmine.api.Bank;

import java.util.List;

public interface IBankDAO {
    void createAccount(Account acct, int playerId) throws DAOException;

    int createBank(Bank bank) throws DAOException;

    void deleteBank(Bank bank) throws DAOException;

    void deposit(Bank bank, Account acct, int playerId, long amount) throws DAOException;

    Account getAccount(Bank bank, int accNumber) throws DAOException;

    Account getAccountByPlayer(Bank bank, int playerId) throws DAOException;

    List<Account> getAccounts(Bank bank) throws DAOException;

    Bank getBank(int bankId) throws DAOException;

    void setPin(Account acct, String pin) throws DAOException;

    boolean withdraw(Bank bank, Account acct, int playerId, long amount) throws DAOException;
}
