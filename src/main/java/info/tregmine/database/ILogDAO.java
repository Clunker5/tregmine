package info.tregmine.database;

import info.tregmine.api.GenericPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.Set;

public interface ILogDAO {
    Set<String> getAliases(GenericPlayer player) throws DAOException;

    Date getLastSeen(GenericPlayer player) throws DAOException;

    void insertChatMessage(GenericPlayer player, String channel, String message) throws DAOException;

    void insertGiveLog(GenericPlayer sender, GenericPlayer recipient, ItemStack stack) throws DAOException;

    void insertLogin(GenericPlayer player, boolean logout, int onlinePlayers) throws DAOException;

    void insertOreLog(GenericPlayer player, Location loc, Material material) throws DAOException;

    void insertWarpLog(GenericPlayer player, int warpId) throws DAOException;
}
