package info.tregmine.database;

import info.tregmine.api.TregminePlayer;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.List;

public interface IHomeDAO {
    void deleteHome(int playerId, String name) throws DAOException;

    Location getHome(int playerId, String name, Server server) throws DAOException;

    Location getHome(TregminePlayer player) throws DAOException;

    Location getHome(TregminePlayer player, String name) throws DAOException;

    List<String> getHomeNames(int playerId) throws DAOException;

    void insertHome(TregminePlayer player, String name, Location loc) throws DAOException;
}
