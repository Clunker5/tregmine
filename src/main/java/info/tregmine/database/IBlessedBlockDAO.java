package info.tregmine.database;

import info.tregmine.api.TregminePlayer;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.Map;

public interface IBlessedBlockDAO {
    void delete(Location loc) throws DAOException;

    void insert(TregminePlayer player, Location loc) throws DAOException;

    Map<Location, Integer> load(Server server) throws DAOException;

    int owner(Location loc) throws DAOException;
}
