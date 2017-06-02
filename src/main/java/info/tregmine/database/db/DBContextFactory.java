package info.tregmine.database.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import info.tregmine.Tregmine;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IContextFactory;
import info.tregmine.database.db.pojo.PlayerPOJO;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.dbcp.BasicDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DBContextFactory implements IContextFactory {
    private BasicDataSource ds;
    private Map<String, LoggingConnection.LogEntry> queryLog;
    private Tregmine plugin;
    private MongoClient mongoClient;
    private Morphia morphia;
    private Datastore datastore;
    private String driver;
    private String url;
    private String user;
    private String password;

    public DBContextFactory(FileConfiguration config, Tregmine instance) {
        this.plugin = instance;

        queryLog = new HashMap<>();

        String driver = config.getString("db.driver");
        if (driver == null) {
            driver = "com.mysql.jdbc.Driver";
        }

        try {
            Class.forName(driver).newInstance();
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        }

        String user = config.getString("db.user");
        String password = config.getString("db.password");
        String url = config.getString("db.url");

        ds = new BasicDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setMaxActive(5);
        ds.setMaxIdle(5);
        ds.setDefaultAutoCommit(true);

        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;

        this.mongoClient = new MongoClient(config.getString("mongo.ip"), config.getInt("mongo.port"));
        this.morphia = new Morphia();
        this.morphia.map
                (
                    PlayerPOJO.class
                );
        this.datastore = this.morphia.createDatastore(this.mongoClient, config.getString("mongo.database"));
        this.datastore.ensureIndexes();
    }

    @Override
    public IContext createContext() throws DAOException {
        try {
            // It's the responsibility of the context to make sure that the
            // connection is correctly closed
            Connection conn = ds.getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET NAMES latin1");
            }

            return new DBContext(this.datastore, this.plugin, conn);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }
}
