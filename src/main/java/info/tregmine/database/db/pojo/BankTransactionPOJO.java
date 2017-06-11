package info.tregmine.database.db.pojo;

import info.tregmine.api.Transaction;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

@Entity(value = "bank_transaction", noClassnameStored = true)
public class BankTransactionPOJO {
    @Id
    public ObjectId id;

    @Indexed
    public ObjectId accountId;

    @Indexed
    public ObjectId playerId;

    public Transaction type;

    public int transactionAmount;

    public int timestamp;
}
