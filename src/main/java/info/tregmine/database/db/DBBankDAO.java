package info.tregmine.database.db;

import com.google.common.collect.Lists;
import info.tregmine.Tregmine;
import info.tregmine.api.Account;
import info.tregmine.api.Bank;
import info.tregmine.api.Transaction;
import info.tregmine.database.DAOException;
import info.tregmine.database.IBankDAO;
import info.tregmine.database.db.pojo.BankAccountPOJO;
import info.tregmine.database.db.pojo.BankPOJO;
import info.tregmine.database.db.pojo.BankTransactionPOJO;
import info.tregmine.database.db.pojo.PlayerPOJO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DBBankDAO extends BasicDAO<BankPOJO, String> implements IBankDAO {
    private Tregmine plugin;
    private Random r;

    public DBBankDAO(Class<BankPOJO> entityClass, Datastore ds, Tregmine plguin) {
        super(entityClass, ds);
        this.r = new Random();
    }

    @Override
    public void createAccount(Account acct) throws DAOException {
        acct.setAccountNumber(this.getMaxxAccountNr());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            stringBuilder.append(r.nextInt(10));
        }
        acct.setPin(stringBuilder.toString());
        acct.getBank().addAccount(acct);
        acct.setId((ObjectId) this.save(this.serialize(acct.getBank())).getId());
    }

    @Override
    public ObjectId createBank(Bank bank) throws DAOException {
        return (ObjectId) this.save(this.serialize(bank)).getId();
    }

    @Override
    public void deleteBank(Bank bank) throws DAOException {
        this.deleteById(bank.getId());
    }

    @Override
    public void deposit(Bank bank, Account acct, int playerId, long amount) throws DAOException {

    }

    @Override
    public Account getAccount(Bank bank, int accNumber) throws DAOException {
        return null;
    }

    @Override
    public Account getAccountByPlayer(Bank bank, int playerId) throws DAOException {
        return null;
    }

    private List<Account> serializeAccounts(BankPOJO bankPOJO) {
        List<Account> accountsList = new ArrayList<>();
        for(BankAccountPOJO bankAccountPOJO : bankPOJO.bankAccounts) {
            Account account = new Account();
            
        }
    }

    @Override
    public Bank getBank(String bankId) throws DAOException {
        BankPOJO bankPOJO = this.get(bankId);
        if (bankPOJO == null) return null;
        Bank bank = new Bank(bankPOJO.lotId.toString());
        bank.setId(bankPOJO.id.toString());

    }

    @Override
    public void setPin(Account acct, String pin) throws DAOException {

    }

    @Override
    public boolean withdraw(Bank bank, Account acct, int playerId, long amount) throws DAOException {
        return false;
    }

    private long getMaxxAccountNr() {
        Account bankAccount = (Account) this.getDatastore().find(BankAccountPOJO.class).order("-accountNumber").get(new FindOptions().limit(1));
        if (bankAccount != null) return bankAccount.getAccountNumber();
        return 0;
    }

    private BankPOJO serialize(Bank bank) {
        BankPOJO bankPOJO = new BankPOJO();
        bankPOJO.id = new ObjectId(bank.getId());
        bankPOJO.lotId = new ObjectId(bank.getLotId());
        //Awkward casting but it works
        bankPOJO.bankAccounts = Arrays.asList((BankAccountPOJO[]) bank.getAccounts().toArray());
        return bankPOJO;
    }
}
