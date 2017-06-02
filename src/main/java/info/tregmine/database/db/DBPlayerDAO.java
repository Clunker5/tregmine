package info.tregmine.database.db;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Rank;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IPlayerDAO;
import info.tregmine.database.db.pojo.PlayerPOJO;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by eric on 6/1/17.
 */
public class DBPlayerDAO extends BasicDAO<PlayerPOJO, String> implements IPlayerDAO {

    private Tregmine plugin;

    public DBPlayerDAO(Class<PlayerPOJO> entityClass, Datastore ds, Tregmine plguin)
    {
        super(entityClass, ds);
        this.plugin = plugin;
    }

    @Override
    public GenericPlayer createPlayer(Player wrap) {
        PlayerPOJO pojo = new PlayerPOJO();
        pojo.uuid = wrap.getUniqueId().toString();
        pojo.username = wrap.getName();
        pojo.keywords = wrap.getName();
        String id = (String) this.save(pojo).getId();
        TregminePlayer player = new TregminePlayer(wrap, plugin);
        player.setId(id);
        return player;
    }

    @Override
    public boolean doesIgnore(GenericPlayer player, GenericPlayer victim) {
        try {
            List<String> ignore = Arrays.asList(this.get(player.getId()).ignore.split(","));
            return ignore.contains(victim.getName());
        }catch(NullPointerException e) {
            return false;
        }
    }

    @Override
    public List<String> getIgnored(GenericPlayer player) {
        try {
            return Arrays.asList(this.get(player.getId()).ignore.split(","));
        }catch(NullPointerException e) {
            return null;
        }
    }

    @Override
    public List<String> getKeywords(GenericPlayer player) {
        try {
            return Arrays.asList(this.get(player.getId()).keywords.split(","));
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public GenericPlayer getPlayer(String id) throws DAOException {
        return getPlayerByPOJO(this.findOne("id", new ObjectId(id)));
    }

    @Override
    public GenericPlayer getPlayer(Player player) throws DAOException {
        return getPlayerByPOJO(this.findOne("uuid", player.getUniqueId().toString()));
    }

    @Override
    @Deprecated
    public GenericPlayer getPlayerByUsername(String username) throws DAOException {
        return getPlayerByPOJO(this.findOne("username", username));
    }

    @Override
    public GenericPlayer getPlayer(UUID id) throws DAOException {
        return getPlayerByPOJO(this.findOne("uuid", id.toString()));
    }

    @Override
    public GenericPlayer getPlayerByPOJO(PlayerPOJO playerPOJO) {
        if(playerPOJO == null) return null;
        TregminePlayer player = new TregminePlayer(UUID.fromString(playerPOJO.uuid), plugin, playerPOJO.username);
        player.setId(playerPOJO.id.toString());
        player.setPasswordHash(playerPOJO.password);
        player.setRank(playerPOJO.rank);
        player.setCurrentInventory(playerPOJO.inventory == null ? "survival" : playerPOJO.inventory);
        for (GenericPlayer.Flags flag : GenericPlayer.Flags.values()) {
            if ((playerPOJO.flags & (1 << flag.ordinal())) != 0) {
                player.setFlag(flag);
            }
        }
        loadSettings(player);
        return player;
    }

    @Override
    public void updateIgnore(GenericPlayer player, List<String> update) throws DAOException {
        PlayerPOJO playerPOJO = this.get(player.getId());
        if (playerPOJO == null) return;
        if (update.size() == 0) return;
        StringBuilder buffer = new StringBuilder();
        update.forEach((ignored) -> buffer.append((buffer.length() == 0 ? "" : ",") + ignored));
        playerPOJO.ignore = buffer.toString();
        this.save(playerPOJO);
    }

    @Override
    public void updateKeywords(GenericPlayer player, List<String> update) throws DAOException {
        PlayerPOJO playerPOJO = this.get(player.getId());
        if (playerPOJO == null) return;
        if (update.size() == 0) return;
        StringBuilder buffer = new StringBuilder();
        update.forEach((ignored) -> buffer.append((buffer.length() == 0 ? "" : ",") + ignored));
        playerPOJO.keywords = buffer.toString();
        this.save(playerPOJO);
    }

    @Override
    public void updatePlayer(GenericPlayer player) throws DAOException {
        PlayerPOJO playerPOJO = this.get(player.getId());
        if (playerPOJO == null) playerPOJO = new PlayerPOJO();
        playerPOJO.keywords = player.getKeyword();
        playerPOJO.inventory = player.getCurrentInventory();
        playerPOJO.username = player.getName();
        playerPOJO.confirmed = player.getRank() != Rank.UNVERIFIED;
        int flags = 0;
        for (GenericPlayer.Flags flag : GenericPlayer.Flags.values()) {
            flags |= player.hasFlag(flag) ? 1 << flag.ordinal() : 0;
        }
        playerPOJO.flags = flags;
        playerPOJO.password = player.getPasswordHash();
        playerPOJO.uuid = player.getStoredUuid().toString();
        this.save(playerPOJO);
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
        updateProperty(player, "playtime", String.valueOf(player.getPlayTime() + player.getTimeOnline()));
    }

    @Override
    public void updateProperty(GenericPlayer player, String key, String value) throws DAOException {
        PlayerPOJO playerPOJO = this.get(player.getId());
        if (playerPOJO == null) return;
        Map<String, String> propMap = propToMap(playerPOJO.properties);
        propMap.put(key, value);
        playerPOJO.properties = mapToProp(propMap);
        this.save(playerPOJO);
    }

    private void loadSettings(GenericPlayer player) {
        try{
            PlayerPOJO playerPOJO = this.get(player.getId());
            String[] properties = playerPOJO.properties.split(",");
            for(String prop : properties) {
                try {
                    String[] kV = prop.split(":");
                    if (kV.length != 2) continue;
                    switch (kV[0]) {
                        case "keyword":
                            player.setKeyword(kV[1]);
                            break;
                        case "guardian":
                            player.setGuardianRank(Integer.parseInt(kV[1]));
                            break;
                        case "quitmessage":
                            player.setQuitMessage(kV[1]);
                            break;
                        case "playtime":
                            player.setPlayTime(Integer.parseInt(kV[1]));
                            break;
                        case "afkkick":
                            player.setAfkKick(Boolean.valueOf(kV[1]));
                            break;
                        case "cursewarned":
                            player.setCurseWarned(Boolean.valueOf(kV[1]));
                            break;
                        case "nick":
                            player.setProperty(GenericPlayer.Property.NICKNAME);
                            player.setTemporaryChatName(this.plugin.getRankColor(player.getRank()) + player.getRank().getDiscordEquivalent());
                            break;
                    }
                } catch (NumberFormatException e) { continue; }
            }
        }catch(NullPointerException e) {

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> propToMap(String properties) {
        Map<String, String> propMap = new HashMap();
        for(String pair : properties.split(",")) {
            String[] kV = pair.split(":");
            if (kV.length != 2) continue;
            propMap.put(kV[0], kV[1]);
        }
        return propMap;
    }

    private String mapToProp(Map<String, String> properties) {
        StringBuilder rawProps = new StringBuilder();
        for(String key : properties.keySet()) {
            rawProps.append((rawProps.length() == 0 ? "" : ",") + key + ":" + properties.get(key));
        }
        return rawProps.toString();
    }
}
