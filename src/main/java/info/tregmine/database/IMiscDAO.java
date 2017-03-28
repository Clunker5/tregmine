package info.tregmine.database;

import org.bukkit.Location;

import java.util.List;

public interface IMiscDAO {
    /*
     * I don't know where else to put this
     */
    boolean blocksWereChanged(Location start, int radius) throws DAOException;

    List<String> loadBannedWords() throws DAOException;

    List<String> loadInsults() throws DAOException;

    List<String> loadQuitMessages() throws DAOException;
}
