package info.tregmine.database;

import info.tregmine.api.TregminePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.Set;

public interface ILogDAO {
    Set<String> getAliases(TregminePlayer player) throws DAOException;

    Date getLastSeen(TregminePlayer player) throws DAOException;

    void insertChatMessage(TregminePlayer player, String channel, String message) throws DAOException;

    void insertGiveLog(TregminePlayer sender, TregminePlayer recipient, ItemStack stack) throws DAOException;

    void insertLogin(TregminePlayer player, boolean logout, int onlinePlayers) throws DAOException;

    void insertOreLog(TregminePlayer player, Location loc, Material material) throws DAOException;

    void insertWarpLog(TregminePlayer player, int warpId) throws DAOException;
}
