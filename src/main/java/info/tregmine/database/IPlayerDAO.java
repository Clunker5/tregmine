package info.tregmine.database;

import info.tregmine.api.Badge;
import info.tregmine.api.TregminePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IPlayerDAO {
    TregminePlayer createPlayer(Player wrap) throws DAOException;

    boolean doesIgnore(TregminePlayer player, TregminePlayer victim) throws DAOException;

    Map<Badge, Integer> getBadges(TregminePlayer player) throws DAOException;

    List<String> getIgnored(TregminePlayer to) throws DAOException;

    List<String> getKeywords(TregminePlayer to) throws DAOException;

    TregminePlayer getPlayer(int id) throws DAOException;

    TregminePlayer getPlayer(Player player) throws DAOException;

    TregminePlayer getPlayer(String username) throws DAOException;

    TregminePlayer getPlayer(UUID id) throws DAOException;

    void updateBadges(TregminePlayer player) throws DAOException;

    void updateIgnore(TregminePlayer player, List<String> update) throws DAOException;

    void updateKeywords(TregminePlayer player, List<String> update) throws DAOException;

    void updatePlayer(TregminePlayer player) throws DAOException;

    void updatePlayerInfo(TregminePlayer player) throws DAOException;

    void updatePlayerKeyword(TregminePlayer player) throws DAOException;

    void updatePlayTime(TregminePlayer player) throws DAOException;

    void updateProperty(TregminePlayer player, String key, String value) throws DAOException;

    // public String generateReferralCode(TregminePlayer source) throws
    // DAOException;
}
