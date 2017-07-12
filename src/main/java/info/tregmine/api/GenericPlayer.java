package info.tregmine.api;

import info.tregmine.Tregmine;
import info.tregmine.api.returns.BooleanStringReturn;
import info.tregmine.zones.Zone;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ericrabil on 3/29/17.
 */
public interface GenericPlayer extends GenericDelegate {
    void addReport(String[] report);

    boolean alertedAfk();

    void awardBadgeLevel(Badge badge, String message);

    BooleanStringReturn canBeHere(Location loc);

    boolean canMentor();

    boolean canVS();

    String causeOfDeath();

    void checkActivity();

    TextComponent decideVS(GenericPlayer canthey);

    // java.lang.Object overrides
    @Override
    boolean equals(Object obj);

    boolean getAfkKick();

    void setAfkKick(boolean a);

    PermissionAttachment getAttachment();

    Set<Flags> getFlags();

    void setAttachment(PermissionAttachment ment);

    int getBadgeLevel(Badge badge);

    Map<Badge, Integer> getBadges();

    void setBadges(Map<Badge, Integer> v);

    int getBlessTarget();

    void setBlessTarget(int v);

    String getChatChannel();

    void setChatChannel(String v);

    TextComponent getChatName();

    String getChatNameNoColor();

    String getChatNameNoHover();

    TextComponent getChatNameStaff();

    ChatState getChatState();

    void setChatState(ChatState v);

    String getCity();

    void setCity(String v);

    int getCombatLog();

    void setCombatLog(int value);

    String getCountry();

    void setCountry(String v);

    FishyBlock getCurrentFishyBlock();

    void setCurrentFishyBlock(FishyBlock v);

    String getCurrentInventory();

    void setCurrentInventory(String inv);

    Zone getCurrentZone();

    void setCurrentZone(Zone zone);

    Block getFillBlock1();

    // block fill state
    void setFillBlock1(Block v);

    Block getFillBlock2();

    void setFillBlock2(Block v);

    int getFillBlockCounter();

    void setFillBlockCounter(int v);

    int getFishyBuyCount();

    void setFishyBuyCount(int v);

    boolean getFrozen();

    void setFrozen(boolean v);

    int getGuardianRank();

    void setGuardianRank(int v);

    GuardianState getGuardianState();

    void setGuardianState(GuardianState v);

    String getHost();

    void setHost(String v);

    int getId();

    void setId(int v);

    String getIp();

    void setIp(String v);

    boolean getIsStaff();

    String getKeyword();

    void setKeyword(String v);

    String getLastMessenger();

    void setLastMessenger(String messenger);

    long getLastOnlineActivity();

    void setLastOnlineActivity(long a);

    Location getLastPos();

    void setLastPos(Location pos);

    GenericPlayer getMentor();

    void setMentor(GenericPlayer v);

    PlayerMute getMute();

    void setMute(PlayerMute p0);

    ChatColor getNameColor();

    boolean getNewChunk();

    void setNewChunk(boolean value);

    FishyBlock getNewFishyBlock();

    // Fishy block state
    void setNewFishyBlock(FishyBlock v);

    Nickname getNickname();

    String getPasswordHash();

    void setPasswordHash(String v);

    int getPlayTime();

    void setPlayTime(int v);

    Tregmine getPlugin();

    QuitCause getQuitCause();

    void setQuitCause(QuitCause q);

    String getQuitMessage();

    void setQuitMessage(String v);

    Rank getRank();

    void setRank(Rank v);

    String getRealName();

    List<String[]> getReports();

    int getReportTotal();

    UUID getStoredUuid();

    void setStoredUuid(UUID v);

    GenericPlayer getStudent();

    void setStudent(GenericPlayer v);

    int getTargetZoneId();

    void setTargetZoneId(int v);

    int getTimeOnline();

    int getTotalBans();

    void setTotalBans(int total);

    int getTotalHards();

    void setTotalHards(int total);

    int getTotalKicks();

    void setTotalKicks(int total);

    int getTotalSofts();

    void setTotalSofts(int total);

    Rank getTrueRank();

    Block getZoneBlock1();

    // zones state
    void setZoneBlock1(Block v);

    Block getZoneBlock2();

    void setZoneBlock2(Block v);

    int getZoneBlockCounter();

    void setZoneBlockCounter(int v);

    void gotoWorld(Player player, Location loc, String success, String failure);

    boolean hasBadge(Badge badge);

    boolean hasBlockPermission(Location loc, boolean punish);

    boolean hasCommandStatus(CommandStatus status);

    boolean hasFlag(Flags flag);

    @Override
    int hashCode();

    boolean hasNick();

    boolean hasProperty(Property prop);

    // convenience methods
    void hidePlayer(GenericPlayer player);

    boolean isAfk();

    void setAfk(boolean value);

    boolean isCombatLogged();

    boolean isCurseWarned();

    void setCurseWarned(boolean a);

    boolean isHidden();

    boolean isInVanillaWorld();

    boolean isMuted();

    void setMuted(boolean p0);

    boolean isValid();

    void setValid(boolean v);

    /*
         * Load an already existing inventory
         *
         * @param name - Name of the inventory
         *
         * @param save - Same current inventory
         */
    void loadInventory(String name, boolean save);

    ChatColor RankColor();

    void removeCommandStatus(CommandStatus status);

    void removeFlag(Flags flag);

    void removeProperty(Property prop);

    void resetTimeOnline();

    /*
         * Save the inventory specified, if null - saves current inventory.
         *
         * @param name - Name of the new inventory
         */
    void saveInventory(String name);

    void sendNotification(Notification notif);

    void sendNotification(Notification notif, BaseComponent... message);

    void sendNotification(Notification notif, BaseComponent message);

    void setAlerted(boolean a);

    void setCommandStatus(CommandStatus status);

    void setCurrentTexture(String url);

    void setDeathCause(String a);

    void setFlag(Flags flag);

    void setNick(Nickname n);

    void setPassword(String newPassword);

    void setProperty(Property prop);

    void setSilentAfk(boolean value);

    void setStaff(boolean v);

    void setTemporaryChatName(String name);

    void setTemporaryRank(Rank v);

    void showPlayer(GenericPlayer player);

    void teleportWithHorse(Location loc);

    Zone updateCurrentZone();

    boolean verifyPassword(String attempt);

    public enum ChatState {
        SETUP, CHAT, TRADE, SELL, FISHY_SETUP, FISHY_WITHDRAW, FISHY_BUY, BANK
    }

    // Flags are stored as integers - order must _NOT_ be changed
    public enum Flags {
        CHILD, TPSHIELD, SOFTWARNED, HARDWARNED, INVISIBLE, HIDDEN_LOCATION, FLY_ENABLED, FORCESHIELD, CHEST_LOG, HIDDEN_ANNOUNCEMENT, CHANNEL_VIEW, WATCHING_CHUNKS, AFK_KICK
    }

    public enum GuardianState {
        ACTIVE, INACTIVE, QUEUED
    }

    public enum Property {
        NICKNAME
    }
}
