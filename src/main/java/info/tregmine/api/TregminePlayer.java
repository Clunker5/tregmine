package info.tregmine.api;

import info.tregmine.Tregmine;
import info.tregmine.api.encryption.BCrypt;
import info.tregmine.api.returns.BooleanStringReturn;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IInventoryDAO;
import info.tregmine.database.IPlayerDAO;
import info.tregmine.quadtree.Point;
import info.tregmine.zones.Lot;
import info.tregmine.zones.Zone;
import info.tregmine.zones.ZoneWorld;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

import static org.bukkit.ChatColor.*;

public class TregminePlayer extends PlayerDelegate implements GenericPlayer {
    // Persistent values
    private int id = 0;
    private UUID storedUuid = null;
    private String name = null;
    private String realName = null;
    private String password = null;
    private String keyword = null;
    private Rank rank = Rank.UNVERIFIED;
    private boolean Staff = false;
    private String quitMessage = null;
    private int guardianRank = 0;
    private int playTime = 0;
    private Set<Flags> flags;
    private Set<CommandStatus> commandstatus;
    private Set<Property> properties;
    private Map<Badge, Integer> badges;
    private Location lastpos = null;
    // Discord values
    private boolean alertedAfk;
    private QuitCause causeofquit = null;
    // One-time state
    private String chatChannel = "GLOBAL";
    private Zone currentZone = null;
    private GuardianState guardianState = GuardianState.QUEUED;
    private int blessTarget = 0;
    private ChatState chatState = ChatState.CHAT;
    private Date loginTime = null;
    private boolean valid = true;
    private String ip;
    private String host;
    private String city;
    private String country;
    private GenericPlayer mentor;
    private GenericPlayer student;
    private String currentInventory;
    private int combatLog;
    private long lastOnlineActivity;
    private String lastMessenger;
    private boolean AfkKick = true;
    private boolean CurseWarned;
    private PermissionAttachment attachment;
    private PlayerMute mute = null;
    private boolean muted = false;

    private String namePreAfkAppendage;

    // Reports
    private List<String[]> reports = new ArrayList<String[]>();
    private int kicks = 0;
    private int softwarns = 0;
    private int hardwarns = 0;
    private int bans = 0;
    // Player state for block fill
    private Block fillBlock1 = null;
    private Block fillBlock2 = null;
    private int fillBlockCounter = 0;
    // Player state for zone creation
    private Block zoneBlock1 = null;
    private Block zoneBlock2 = null;
    private int zoneBlockCounter = 0;
    private int targetZoneId = 0;
    // Player state for activity
    private boolean afk = false;
    private boolean isFrozen = false;
    // Fishy Block state
    private FishyBlock newFishyBlock;
    private FishyBlock currentFishyBlock;
    private int fishyBuyCount;
    // Chunk Watcher
    private boolean newChunk = false;
    // Ranks
    private boolean isTemporaryRank = false;
    private Rank temporaryRank = null;
    // Death states
    private String deathcause = "";
    // Nickname stats
    private boolean hasNick = false;
    private Nickname nickname;
    private Tregmine plugin;

    public TregminePlayer(Player player, Tregmine instance) {
        super(player);

        this.name = player.getName();
        this.realName = player.getName();
        this.loginTime = new Date();

        this.flags = EnumSet.noneOf(Flags.class);
        this.commandstatus = EnumSet.noneOf(CommandStatus.class);
        this.properties = EnumSet.noneOf(Property.class);
        this.badges = new EnumMap<Badge, Integer>(Badge.class);
        this.plugin = instance;
    }

    public TregminePlayer(UUID uuid, Tregmine instance, String uname) {
        super(null);
        this.name = uname;
        this.realName = uname;
        this.loginTime = new Date();

        this.flags = EnumSet.noneOf(Flags.class);
        this.commandstatus = EnumSet.noneOf(CommandStatus.class);
        this.properties = EnumSet.noneOf(Property.class);
        this.badges = new EnumMap<Badge, Integer>(Badge.class);
        this.plugin = instance;
    }

    @Override
    public void addReport(String[] report) {
        this.reports.add(report);
    }

    @Override
    public boolean alertedAfk() {
        return this.alertedAfk;
    }

    @Override
    public void awardBadgeLevel(Badge badge, String message) {
        int badgeLevel = getBadgeLevel(badge) + 1;
        badges.put(badge, badgeLevel);

        if (badgeLevel == 1) {
            sendMessage(ChatColor.GOLD + "Congratulations! You've been awarded " + "the " + badge.getName()
                    + " badge of honor: " + message);
        } else {
            sendMessage(ChatColor.GOLD + "Congratulations! You've been awarded " + "the level " + ChatColor.GREEN
                    + badgeLevel + " " + ChatColor.GOLD + badge.getName() + "badge of honor: " + message);
        }
    }

    @Override
    public BooleanStringReturn canBeHere(Location loc) {
        ZoneWorld world = plugin.getWorld(loc.getWorld());
        Zone zone = world.findZone(loc);
        Lot lot = world.findLot(loc);

        if (zone == null) { // Wilderness - Can be there
            return new BooleanStringReturn(true, null);
        }

        if (this.getRank().canModifyZones()) { // Admins can be there
            return new BooleanStringReturn(true, null);
        }

        Zone.Permission permission = zone.getUser(this);

        if (zone.getEnterDefault()) {
            // Banned - Can not be there
            if (permission != null && permission == Zone.Permission.Banned) {
                return new BooleanStringReturn(false,
                        ChatColor.RED + "[" + zone.getName() + "] You are banned from " + zone.getName());
            }

            // If zone has BlockWarned and user is warned
            if (zone.hasFlag(Zone.Flags.BLOCK_WARNED) && (this.hasFlag(GenericPlayer.Flags.HARDWARNED)
                    || this.hasFlag(GenericPlayer.Flags.SOFTWARNED))) {
                return new BooleanStringReturn(false,
                        ChatColor.RED + "[" + zone.getName() + "] You must not be warned to be here!");
            }

            // If zone has Admin Only and user is not admin
            if (zone.hasFlag(Zone.Flags.ADMIN_ONLY)
                    && (this.getRank() != Rank.JUNIOR_ADMIN || this.getRank() != Rank.SENIOR_ADMIN)) {
                return new BooleanStringReturn(false,
                        ChatColor.RED + "[" + zone.getName() + "] You must be an admin to enter " + zone.getName());
            }

            // If zone has Require Residency and user is not resident yet
            if (zone.hasFlag(Zone.Flags.REQUIRE_RESIDENCY) && (this.getRank() == Rank.UNVERIFIED
                    || this.getRank() == Rank.TOURIST || this.getRank() == Rank.SETTLER)) {
                return new BooleanStringReturn(false, ChatColor.RED + "[" + zone.getName() + "] You must be atleast "
                        + plugin.getRankColor(Rank.RESIDENT) + "Resident" + ChatColor.RED + zone.getName());
            }
        } else {
            // If no permission (Allowed, Maker, Owner, Banned) then stop
            if (permission == null) {
                return new BooleanStringReturn(false,
                        ChatColor.RED + "[" + zone.getName() + "] You are not allowed to enter " + zone.getName());
            }

            // If the permission is banned then stop
            if (permission == Zone.Permission.Banned) {
                return new BooleanStringReturn(false,
                        ChatColor.RED + "[" + zone.getName() + "] You are banned from " + zone.getName());
            }
        }

        // If private lot
        if (lot != null && lot.hasFlag(Lot.Flags.PRIVATE)) {
            // If not owner - then stop
            if (!lot.isOwner(this)) {
                return new BooleanStringReturn(false, ChatColor.RED + "[" + zone.getName() + "] This lot is private!");
            }
        }

        return new BooleanStringReturn(true, null);
    }

    @Override
    public boolean canMentor() {
        if (hasFlag(GenericPlayer.Flags.SOFTWARNED) || hasFlag(GenericPlayer.Flags.HARDWARNED)) {

            return false;
        }

        return getRank().canMentor();
    }

    @Override
    public boolean canVS() {
        return this.getRank().canViewPlayerStats();
    }

    @Override
    public String causeOfDeath() {
        return this.deathcause;
    }

    @Override
    public void checkActivity() {
        long autoafk = plugin.getConfig().getLong("general.afk.autoafk");
        if (!isAfk() && autoafk > 0 && lastOnlineActivity + autoafk * 1000 < System.currentTimeMillis()) {
            setAfk(true);
        }

    }

    @Override
    public TextComponent decideVS(GenericPlayer canthey) {
        if (canthey.canVS()) {
            return this.getChatNameStaff();
        } else {
            return this.getChatName();
        }
    }

    // java.lang.Object overrides
    @Override
    public boolean equals(Object obj) {
        return ((GenericPlayer) obj).getId() == getId();
    }

    @Override
    public boolean getAfkKick() {
        return AfkKick;
    }

    @Override
    public void setAfkKick(boolean a) {
        this.AfkKick = a;
    }

    @Override
    public PermissionAttachment getAttachment() {
        return this.attachment;
    }

    @Override
    public void setAttachment(PermissionAttachment ment) {
        this.attachment = ment;
    }

    @Override
    public int getBadgeLevel(Badge badge) {
        if (!hasBadge(badge)) {
            return 0;
        } else {
            return badges.get(badge);
        }
    }

    @Override
    public Map<Badge, Integer> getBadges() {
        return badges;
    }

    @Override
    public void setBadges(Map<Badge, Integer> v) {
        this.badges = v;
    }

    @Override
    public int getBlessTarget() {
        return blessTarget;
    }

    @Override
    public void setBlessTarget(int v) {
        this.blessTarget = v;
    }

    @Override
    public String getChatChannel() {
        return chatChannel;
    }

    @Override
    public void setChatChannel(String v) {
        this.chatChannel = v;
    }

    @Override
    public TextComponent getChatName() {
        TextComponent returns = new TextComponent(this.name);
        if (this.hasFlag(Flags.CHILD)) {
            returns.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(this.getRank().getName(plugin) + "\n" + ChatColor.AQUA + "CHILD").create()));
        } else {
            returns.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(this.getRank().getName(plugin)).create()));
        }
        return returns;
    }

    @Override
    public String getChatNameNoColor() {
        return ChatColor.stripColor(this.getChatNameNoHover());
    }

    @Override
    public String getChatNameNoHover() {
        return name;
    }

    @Override
    public TextComponent getChatNameStaff() {
        TextComponent returns = new TextComponent(this.name);
        String addon = "";
        if (this.hasNick) {
            addon += "\n" + ChatColor.AQUA + "Real name: " + this.name;
        }
        if (this.getTotalBans() != 0) {
            addon += "\n" + ChatColor.DARK_GRAY + "Bans: " + this.getTotalBans();
        }
        if (this.getTotalKicks() != 0) {
            addon += "\n" + ChatColor.GRAY + "Kicks: " + this.getTotalKicks();
        }
        if (this.getTotalHards() != 0) {
            addon += "\n" + ChatColor.DARK_GRAY + "Hard-Warns: " + this.getTotalHards();
        }
        if (this.getTotalSofts() != 0) {
            addon += "\n" + ChatColor.GRAY + "Soft-Warns " + this.getTotalSofts();
        }
        if (this.hasFlag(Flags.CHILD)) {
            returns.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(this.getRank().getName(plugin) + "\n" + ChatColor.AQUA + "CHILD" + addon)
                            .create()));
        } else {
            returns.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(this.getRank().getName(plugin) + addon).create()));
        }
        return returns;
    }

    @Override
    public ChatState getChatState() {
        return chatState;
    }

    @Override
    public void setChatState(ChatState v) {
        this.chatState = v;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public void setCity(String v) {
        this.city = v;
    }

    @Override
    public int getCombatLog() {
        return combatLog;
    }

    @Override
    public void setCombatLog(int value) {
        this.combatLog = value;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public void setCountry(String v) {
        this.country = v;
    }

    @Override
    public FishyBlock getCurrentFishyBlock() {
        return currentFishyBlock;
    }

    @Override
    public void setCurrentFishyBlock(FishyBlock v) {
        this.currentFishyBlock = v;
    }

    @Override
    public String getCurrentInventory() {
        return currentInventory;
    }

    @Override
    public void setCurrentInventory(String inv) {
        this.currentInventory = inv;
    }

    @Override
    public Zone getCurrentZone() {
        return currentZone;
    }

    @Override
    public void setCurrentZone(Zone zone) {
        this.currentZone = zone;
    }

    @Override
    public Block getFillBlock1() {
        return fillBlock1;
    }

    // block fill state
    @Override
    public void setFillBlock1(Block v) {
        this.fillBlock1 = v;
    }

    @Override
    public Block getFillBlock2() {
        return fillBlock2;
    }

    @Override
    public void setFillBlock2(Block v) {
        this.fillBlock2 = v;
    }

    @Override
    public int getFillBlockCounter() {
        return fillBlockCounter;
    }

    @Override
    public void setFillBlockCounter(int v) {
        this.fillBlockCounter = v;
    }

    @Override
    public int getFishyBuyCount() {
        return fishyBuyCount;
    }

    @Override
    public void setFishyBuyCount(int v) {
        this.fishyBuyCount = v;
    }

    @Override
    public boolean getFrozen() {
        return isFrozen;
    }

    @Override
    public void setFrozen(boolean v) {
        isFrozen = v;
    }

    @Override
    public int getGuardianRank() {
        return guardianRank;
    }

    @Override
    public void setGuardianRank(int v) {
        this.guardianRank = v;
    }

    @Override
    public GuardianState getGuardianState() {
        return guardianState;
    }

    @Override
    public void setGuardianState(GuardianState v) {
        this.guardianState = v;

        setTemporaryChatName(getNameColor() + getRealName());
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String v) {
        this.host = v;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int v) {
        this.id = v;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public void setIp(String v) {
        this.ip = v;
    }

    @Override
    public boolean getIsStaff() {
        return Staff;
    }

    @Override
    public String getKeyword() {
        return keyword;
    }

    @Override
    public void setKeyword(String v) {
        this.keyword = v;
    }

    @Override
    public String getLastMessenger() {
        return lastMessenger;
    }

    @Override
    public void setLastMessenger(String messenger) {
        this.lastMessenger = messenger;
    }

    @Override
    public long getLastOnlineActivity() {
        return lastOnlineActivity;
    }

    @Override
    public void setLastOnlineActivity(long a) {
        lastOnlineActivity = a;
    }

    @Override
    public Location getLastPos() {
        return this.lastpos;
    }

    @Override
    public void setLastPos(Location pos) {
        this.lastpos = pos;
    }

    @Override
    public GenericPlayer getMentor() {
        return mentor;
    }

    @Override
    public void setMentor(GenericPlayer v) {
        this.mentor = v;
    }

    @Override
    public PlayerMute getMute() {
        return this.mute;
    }

    @Override
    public void setMute(PlayerMute p0) {
        this.mute = p0;
    }

    @Override
    public ChatColor getNameColor() {
        if (hasFlag(Flags.SOFTWARNED)) {
            return ChatColor.GRAY;
        } else if (hasFlag(Flags.HARDWARNED)) {
            return ChatColor.GRAY;
        } else if (hasFlag(Flags.CHILD)) {
            return ChatColor.AQUA;
        }

        if (rank == null) {
            return ChatColor.WHITE;
        } else if (rank == Rank.GUARDIAN) {
            switch (guardianState) {
                case ACTIVE:
                    return plugin.getRankColor(Rank.GUARDIAN);
                case INACTIVE:
                case QUEUED:
                    return ChatColor.GOLD;
                default:
            }
        }

        return plugin.getRankColor(rank);
    }

    @Override
    public boolean getNewChunk() {
        return newChunk;
    }

    @Override
    public void setNewChunk(boolean value) {
        this.newChunk = value;
    }

    @Override
    public FishyBlock getNewFishyBlock() {
        return newFishyBlock;
    }

    // Fishy block state
    @Override
    public void setNewFishyBlock(FishyBlock v) {
        this.newFishyBlock = v;
    }

    @Override
    public Nickname getNickname() {
        return this.nickname;
    }

    @Override
    public String getPasswordHash() {
        return password;
    }

    @Override
    public void setPasswordHash(String v) {
        password = v;
    }

    // non-persistent state methods

    @Override
    public int getPlayTime() {
        return playTime;
    }

    @Override
    public void setPlayTime(int v) {
        this.playTime = v;
    }

    @Override
    public Tregmine getPlugin() {
        return plugin;
    }

    @Override
    public QuitCause getQuitCause() {
        return this.causeofquit;
    }

    @Override
    public void setQuitCause(QuitCause q) {
        this.causeofquit = q;
    }

    @Override
    public String getQuitMessage() {
        return quitMessage;
    }

    @Override
    public void setQuitMessage(String v) {
        this.quitMessage = v;
    }

    @Override
    public Rank getRank() {
        if (isTemporaryRank) {
            return temporaryRank;
        } else {
            return rank;
        }
    }

    @Override
    public void setRank(Rank v) {
        this.rank = v;
        if (v == Rank.GUARDIAN || v == Rank.JUNIOR_ADMIN || v == Rank.SENIOR_ADMIN || v == Rank.CODER) {
            this.Staff = true;
        }
        setTemporaryChatName(getNameColor() + getRealName());
    }

    @Override
    public String getRealName() {
        return realName;
    }

    @Override
    public List<String[]> getReports() {
        return this.reports;
    }

    @Override
    public int getReportTotal() {
        return this.reports.size();
    }

    @Override
    public UUID getStoredUuid() {
        return storedUuid;
    }

    @Override
    public void setStoredUuid(UUID v) {
        this.storedUuid = v;
    }

    @Override
    public GenericPlayer getStudent() {
        return student;
    }

    @Override
    public void setStudent(GenericPlayer v) {
        this.student = v;
    }

    @Override
    public int getTargetZoneId() {
        return targetZoneId;
    }

    @Override
    public void setTargetZoneId(int v) {
        this.targetZoneId = v;
    }

    @Override
    public int getTimeOnline() {
        return (int) ((new Date().getTime() - loginTime.getTime()) / 1000L);
    }

    @Override
    public int getTotalBans() {
        return this.bans;
    }

    @Override
    public void setTotalBans(int total) {
        this.bans = total;
    }

    @Override
    public int getTotalHards() {
        return this.hardwarns;
    }

    @Override
    public void setTotalHards(int total) {
        this.hardwarns = total;
    }

    @Override
    public int getTotalKicks() {
        return this.kicks;
    }

    @Override
    public void setTotalKicks(int total) {
        this.kicks = total;
    }

    @Override
    public int getTotalSofts() {
        return this.softwarns;
    }

    @Override
    public void setTotalSofts(int total) {
        this.softwarns = total;
    }

    @Override
    public Rank getTrueRank() {
        return rank;
    }

    @Override
    public Block getZoneBlock1() {
        return zoneBlock1;
    }

    // zones state
    @Override
    public void setZoneBlock1(Block v) {
        this.zoneBlock1 = v;
    }

    @Override
    public Block getZoneBlock2() {
        return zoneBlock2;
    }

    @Override
    public void setZoneBlock2(Block v) {
        this.zoneBlock2 = v;
    }

    @Override
    public int getZoneBlockCounter() {
        return zoneBlockCounter;
    }

    @Override
    public void setZoneBlockCounter(int v) {
        this.zoneBlockCounter = v;
    }

    @Override
    public void gotoWorld(Player player, Location loc, String success, String failure) {
        World world = loc.getWorld();
        Chunk chunk = world.getChunkAt(loc);
        world.loadChunk(chunk);
        if (world.isChunkLoaded(chunk)) {
            plugin.getPlayer(player).teleportWithHorse(loc);
            player.sendMessage(success);
        } else {
            player.sendMessage(failure);
        }
    }

    @Override
    public boolean hasBadge(Badge badge) {
        return badges.containsKey(badge);
    }

    /**
     * Returns true or false if the player has permission for that block
     *
     * @param loc    - Location of the block in question
     * @param punish - Should it return an error message and set fire ticks
     * @return true or false
     */
    @Override
    public boolean hasBlockPermission(Location loc, boolean punish) {
        ZoneWorld world = plugin.getWorld(loc.getWorld());
        Point point = new Point(loc.getBlockX(), loc.getBlockZ());

        Zone zone = world.findZone(point);
        Lot lot = world.findLot(point);

        Zone currentZone = this.getCurrentZone();
        if (currentZone == null || !currentZone.contains(point)) {
            currentZone = world.findZone(point);
            this.setCurrentZone(currentZone);
        }

        if (this.hasFlag(GenericPlayer.Flags.HARDWARNED)) {
            if (punish == true) {
                this.setFireTicks(100);
                this.sendMessage(ChatColor.RED + "[" + zone.getName() + "] " + "You are hardwarned!");
            }
            return false;
        }

        if (this.getRank() == Rank.TOURIST) {
            return false;
            // Don't punish as that's just cruel ;p
        }

        if (zone == null) { // Is in the wilderness - So return true
            return true;
        }

        if (this.getRank().canModifyZones()) { // Lets people with
            // canModifyZones have block
            // permission
            return true;
        }

        Zone.Permission perm = zone.getUser(this);

        if (perm == Zone.Permission.Banned) { // If banned then return false
            if (punish == true) {
                this.setFireTicks(100);
                this.sendMessage(ChatColor.RED + "[" + zone.getName() + "] " + "You are banned from this zone!");
            }
            return false;
        }

        if (lot == null && (perm == Zone.Permission.Maker || perm == Zone.Permission.Owner)) { // If
            // allowed/maker/owner
            // and
            // not
            // in
            // a
            // lot
            // :
            // return
            // true
            return true;
        }

        if (lot == null && zone.getPlaceDefault()) { // If placeDefault and not
            // in a lot : return
            // true
            return true;
        }

        if (lot != null && perm == Zone.Permission.Owner && zone.isCommunist()) { // If
            // communist
            // zone
            // return
            // true
            return true;
        }

        if (lot != null && lot.isOwner(this)) { // If is lot owner
            return true;
        }

        if (lot != null && lot.hasFlag(Lot.Flags.FREE_BUILD)) {
            return true;
        }

        if (punish == true) {
            if (lot != null && zone != null) { // Lot Error Message

                this.setFireTicks(100);
                this.sendMessage(ChatColor.RED + "[" + currentZone.getName() + "] "
                        + "You do not have sufficient permissions in " + lot.getName() + ".");

            } else { // Zone Error Message

                this.setFireTicks(100);
                this.sendMessage(ChatColor.RED + "[" + currentZone.getName() + "] "
                        + "You do not have sufficient permissions in " + zone.getName() + ".");

            }
        }

        return false; // If they don't fit into any of that. Return false
    }

    @Override
    public boolean hasCommandStatus(CommandStatus status) {
        return this.commandstatus.contains(status);
    }

    @Override
    public boolean hasFlag(Flags flag) {
        return flags.contains(flag);
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean hasNick() {
        return this.hasNick;
    }

    @Override
    public boolean hasProperty(Property prop) {
        return properties.contains(prop);
    }

    // convenience methods
    @Override
    public void hidePlayer(GenericPlayer player) {
        hidePlayer(player.getDelegate());
    }

    @Override
    public boolean isAfk() {
        return this.afk;
    }

    @Override
    public void setAfk(boolean value) {
        if (value == true) {
            this.afk = true;
            if (!this.isHidden()) {
                this.plugin.broadcast(new TextComponent(ITALIC + ""), getChatName(),
                        new TextComponent(RESET + "" + BLUE + " is now afk."));
                this.plugin.getDiscordDelegate().getChatChannel().sendMessage("**" + getChatNameNoColor() + "** is now afk.").complete();
            }
            String oldname = getChatNameNoHover();
            this.namePreAfkAppendage = oldname;
            setTemporaryChatName(GRAY + "[AFK] " + RESET + oldname);
        } else if (value == false) {
            final long currentTime = System.currentTimeMillis();
            this.setLastOnlineActivity(currentTime);
            this.afk = false;
            setTemporaryChatName(this.namePreAfkAppendage);
            if (!this.isHidden()) {
                this.plugin.broadcast(new TextComponent(ITALIC + ""), getChatName(),
                        new TextComponent(RESET + "" + GREEN + " is no longer afk."));
                if (this.plugin.discordEnabled()) {
                    this.plugin.getDiscordDelegate().getChatChannel().sendMessage("**" + getChatNameNoColor() + "** is no longer afk.").complete();
                }
            }
        }
    }

    @Override
    public boolean isCombatLogged() {
        return combatLog > 0;
    }

    @Override
    public boolean isCurseWarned() {
        return this.CurseWarned;
    }

    @Override
    public void setCurseWarned(boolean a) {
        this.CurseWarned = a;
    }

    @Override
    public boolean isHidden() {
        return this.hasFlag(Flags.INVISIBLE);
    }

    @Override
    public boolean isInVanillaWorld() {
        return this.getWorld() == this.plugin.getVanillaWorld() || this.getWorld() == this.plugin.getVanillaNether()
                || this.getWorld() == this.plugin.getVanillaEnd();
    }

    @Override
    public boolean isMuted() {
        return this.muted;
    }

    @Override
    public void setMuted(boolean p0) {
        this.muted = p0;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean v) {
        this.valid = v;
    }

    /*
     * Load an already existing inventory
     *
     * @param name - Name of the inventory
     *
     * @param save - Same current inventory
     */
    @Override
    public void loadInventory(String name, boolean save) {
        try (IContext ctx = plugin.createContext()) {
            IInventoryDAO dao = ctx.getInventoryDAO();

            if (save) {
                this.saveInventory(currentInventory);
            }

            boolean firstTime = false;

            int id3;
            id3 = dao.fetchInventory(this, name, "main");
            while (id3 == -1) {
                dao.createInventory(this, name, "main");
                plugin.getLogger().info("INVENTORY: Creating");
                id3 = dao.fetchInventory(this, name, "main");
                firstTime = true;
            }

            int id4;
            id4 = dao.fetchInventory(this, name, "armour");
            while (id4 == -1) {
                dao.createInventory(this, name, "armour");
                plugin.getLogger().info("INVENTORY: Creating");
                id4 = dao.fetchInventory(this, name, "armour");
                firstTime = true;
            }

            int id5;
            id5 = dao.fetchInventory(this, name, "ender");
            while (id5 == -1) {
                dao.createInventory(this, name, "ender");
                plugin.getLogger().info("INVENTORY: Creating");
                id5 = dao.fetchInventory(this, name, "ender");
                firstTime = true;
            }

            if (firstTime) {
                this.saveInventory(name);
            }

            this.getInventory().clear();
            this.getInventory().setHelmet(null);
            this.getInventory().setChestplate(null);
            this.getInventory().setLeggings(null);
            this.getInventory().setBoots(null);
            this.getEnderChest().clear();

            dao.loadInventory(this, id3, "main");
            dao.loadInventory(this, id4, "armour");
            dao.loadInventory(this, id5, "ender");

            this.currentInventory = name;

            IPlayerDAO playerDAO = ctx.getPlayerDAO();
            playerDAO.updatePlayer(this);

        } catch (DAOException e) {
            plugin.getLogger().info("INVENTORY ERROR: Trying to load " + this.getName() + " inventory named: " + name);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChatColor RankColor() {
        // Gives admins, guardians a chat color
        if (rank == Rank.GUARDIAN) {
            return ChatColor.GREEN;
        } else if (rank == Rank.JUNIOR_ADMIN || rank == Rank.SENIOR_ADMIN) {
            return ChatColor.GOLD;
        } else {
            return ChatColor.GRAY;
        }

    }

    @Override
    public void removeCommandStatus(CommandStatus status) {
        this.commandstatus.remove(status);
    }

    @Override
    public void removeFlag(Flags flag) {
        flags.remove(flag);
    }

    @Override
    public void removeProperty(Property prop) {
        properties.remove(prop);
    }

    @Override
    public void resetTimeOnline() {
        loginTime = new Date();
    }

    /*
     * Save the inventory specified, if null - saves current inventory.
     *
     * @param name - Name of the new inventory
     */
    @Override
    public void saveInventory(String name) {
        String inventory = name;
        if (name == null) {
            inventory = this.currentInventory;
        }

        try (IContext ctx = plugin.createContext()) {
            IInventoryDAO dao = ctx.getInventoryDAO();

            int id;
            id = dao.fetchInventory(this, inventory, "main");
            while (id == -1) {
                dao.createInventory(this, inventory, "main");
                plugin.getLogger().info("INVENTORY: Creating");
                id = dao.fetchInventory(this, inventory, "main");
            }

            dao.saveInventory(this, id, "main");

            int id2;
            id2 = dao.fetchInventory(this, inventory, "armour");
            while (id2 == -1) {
                dao.createInventory(this, inventory, "armour");
                plugin.getLogger().info("INVENTORY: Creating");
                id2 = dao.fetchInventory(this, inventory, "armour");
            }

            dao.saveInventory(this, id2, "armour");

            int id3;
            id3 = dao.fetchInventory(this, inventory, "ender");
            while (id3 == -1) {
                dao.createInventory(this, inventory, "ender");
                plugin.getLogger().info("INVENTORY: Creating");
                id3 = dao.fetchInventory(this, inventory, "ender");
            }

            dao.saveInventory(this, id3, "ender");
        } catch (DAOException e) {
            plugin.getLogger().info("INVENTORY ERROR: Trying to save " + this.getName() + " inventory named: " + name);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendNotification(Notification notif) {
        playSound(getLocation(), notif.getSound(), 2F, 1F);
    }

    /**
     * Sends the player a notification along with an associated message.
     * <p>
     * <p>
     * If the message is <b>null</b> or equal to "", the message won't send,
     * however the notification will still play.
     * <p>
     * If the notification is <b>null</b>, and the message is not if will send
     * the player the message.
     *
     * @param notif   - The notification to send to the player
     * @param message - The message to send the player with the notification
     * @throws IllegalArgumentException if both notif and message are null
     */
    @Override
    public void sendNotification(Notification notif, BaseComponent... message) {
        if (notif != null && notif != Notification.NONE) {
            playSound(getLocation(), notif.getSound(), 2F, 1F);
            sendMessage(message);
        } else {
            sendMessage(message);
        }
    }

    @Override
    public void sendNotification(Notification notif, BaseComponent message) {
        if (notif != null && notif != Notification.NONE) {
            playSound(getLocation(), notif.getSound(), 2F, 1F);
            sendMessage(message);
        } else {
            sendMessage(message);
        }
    }

    @Override
    public void setAlerted(boolean a) {
        this.alertedAfk = a;
    }

    @Override
    public void setCommandStatus(CommandStatus status) {
        this.commandstatus.add(status);
    }

    @Override
    public void setCurrentTexture(String url) {
        /*
         * if (url == null) { this.texture =
		 * "https://dl.dropbox.com/u/5405236/mc/df.zip"; }
		 *
		 * if (!url.equals(this.texture)) { this.texture = url;
		 * setTexturePack(url); }
		 */
    }

    @Override
    public void setDeathCause(String a) {
        this.deathcause = a;
    }

    @Override
    public void setFlag(Flags flag) {
        flags.add(flag);
    }

    @Override
    public void setNick(Nickname n) {
        this.nickname = n;
        this.hasNick = true;
        this.name = n.getNickname();
    }

    @Override
    public void setPassword(String newPassword) {
        password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
    }

    @Override
    public void setProperty(Property prop) {
        properties.add(prop);
    }

    @Override
    public void setSilentAfk(boolean value) {
        if (this.isHidden()) {
            return;
        }
        if (value == true) {
            this.afk = true;
            String oldname = getChatNameNoHover();
            setTemporaryChatName(GRAY + "[AFK] " + RESET + oldname);
        } else if (value == false) {
            final long currentTime = System.currentTimeMillis();
            this.setLastOnlineActivity(currentTime);
            this.afk = false;
            setTemporaryChatName(getNameColor() + getRealName());
        } else {
            return;
        }
    }

    @Override
    public void setStaff(boolean v) {
        this.Staff = v;
    }

    // -----------------------------//
    // Tregmine Inventory Handling //
    // -----------------------------//

    @Override
    public void setTemporaryChatName(String name) {
        this.name = name;

        if (getDelegate() != null) {
            if (getChatNameNoHover().length() > 16) {
                setPlayerListName(name.substring(0, 15));
            } else {
                setPlayerListName(name);
            }
        }
    }

    @Override
    public void setTemporaryRank(Rank v) {
        this.temporaryRank = v;
        this.isTemporaryRank = true;
        if (v == Rank.GUARDIAN || v == Rank.JUNIOR_ADMIN || v == Rank.SENIOR_ADMIN || v == Rank.CODER) {
            this.Staff = true;
        }
        setTemporaryChatName(getNameColor() + getRealName());
    }

    @Override
    public void showPlayer(GenericPlayer player) {
        showPlayer(player.getDelegate());
    }

    @Override
    public void teleportWithHorse(Location loc) {
        World cWorld = loc.getWorld();
        String[] worldNamePortions = cWorld.getName().split("_");

        Entity v = getVehicle();
        if (v != null && v instanceof Horse) {
            if (!worldNamePortions[0].equalsIgnoreCase("world")) {
                this.sendMessage(ChatColor.RED + "Can not teleport with horse! Sorry!");
                return;
            }

            Horse horse = (Horse) v;
            horse.eject();
            horse.teleport(loc);
            teleport(loc);
            horse.addPassenger(getDelegate());
        } else {
            teleport(loc);
        }

        if (worldNamePortions[0].equalsIgnoreCase("world")) {
            this.loadInventory("survival", true);
        } else {
            this.loadInventory(worldNamePortions[0], true);
        }
    }

    @Override
    public Zone updateCurrentZone() {
        Point pos = new Point(this.getLocation().getBlockX(), this.getLocation().getBlockZ());
        Zone localZone = this.getCurrentZone();

        if (localZone == null || !localZone.contains(pos)) {
            ZoneWorld world = plugin.getWorld(this.getLocation().getWorld());
            localZone = world.findZone(pos);
            this.setCurrentZone(localZone);
        }

        return currentZone;
    }

    @Override
    public boolean verifyPassword(String attempt) {
        return BCrypt.checkpw(attempt, password);
    }

}
