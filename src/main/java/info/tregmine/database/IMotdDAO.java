package info.tregmine.database;

public interface IMotdDAO {
    String getMotd() throws DAOException;

    String getUpdates(String version) throws DAOException;
}
