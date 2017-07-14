package info.tregmine.database;

import info.tregmine.api.Badge;
import info.tregmine.api.GenericPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IPlayerDAO {
    GenericPlayer createPlayer(Player wrap) throws DAOException;

    boolean doesIgnore(GenericPlayer player, GenericPlayer victim) throws DAOException;

    Map<Badge, Integer> getBadges(GenericPlayer player) throws DAOException;

    List<String> getIgnored(GenericPlayer to) throws DAOException;

    List<String> getKeywords(GenericPlayer to) throws DAOException;

    GenericPlayer getPlayer(int id) throws DAOException;

    GenericPlayer getPlayer(Player player) throws DAOException;

    GenericPlayer getPlayer(String username) throws DAOException;

    GenericPlayer getPlayer(UUID id) throws DAOException;

    void updateBadges(GenericPlayer player) throws DAOException;

    void updateIgnore(GenericPlayer player, List<String> update) throws DAOException;

    void updateKeywords(GenericPlayer player, List<String> update) throws DAOException;

    void updatePlayer(GenericPlayer player) throws DAOException;

    void updatePlayerInfo(GenericPlayer player) throws DAOException;

    void updatePlayerKeyword(GenericPlayer player) throws DAOException;

    void updatePlayTime(GenericPlayer player) throws DAOException;

    void updateProperty(GenericPlayer player, String key, String value) throws DAOException;

    void deleteProperty(GenericPlayer player, String key) throws DAOException;

    // public String generateReferralCode(TregminePlayer source) throws
    // DAOException;
}
