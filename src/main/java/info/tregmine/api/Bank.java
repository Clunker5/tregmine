package info.tregmine.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import info.tregmine.database.db.pojo.BankPOJO;
import org.bson.types.ObjectId;

import java.util.*;

/**
 * @author Robert Catron
 * @see Account
 * @since 12/7/2013
 */
public class Bank extends BankPOJO {

    public Bank() {
    }

    public Bank(String lotId) {
        this.lotId = lotId;
    }

    public List<Account> getAccounts() {
        return this.bankAccounts;
    }

    public void setAccounts(List<Account> accounts) {
        accounts.forEach(account -> this.accounts.put(account.getId(), account));
    }

    public ObjectId getId() {
        return this.id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getLotId() {
        return lotId;
    }

    public void setLotId(ObjectId id) {
        this.lotId = id;
    }

    public void addAccount(Account a) {
        this.bankAccounts.add(a);
    }
}
