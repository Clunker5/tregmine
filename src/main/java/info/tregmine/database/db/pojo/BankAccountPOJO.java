package info.tregmine.database.db.pojo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import java.util.ArrayList;
import java.util.List;

@Entity(value = "bank_account", noClassnameStored = true)
public class BankAccountPOJO {
    @Id
    protected ObjectId id;

    @Indexed
    protected ObjectId bankId = null;

    @Indexed
    protected ObjectId playerId = null;

    @Indexed
    protected int accountNumber = 0;

    @Embedded
    protected List<BankTransactionPOJO> transactions = new ArrayList<>();

    protected long accountBalance = 0;

    protected String accountPin = null;


}
