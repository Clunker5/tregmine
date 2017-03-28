package info.tregmine.database;

import info.tregmine.api.Warp;
import org.bukkit.Location;
import org.bukkit.Server;

public interface IWarpDAO {
    Warp getWarp(String name, Server server) throws DAOException;

    void insertWarp(String name, Location loc) throws DAOException;
}
