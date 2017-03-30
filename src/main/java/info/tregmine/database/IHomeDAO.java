package info.tregmine.database;

import info.tregmine.api.GenericPlayer;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.List;

public interface IHomeDAO {
    void deleteHome(int playerId, String name) throws DAOException;

    Location getHome(int playerId, String name, Server server) throws DAOException;

    Location getHome(GenericPlayer player) throws DAOException;

    Location getHome(GenericPlayer player, String name) throws DAOException;

    List<String> getHomeNames(int playerId) throws DAOException;

    void insertHome(GenericPlayer player, String name, Location loc) throws DAOException;
}
