package info.tregmine.api;

import info.tregmine.database.db.pojo.BankAccountPOJO;
import info.tregmine.database.db.pojo.BankTransactionPOJO;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Random;

/**
 * Represents a bank account holding information as to how much a player
 * currently has in a certain bank.
 *
 * @author Robert Catron
 * @since 12/7/2013
 */
public class Account extends BankAccountPOJO {

    private Bank bank;

    private long account_number;

    private boolean verified;

    public Account() {
        Random r = new Random(9);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(r.nextInt());
        }

        try {
            account_number = Integer.parseInt(String.valueOf(id) + sb.toString().trim());
        } catch (NumberFormatException e) {
        }
    }

    public long getAccountNumber() {
        return account_number;
    }

    public void setAccountNumber(long v) {
        this.account_number = v;
    }

    public long getBalance() {
        return this.accountBalance;
    }

    public void setBalance(long v) {
        this.accountBalance = v;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public ObjectId getId() {
        return this.id;
    }

    public void setId(ObjectId v) {
        this.id = v;
    }

    public String getPin() {
        return this.accountPin;
    }

    public void setPin(String pin) {
        this.accountPin = pin;
    }

    public ObjectId getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(ObjectId v) {
        this.playerId = v;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean v) {
        this.verified = v;
    }

}
