package info.tregmine.database;

import java.util.List;

public interface IMiscDAO {
    List<String> loadBannedWords() throws DAOException;

    List<String> loadInsults() throws DAOException;

    List<String> loadQuitMessages() throws DAOException;
}
