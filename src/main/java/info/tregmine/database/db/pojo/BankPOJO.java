package info.tregmine.database.db.pojo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eric on 6/2/17.
 */

@Entity(value = "bank", noClassnameStored = true)
public class BankPOJO {
    @Id
    protected ObjectId id;

    @Indexed
    protected ObjectId lotId;

    @Embedded
    protected List<BankAccountPOJO> bankAccounts = new ArrayList<>();
}
