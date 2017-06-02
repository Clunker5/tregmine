package info.tregmine.database.db.pojo;

import info.tregmine.api.Rank;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;

@Entity(value = "players", noClassnameStored = true)
public class PlayerPOJO {
    @Id
    public ObjectId id;

    @Indexed(options = @IndexOptions(unique = true))
    public String uuid;

    @Indexed
    public String username;

    public String password = null;

    public String email = null;

    public boolean confirmed = false;

    public long wallet = 15000;

    public Rank rank = Rank.UNVERIFIED;

    public int flags;

    public String properties = null;

    public String keywords = null;

    public String ignore = null;

    public String inventory = null;
}
