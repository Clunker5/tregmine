package info.tregmine.database.db;

import info.tregmine.Tregmine;
import info.tregmine.api.*;
import info.tregmine.database.DAOException;
import info.tregmine.database.IPlayerDAO;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DBPlayerDAO implements IPlayerDAO {
    public static final String PLAYER_SEARCH_STRING = "SELECT player_uuid, player_name, player_id, player_uuid, player_password, player_rank, player_inventory, player_flags FROM player WHERE %s = ? LIMIT 1";
    private Connection conn;
    private Tregmine plugin;
    private Map<Integer, List<String>> keywordCache = new HashMap<>();
    private Map<Integer, List<String>> ignoreCache = new HashMap<>();

    public DBPlayerDAO(Connection conn) {
        this.conn = conn;
    }

    public DBPlayerDAO(Connection conn, Tregmine instance) {
        this.conn = conn;
        this.plugin = instance;
    }

    @Override
    public GenericPlayer createPlayer(Player wrap) throws DAOException {
        String sql = "INSERT INTO player (player_uuid, player_name, player_rank, player_keywords) VALUE (?, ?, ?, ?)";

        GenericPlayer player = new TregminePlayer(wrap, plugin);
        player.setStoredUuid(player.getUniqueId());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setString(3, player.getTrueRank().toString());
            stmt.setString(4, player.getRealName());
            stmt.execute();

            stmt.executeQuery("SELECT LAST_INSERT_ID()");

            try (ResultSet rs = stmt.getResultSet()) {
                if (!rs.next()) {
                    throw new DAOException("Failed to get player id", sql);
                }

                player.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }

        return player;
    }

    @Override
    public boolean doesIgnore(GenericPlayer player, GenericPlayer victim) throws DAOException {
        if (this.ignoreCache.containsKey(player.getId()))
            return this.ignoreCache.get(player.getId()).contains(victim.getRealName());

        String sql = "SELECT player_ignore FROM player WHERE player_id = ? ";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, player.getId());
            stmt.execute();

            try (ResultSet rs = stmt.getResultSet()) {
                if (!rs.next())
                    return false;

                String stringofignored = rs.getString("player_ignore");
                if (stringofignored == null || stringofignored.length() == 0) {
                    return false;
                }

                List<String> strings = Arrays.asList(stringofignored.split(","));
                this.ignoreCache.put(player.getId(), strings);

                return strings.contains(victim.getRealName());
            }
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    @Override
    public Map<Badge, Integer> getBadges(GenericPlayer player) throws DAOException {
        String sql = "SELECT badge_name, badge_level FROM player_badge " + "WHERE player_id = ?";

        Map<Badge, Integer> badges = new EnumMap<Badge, Integer>(Badge.class);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, player.getId());
            stmt.execute();

            try (ResultSet rs = stmt.getResultSet()) {
                while (rs.next()) {
                    Badge badge = Badge.fromString(rs.getString("badge_name"));
                    int lvl = rs.getInt("badge_level");
                    badges.put(badge, lvl);
                }
            }
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }

        return badges;
    }

    @Override
    public List<String> getIgnored(GenericPlayer player) throws DAOException {
        if (this.ignoreCache.containsKey(player.getId())) return this.ignoreCache.get(player.getId());
        String sql = "SELECT player_ignore FROM player " + "WHERE player_id = ? ";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, player.getId());
            stmt.execute();

            try (ResultSet rs = stmt.getResultSet()) {
                if (!rs.next())
                    return null;
                String stringofignored = rs.getString("player_ignore");
                List<String> strings = stringofignored == null ? new ArrayList<>() : Arrays.asList(stringofignored.split(","));
                this.ignoreCache.put(player.getId(), strings);
                return strings;
            }
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    @Override
    public List<String> getKeywords(GenericPlayer to) throws DAOException {
        if (this.keywordCache.containsKey(to.getId())) return this.keywordCache.get(to.getId());

        String sql = "SELECT player_keywords FROM player " + "WHERE player_id = ? ";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, to.getId());
            stmt.execute();

            try (ResultSet rs = stmt.getResultSet()) {
                if (!rs.next())
                    return null;

                String stringofkeywords = rs.getString("player_keywords");
                String[] strings = stringofkeywords.split(",");

                List<String> playerkeywords = new ArrayList<String>();
                for (String i : strings) {
                    if ("".equalsIgnoreCase(i))
                        continue;
                    playerkeywords.add(i);
                }

                return playerkeywords;
            }
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    @Override
    public GenericPlayer getPlayer(int id) throws DAOException {
        return this.loadPlayer("player_id", id);
    }

    @Override
    public GenericPlayer getPlayer(Player wrap) throws DAOException {
        return this.loadPlayer(wrap);
    }

    @Override
    public GenericPlayer getPlayer(String username) throws DAOException {
        return this.loadPlayer("player_name", username);
    }

    private GenericPlayer loadPlayer(String key, Object value) throws DAOException {
        String sql = String.format(PLAYER_SEARCH_STRING, key);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, value);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next())
                return null;
            return this.injectData(rs, new TregminePlayer(UUID.fromString("player_uuid"), plugin, rs.getString("player_name")));
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    private GenericPlayer loadPlayer(Player wrap) throws DAOException {
        String sql = String.format(PLAYER_SEARCH_STRING, "player_uuid");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, wrap.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next())
                return null;
            return this.injectData(rs, new TregminePlayer(wrap, plugin));
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    private GenericPlayer injectData(ResultSet rs, GenericPlayer player) throws DAOException, SQLException {
        player.setId(rs.getInt("player_id"));
        String uniqueIdStr = rs.getString("player_uuid");
        if (uniqueIdStr != null) {
            player.setStoredUuid(UUID.fromString(uniqueIdStr));
        }
        player.setPasswordHash(rs.getString("player_password"));
        player.setRank(Rank.fromString(rs.getString("player_rank")));
        if (rs.getString("player_inventory") == null) {
            player.setCurrentInventory("survival");
        } else {
            player.setCurrentInventory(rs.getString("player_inventory"));
        }

        int flags = rs.getInt("player_flags");
        for (GenericPlayer.Flags flag : GenericPlayer.Flags.values()) {
            if ((flags & (1 << flag.ordinal())) != 0) {
                player.setFlag(flag);
            }
        }
        this.loadSettings(player);
        this.loadReports(player);
        return player;
    }

    @Override
    public GenericPlayer getPlayer(UUID id) throws DAOException {
        return this.loadPlayer("player_uuid", id.toString());
    }

    private void loadReports(GenericPlayer player) throws DAOException {
        String sql = "SELECT report_action FROM player_report WHERE subject_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, player.getId());
            stmt.execute();
            try (ResultSet rs = stmt.getResultSet()) {
                while (rs.next()) {
                    if ("softwarn".equals(rs.getString("report_action"))) {
                        player.setTotalSofts(player.getTotalSofts() + 1);
                    }
                    if ("hardwarn".equals(rs.getString("report_action"))) {
                        player.setTotalHards(player.getTotalHards() + 1);
                    }
                    if ("kick".equals(rs.getString("report_action"))) {
                        player.setTotalKicks(player.getTotalKicks() + 1);
                    }
                    if ("ban".equals(rs.getString("report_action"))) {
                        player.setTotalBans(player.getTotalBans() + 1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSettings(GenericPlayer player) throws DAOException {
        String sql = "SELECT property_key, property_value FROM player_property WHERE player_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, player.getId());
            stmt.execute();

            try (ResultSet rs = stmt.getResultSet()) {
                while (rs.next()) {
                    String key = rs.getString("property_key");
                    String value = rs.getString("property_value");
                    if ("keyword".equals(key)) {
                        player.setKeyword(value);
                    } else if ("guardian".equals(key)) {
                        player.setGuardianRank(Integer.parseInt(value));
                    } else if ("quitmessage".equals(key)) {
                        player.setQuitMessage(value);
                    } else if ("playtime".equals(key)) {
                        player.setPlayTime(Integer.parseInt(value));
                    } else if ("afkkick".equals(key)) {
                        player.setAfkKick(Boolean.valueOf(value));
                    } else if ("cursewarned".equals(key)) {
                        player.setCurseWarned(Boolean.valueOf(value));
                    } else if ("nickname".equals(key)) {
                        player.setNickname(Nickname.fromSQL(value));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    // public String generateReferralCode(TregminePlayer source) throws
    // DAOException {
    // //Generate a six-character securely randomized string, to be used as a
    // referral code.
    // String referralCode = new BigInteger(130,
    // this.plugin.getSecureRandom()).toString(32).substring(0, 6);
    // String sql = "UPDATE player SET player_referralcode = ? WHERE player_id =
    // ?";
    // try(PreparedStatement stmt = conn.prepareStatement(sql)){
    // stmt.setString(1, referralCode);
    // stmt.setInt(2, source.getId());
    // stmt.execute();
    // }catch(SQLException e){
    // throw new DAOException(sql, e);
    // }
    // return referralCode;
    // }

    @Override
    public void updateBadges(GenericPlayer player) throws DAOException {
        Map<Badge, Integer> dbBadges = player.getBadges();
        Map<Badge, Integer> memBadges = getBadges(player);

        Map<Badge, Integer> added = new EnumMap<Badge, Integer>(Badge.class);
        Map<Badge, Integer> changed = new EnumMap<Badge, Integer>(Badge.class);
        for (Map.Entry<Badge, Integer> entry : memBadges.entrySet()) {
            if (dbBadges.containsKey(entry.getKey()) && dbBadges.get(entry.getKey()) != entry.getValue()) {

                changed.put(entry.getKey(), entry.getValue());
            } else if (!dbBadges.containsKey(entry.getKey())) {
                added.put(entry.getKey(), entry.getValue());
            }
        }

        Map<Badge, Integer> deleted = new EnumMap<Badge, Integer>(Badge.class);
        for (Map.Entry<Badge, Integer> entry : dbBadges.entrySet()) {
            if (!memBadges.containsKey(entry.getKey())) {
                deleted.put(entry.getKey(), entry.getValue());
            }
        }

        String sqlInsert = "INSERT INTO player_badge (player_id, badge_name, " + "badge_level, badge_timestamp) ";
        sqlInsert += "VALUES (?, ?, ?, unix_timestamp())";
        try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
            for (Map.Entry<Badge, Integer> entry : added.entrySet()) {
                stmt.setInt(1, player.getId());
                stmt.setString(2, entry.getKey().toString());
                stmt.setInt(3, entry.getValue());
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new DAOException(sqlInsert, e);
        }

        String sqlUpdate = "UPDATE player_badge SET badge_level = ? " + "WHERE player_id = ? AND badge_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
            for (Map.Entry<Badge, Integer> entry : changed.entrySet()) {
                stmt.setInt(1, entry.getValue());
                stmt.setInt(2, player.getId());
                stmt.setString(3, entry.getKey().toString());
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new DAOException(sqlUpdate, e);
        }

        String sqlDelete = "DELETE FROM player_badge " + "WHERE player_id = ? AND badge_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
            for (Map.Entry<Badge, Integer> entry : deleted.entrySet()) {
                stmt.setInt(1, player.getId());
                stmt.setString(2, entry.getKey().toString());
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new DAOException(sqlDelete, e);
        }
    }

    @Override
    public void updateIgnore(GenericPlayer player, List<String> update) throws DAOException {
        if (this.ignoreCache.containsKey(player.getId())) {
            if (this.ignoreCache.get(player.getId()).equals(update))
                return;
            this.ignoreCache.put(player.getId(), update);
        }

        String sql = "UPDATE player SET player_ignore = ? " + "WHERE player_id = ?";

        StringBuilder buffer = new StringBuilder();
        String delim = "";
        for (String ignored : update) {
            buffer.append(delim);
            buffer.append(ignored);
            delim = ",";
        }
        String updateIgnoreString = buffer.toString();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, updateIgnoreString);
            stmt.setInt(2, player.getId());
            stmt.execute();
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    @Override
    public void updateKeywords(GenericPlayer player, List<String> update) throws DAOException {
        String sql = "UPDATE player SET player_keywords = ? " + "WHERE player_id = ?";

        if (this.keywordCache.containsKey(player.getId())) {
            if (this.keywordCache.get(player.getId()).equals(update))
                return;
            this.keywordCache.put(player.getId(), update);
        }

        StringBuilder buffer = new StringBuilder();
        String delim = "";
        for (String keyword : update) {
            buffer.append(delim);
            buffer.append(keyword);
            delim = ",";
        }
        String keywordsString = buffer.toString();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keywordsString);
            stmt.setInt(2, player.getId());
            stmt.execute();
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    @Override
    public void updatePlayer(GenericPlayer player) throws DAOException {
        String sql = "UPDATE player SET player_uuid = ?, player_password = ?, "
                + "player_rank = ?, player_flags = ?, player_inventory = ? ";
        sql += "WHERE player_id = ?";

        int flags = 0;
        for (GenericPlayer.Flags flag : GenericPlayer.Flags.values()) {
            flags |= player.hasFlag(flag) ? 1 << flag.ordinal() : 0;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getStoredUuid().toString());
            stmt.setString(2, player.getPasswordHash());
            stmt.setString(3, player.getRank().toString());
            stmt.setInt(4, flags);
            stmt.setString(5, player.getCurrentInventory());
            stmt.setInt(6, player.getId());
            stmt.execute();
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    @Override
    public void updatePlayerInfo(GenericPlayer player) throws DAOException {
        updateProperty(player, "quitmessage", player.getQuitMessage());
    }

    @Override
    public void updatePlayerKeyword(GenericPlayer player) throws DAOException {
        updateProperty(player, "keyword", player.getKeyword());
    }

    @Override
    public void updatePlayTime(GenericPlayer player) throws DAOException {
        int playTime = player.getPlayTime() + player.getTimeOnline();
        updateProperty(player, "playtime", String.valueOf(playTime));
    }

    @Override
    public void updateProperty(GenericPlayer player, String key, String value) throws DAOException {
        String sqlInsert = "REPLACE INTO player_property (player_id, "
                + "property_key, property_value) VALUE (?, ?, ?)";

        if (value == null) {
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
            stmt.setInt(1, player.getId());
            stmt.setString(2, key);
            stmt.setString(3, value);
            stmt.execute();
        } catch (SQLException e) {
            throw new DAOException(sqlInsert, e);
        }
    }

    @Override
    public void deleteProperty(GenericPlayer player, String key) throws DAOException {
        String sqlDelete = "DELETE FROM player_property WHERE player_id = ? AND property_key = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
            stmt.setInt(1, player.getId());
            stmt.setString(2, key);
            stmt.execute();
        } catch (SQLException e) {
            throw new DAOException(sqlDelete, e);
        }
    }
}
