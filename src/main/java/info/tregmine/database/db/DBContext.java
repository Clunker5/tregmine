package info.tregmine.database.db;

import info.tregmine.Tregmine;
import info.tregmine.database.*;
import info.tregmine.database.db.pojo.PlayerPOJO;
import org.mongodb.morphia.Datastore;

import java.sql.Connection;
import java.sql.SQLException;

public class DBContext implements IContext {
    private Connection conn;
    private Datastore datastore;
    private Tregmine plugin;
    private DBPlayerDAO playerDAO;

    public DBContext(Datastore datastore, Tregmine instance, Connection conn) {
        this.datastore = datastore;
        this.plugin = instance;
        this.playerDAO = new DBPlayerDAO(PlayerPOJO.class, datastore, this.plugin);
        this.conn = conn;
    }

    @Override
    public IBankDAO getBankDAO() {
        return new DBBankDAO(conn);
    }

    @Override
    public IBlessedBlockDAO getBlessedBlockDAO() {
        return new DBBlessedBlockDAO(conn);
    }

    @Override
    public IBlockDAO getBlockDAO() {
        return new DBBlockDAO(conn);
    }

    public Connection getConnection() {
        return conn;
    }

    @Override
    public IDiscordDAO getDiscordDAO() {
        return new DBDiscordDAO(conn);
    }

    @Override
    public IEnchantmentDAO getEnchantmentDAO() {
        return new DBEnchantmentDAO(conn);
    }

    @Override
    public IFishyBlockDAO getFishyBlockDAO() {
        return new DBFishyBlockDAO(conn);
    }

    @Override
    public IHandbookDAO getHandbookDAO() {
        return new DBHandbookDAO(conn);
    }

    @Override
    public IHomeDAO getHomeDAO() {
        return new DBHomeDAO(conn);
    }

    @Override
    public IInventoryDAO getInventoryDAO() {
        return new DBInventoryDAO(conn);
    }

    @Override
    public IInviteDAO getInviteDAO() {
        return new DBInviteDAO(conn);
    }

    @Override
    public IItemDAO getItemDAO() {
        return new DBItemDAO(conn);
    }

    @Override
    public ILogDAO getLogDAO() {
        return new DBLogDAO(conn);
    }

    @Override
    public IMailDAO getMailDAO() {
        return new DBMailDAO(conn, this.plugin);
    }

    @Override
    public IMentorLogDAO getMentorLogDAO() {
        return new DBMentorLogDAO(conn);
    }

    @Override
    public IMiscDAO getMiscDAO() {
        return new DBMiscDAO(conn);
    }

    @Override
    public IMotdDAO getMotdDAO() {
        return new DBMotdDAO(conn);
    }

    @Override
    public IStaffNewsDAO getNewsByUploader() {
        return new DBNewsDAO(conn);
    }

    @Override
    public IPlayerDAO getPlayerDAO() {
        return this.playerDAO;
    }

    @Override
    public IPlayerReportDAO getPlayerReportDAO() {
        return new DBPlayerReportDAO(conn);
    }

    @Override
    public ITradeDAO getTradeDAO() {
        return new DBTradeDAO(conn);
    }

    @Override
    public IWalletDAO getWalletDAO() {
        return new DBWalletDAO(conn);
    }

    @Override
    public IWarpDAO getWarpDAO() {
        return new DBWarpDAO(conn);
    }

    @Override
    public IZonesDAO getZonesDAO() {
        return new DBZonesDAO(conn);
    }

    @Override
    public void close() {

    }
}
