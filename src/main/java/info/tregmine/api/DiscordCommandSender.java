package info.tregmine.api;

import info.tregmine.Tregmine;
import info.tregmine.api.returns.BooleanStringReturn;
import info.tregmine.discord.DiscordDelegate;
import info.tregmine.zones.Zone;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Created by eric on 7/7/17.
 */
public class DiscordCommandSender implements GenericPlayer, CommandSender {

    private DiscordDelegate discord;

    private Rank rank;
    private Guild guild;
    private Member member;

    private boolean hidden = false;
    private boolean muted = false;

    private GenericPlayer student = null;

    private MessageChannel responseChannel;

    public DiscordCommandSender(DiscordDelegate delegate, Rank rank, Member member, MessageChannel responseChannel) {
        this.discord = delegate;
        this.rank = rank;
        this.member = member;
        this.guild = this.member.getGuild();
        this.responseChannel = responseChannel;
    }

    @Override
    public Set<Flags> getFlags() {
        return new HashSet<>();
    }

    @Override
    public void abandonConversation(Conversation p0) {

    }

    @Override
    public void abandonConversation(Conversation p0, ConversationAbandonedEvent p1) {

    }

    @Override
    public void acceptConversationInput(String p0) {

    }

    @Override
    public PermissionAttachment addAttachment(Plugin p0) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin p0, int p1) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin p0, String p1, boolean p2) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin p0, String p1, boolean p2, int p3) {
        return null;
    }

    @Override
    public boolean addPotionEffect(PotionEffect p0) {
        return true;
    }

    @Override
    public boolean addPotionEffect(PotionEffect p0, boolean p1) {
        return true;
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> p0) {
        return true;
    }

    @Override
    public boolean beginConversation(Conversation p0) {
        return false;
    }

    @Override
    public boolean canSee(Player p0) {
        return false;
    }

    @Override
    public void chat(String p0) {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public CraftPlayer craftPlayer() {
        return null;
    }

    @Override
    public void damage(double p0) {

    }

    @Override
    public void damage(double p0, Entity p1) {

    }

    @Override
    public boolean eject() {
        return true;
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return null;
    }

    @Override
    public InetSocketAddress getAddress() {
        return null;
    }

    @Override
    public boolean getAllowFlight() {
        return true;
    }

    @Override
    public void setAllowFlight(boolean p0) {

    }

    @Override
    public Location getBedSpawnLocation() {
        return null;
    }

    @Override
    public void setBedSpawnLocation(Location p0) {

    }

    @Override
    public boolean getCanPickupItems() {
        return false;
    }

    @Override
    public void setCanPickupItems(boolean p0) {

    }

    @Override
    public Location getCompassTarget() {
        return null;
    }

    @Override
    public void setCompassTarget(Location p0) {

    }

    @Override
    public String getCustomName() {
        return this.member.getNickname() == null
                ? this.member.getEffectiveName()
                : this.member.getNickname();
    }

    @Override
    public void setCustomName(String p0) {
        if (this.guild.getSelfMember().canInteract(this.member)) {
            this.guild.getController().setNickname(this.member, p0).complete();
        }
    }

    @Override
    public Player getDelegate() {
        return null;
    }

    @Override
    public void setDelegate(Player v) {

    }

    @Override
    public String getDisplayName() {
        return this.getCustomName();
    }

    @Override
    public void setDisplayName(String p0) {
        this.setCustomName(p0);
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public Inventory getEnderChest() {
        return null;
    }

    @Override
    public int getEntityId() {
        return 0;
    }

    @Override
    public EntityEquipment getEquipment() {
        return null;
    }

    @Override
    public float getExhaustion() {
        return 0;
    }

    @Override
    public void setExhaustion(float p0) {

    }

    @Override
    public float getExp() {
        return 0;
    }

    @Override
    public void setExp(float p0) {

    }

    @Override
    public int getExpToLevel() {
        return 0;
    }

    @Override
    public double getEyeHeight() {
        return 0;
    }

    @Override
    public double getEyeHeight(boolean p0) {
        return 0;
    }

    @Override
    public Location getEyeLocation() {
        return null;
    }

    @Override
    public float getFallDistance() {
        return 0;
    }

    @Override
    public void setFallDistance(float p0) {

    }

    @Override
    public int getFireTicks() {
        return 0;
    }

    @Override
    public void setFireTicks(int p0) {

    }

    @Override
    public long getFirstPlayed() {
        return 0;
    }

    @Override
    public float getFlySpeed() {
        return 0;
    }

    @Override
    public void setFlySpeed(float p0) {

    }

    @Override
    public int getFoodLevel() {
        return 0;
    }

    @Override
    public void setFoodLevel(int p0) {

    }

    @Override
    public GameMode getGameMode() {
        return GameMode.SPECTATOR;
    }

    @Override
    public void setGameMode(GameMode p0) {

    }

    @Override
    public EntityPlayer getHandle() {
        return null;
    }

    @Override
    public double getHealth() {
        return 20;
    }

    @Override
    public void setHealth(double p0) {

    }

    @Override
    public double getHealthScale() {
        return 0;
    }

    @Override
    public void setHealthScale(double p0) {

    }

    @Override
    public PlayerInventory getInventory() {
        return null;
    }

    @Override
    public ItemStack getItemInHand() {
        return null;
    }

    @Override
    public void setItemInHand(ItemStack p0) {

    }

    @Override
    public ItemStack getItemOnCursor() {
        return null;
    }

    @Override
    public void setItemOnCursor(ItemStack p0) {

    }

    @Override
    public Player getKiller() {
        return null;
    }

    @Override
    public double getLastDamage() {
        return 0;
    }

    @Override
    public void setLastDamage(double p0) {

    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return null;
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent p0) {

    }

    @Override
    public long getLastPlayed() {
        return 0;
    }

    @Override
    public Entity getLeashHolder() {
        return null;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public void setLevel(int p0) {

    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return null;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public Location getLocation(Location p0) {
        return null;
    }

    @Override
    public int getMaxFireTicks() {
        return 0;
    }

    @Override
    public double getMaxHealth() {
        return 0;
    }

    @Override
    public void setMaxHealth(double p0) {

    }

    @Override
    public int getMaximumAir() {
        return 0;
    }

    @Override
    public void setMaximumAir(int p0) {

    }

    @Override
    public int getMaximumNoDamageTicks() {
        return 0;
    }

    @Override
    public void setMaximumNoDamageTicks(int p0) {

    }

    @Override
    public List<MetadataValue> getMetadata(String p0) {
        return null;
    }

    @Override
    public String getName() {
        return this.member.getUser().getName();
    }

    @Override
    public List<Entity> getNearbyEntities(double p0, double p1, double p2) {
        return null;
    }

    @Override
    public int getNoDamageTicks() {
        return 0;
    }

    @Override
    public void setNoDamageTicks(int p0) {

    }

    @Override
    public InventoryView getOpenInventory() {
        return null;
    }

    @Override
    public List<Entity> getPassengers() {
        return null;
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public String getPlayerListName() {
        return null;
    }

    @Override
    public void setPlayerListName(String p0) {

    }

    @Override
    public long getPlayerTime() {
        return 0;
    }

    @Override
    public long getPlayerTimeOffset() {
        return 0;
    }

    @Override
    public WeatherType getPlayerWeather() {
        return null;
    }

    @Override
    public void setPlayerWeather(WeatherType p0) {

    }

    @Override
    public int getRemainingAir() {
        return 0;
    }

    @Override
    public void setRemainingAir(int p0) {

    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return false;
    }

    @Override
    public void setRemoveWhenFarAway(boolean p0) {

    }

    @Override
    public float getSaturation() {
        return 0;
    }

    @Override
    public void setSaturation(float p0) {

    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public void setScoreboard(Scoreboard p0) {

    }

    @Override
    public Server getServer() {
        return this.discord.getPlugin().getServer();
    }

    @Override
    public int getSleepTicks() {
        return 0;
    }

    @Override
    public Player.Spigot getSpigot() {
        return null;
    }

    @Override
    public int getTicksLived() {
        return 0;
    }

    @Override
    public void setTicksLived(int p0) {

    }

    @Override
    public int getTotalExperience() {
        return 0;
    }

    @Override
    public void setTotalExperience(int p0) {

    }

    @Override
    public EntityType getType() {
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return null;
    }

    @Override
    public Entity getVehicle() {
        return null;
    }

    @Override
    public Vector getVelocity() {
        return null;
    }

    @Override
    public void setVelocity(Vector p0) {

    }

    @Override
    public float getWalkSpeed() {
        return 0;
    }

    @Override
    public void setWalkSpeed(float p0) {

    }

    @Override
    public World getWorld() {
        return this.discord.getPlugin().getServer().getWorld("world");
    }

    @Override
    public void giveExp(int p0) {

    }

    @Override
    public void giveExpLevels(int p0) {

    }

    @Override
    public boolean hasLineOfSight(Entity p0) {
        return false;
    }

    @Override
    public boolean hasMetadata(String p0) {
        return false;
    }

    @Override
    public boolean hasPermission(String p0) {
        return this.rank == Rank.SENIOR_ADMIN;
    }

    @Override
    public boolean hasPermission(Permission p0) {
        return this.rank == Rank.SENIOR_ADMIN;
    }

    @Override
    public boolean hasPlayedBefore() {
        return true;
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType p0) {
        return false;
    }

    @Override
    public void hidePlayer(Player p0) {

    }

    @Override
    public void incrementStatistic(Statistic p0) {

    }

    @Override
    public void incrementStatistic(Statistic p0, int p1) {

    }

    @Override
    public void incrementStatistic(Statistic p0, Material p1) {

    }

    @Override
    public void incrementStatistic(Statistic p0, Material p1, int p2) {

    }

    @Override
    public boolean isBanned() {
        return false;
    }

    @Override
    public void setBanned(boolean p0) {

    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean isConversing() {
        return false;
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public void setCustomNameVisible(boolean p0) {

    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isFlying() {
        return false;
    }

    @Override
    public void setFlying(boolean p0) {

    }

    @Override
    public boolean isHealthScaled() {
        return false;
    }

    @Override
    public void setHealthScaled(boolean p0) {

    }

    @Override
    public boolean isInsideVehicle() {
        return false;
    }

    @Override
    public boolean isLeashed() {
        return false;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public boolean isOp() {
        return this.rank == Rank.SENIOR_ADMIN;
    }

    @Override
    public void setOp(boolean p0) {

    }

    @Override
    public boolean isPermissionSet(String p0) {
        return false;
    }

    @Override
    public boolean isPermissionSet(Permission p0) {
        return false;
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return false;
    }

    @Override
    public boolean isSleeping() {
        return false;
    }

    @Override
    public boolean isSleepingIgnored() {
        return false;
    }

    @Override
    public void setSleepingIgnored(boolean p0) {

    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public void setSneaking(boolean p0) {

    }

    @Override
    public boolean isSprinting() {
        return false;
    }

    @Override
    public void setSprinting(boolean p0) {

    }

    @Override
    public void addReport(String[] report) {

    }

    @Override
    public boolean alertedAfk() {
        return false;
    }

    @Override
    public void awardBadgeLevel(Badge badge, String message) {

    }

    @Override
    public BooleanStringReturn canBeHere(Location loc) {
        return null;
    }

    @Override
    public boolean canMentor() {
        return this.rank.canMentor();
    }

    @Override
    public boolean canVS() {
        return true;
    }

    @Override
    public String causeOfDeath() {
        return null;
    }

    @Override
    public void checkActivity() {

    }

    @Override
    public TextComponent decideVS(GenericPlayer canthey) {
        return null;
    }

    @Override
    public boolean getAfkKick() {
        return false;
    }

    @Override
    public void setAfkKick(boolean a) {

    }

    @Override
    public PermissionAttachment getAttachment() {
        return null;
    }

    @Override
    public void setAttachment(PermissionAttachment ment) {

    }

    @Override
    public int getBadgeLevel(Badge badge) {
        return 0;
    }

    @Override
    public Map<Badge, Integer> getBadges() {
        return null;
    }

    @Override
    public void setBadges(Map<Badge, Integer> v) {

    }

    @Override
    public int getBlessTarget() {
        return 0;
    }

    @Override
    public void setBlessTarget(int v) {

    }

    @Override
    public String getChatChannel() {
        return "GLOBAL";
    }

    @Override
    public void setChatChannel(String v) {

    }

    @Override
    public TextComponent getChatName() {
        TextComponent returns = new TextComponent(this.getChatNameNoHover());
        StringBuilder addon = new StringBuilder();
        addon.append("\n" + ChatColor.AQUA + ChatColor.ITALIC + "Discord" + ChatColor.RESET);
        addon.append("\n" + ChatColor.GRAY + "Name: " + this.member.getUser().getName() + '#' + this.member.getUser().getDiscriminator());
        addon.append("\n" + ChatColor.GRAY + "ID: " + this.member.getUser().getId());
        returns.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(this.getRank().getName(this.discord.getPlugin()) + addon).create()));
        return returns;
    }

    @Override
    public String getChatNameNoColor() {
        return this.getCustomName();
    }

    @Override
    public String getChatNameNoHover() {
        return this.discord.getPlugin().getRankColor(this.rank) + this.getCustomName();
    }

    @Override
    public TextComponent getChatNameStaff() {
        return this.getChatName();
    }

    @Override
    public ChatState getChatState() {
        return ChatState.CHAT;
    }

    @Override
    public void setChatState(ChatState v) {

    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public void setCity(String v) {

    }

    @Override
    public int getCombatLog() {
        return 0;
    }

    @Override
    public void setCombatLog(int value) {

    }

    @Override
    public String getCountry() {
        return "Atlantic Ocean";
    }

    @Override
    public void setCountry(String v) {

    }

    @Override
    public FishyBlock getCurrentFishyBlock() {
        return null;
    }

    @Override
    public void setCurrentFishyBlock(FishyBlock v) {

    }

    @Override
    public String getCurrentInventory() {
        return null;
    }

    @Override
    public void setCurrentInventory(String inv) {

    }

    @Override
    public Zone getCurrentZone() {
        return null;
    }

    @Override
    public void setCurrentZone(Zone zone) {

    }

    @Override
    public Block getFillBlock1() {
        return null;
    }

    @Override
    public void setFillBlock1(Block v) {

    }

    @Override
    public Block getFillBlock2() {
        return null;
    }

    @Override
    public void setFillBlock2(Block v) {

    }

    @Override
    public int getFillBlockCounter() {
        return 0;
    }

    @Override
    public void setFillBlockCounter(int v) {

    }

    @Override
    public int getFishyBuyCount() {
        return 0;
    }

    @Override
    public void setFishyBuyCount(int v) {

    }

    @Override
    public boolean getFrozen() {
        return false;
    }

    @Override
    public void setFrozen(boolean v) {

    }

    @Override
    public int getGuardianRank() {
        return 0;
    }

    @Override
    public void setGuardianRank(int v) {

    }

    @Override
    public GuardianState getGuardianState() {
        return null;
    }

    @Override
    public void setGuardianState(GuardianState v) {

    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public void setHost(String v) {

    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void setId(int v) {

    }

    @Override
    public String getIp() {
        return "0.0.0.0";
    }

    @Override
    public void setIp(String v) {

    }

    @Override
    public boolean getIsStaff() {
        return this.rank.isStaff();
    }

    @Override
    public String getKeyword() {
        return null;
    }

    @Override
    public void setKeyword(String v) {

    }

    @Override
    public String getLastMessenger() {
        return null;
    }

    @Override
    public void setLastMessenger(String messenger) {

    }

    @Override
    public long getLastOnlineActivity() {
        return 0;
    }

    @Override
    public void setLastOnlineActivity(long a) {

    }

    @Override
    public Location getLastPos() {
        return null;
    }

    @Override
    public void setLastPos(Location pos) {

    }

    @Override
    public GenericPlayer getMentor() {
        return null;
    }

    @Override
    public void setMentor(GenericPlayer v) {

    }

    @Override
    public PlayerMute getMute() {
        return null;
    }

    @Override
    public void setMute(PlayerMute p0) {

    }

    @Override
    public ChatColor getNameColor() {
        return ChatColor.getByChar(this.discord.convertRoleToMinecraftColor(this.discord.getTopRole(this.member.getRoles())));
    }

    @Override
    public boolean getNewChunk() {
        return true;
    }

    @Override
    public void setNewChunk(boolean value) {

    }

    @Override
    public FishyBlock getNewFishyBlock() {
        return null;
    }

    @Override
    public void setNewFishyBlock(FishyBlock v) {

    }

    @Override
    public void refreshPlayerList() {

    }

    @Override
    public Nickname getNickname() {
        return null;
    }

    @Override
    public void setNickname(Nickname n) {
        this.setCustomName(n.getNicknamePlaintext());
    }

    @Override
    public String getPasswordHash() {
        return null;
    }

    @Override
    public void setPasswordHash(String v) {

    }

    @Override
    public void removeNickname() {

    }

    @Override
    public int getPlayTime() {
        return 0;
    }

    @Override
    public void setPlayTime(int v) {

    }

    @Override
    public Tregmine getPlugin() {
        return this.discord.getPlugin();
    }

    @Override
    public QuitCause getQuitCause() {
        return null;
    }

    @Override
    public void setQuitCause(QuitCause q) {

    }

    @Override
    public String getQuitMessage() {
        return null;
    }

    @Override
    public void setQuitMessage(String v) {

    }

    @Override
    public Rank getRank() {
        return this.rank;
    }

    @Override
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public DiscordCommandSender refreshRank(Rank rank) {
        this.rank = rank;
        return this;
    }

    @Override
    public String getRealName() {
        return this.member.getUser().getName();
    }

    @Override
    public List<String[]> getReports() {
        return new ArrayList<>();
    }

    @Override
    public int getReportTotal() {
        return 0;
    }

    @Override
    public UUID getStoredUuid() {
        return UUID.randomUUID();
    }

    @Override
    public void setStoredUuid(UUID v) {

    }

    @Override
    public GenericPlayer getStudent() {
        return this.student;
    }

    @Override
    public void setStudent(GenericPlayer v) {
        this.student = v;
    }

    @Override
    public int getTargetZoneId() {
        return 0;
    }

    @Override
    public void setTargetZoneId(int v) {

    }

    @Override
    public int getTimeOnline() {
        return 0;
    }

    @Override
    public int getTotalBans() {
        return 0;
    }

    @Override
    public void setTotalBans(int total) {

    }

    @Override
    public int getTotalHards() {
        return 0;
    }

    @Override
    public void setTotalHards(int total) {

    }

    @Override
    public int getTotalKicks() {
        return 0;
    }

    @Override
    public void setTotalKicks(int total) {

    }

    @Override
    public int getTotalSofts() {
        return 0;
    }

    @Override
    public void setTotalSofts(int total) {

    }

    @Override
    public Rank getTrueRank() {
        return this.rank;
    }

    @Override
    public Block getZoneBlock1() {
        return null;
    }

    @Override
    public void setZoneBlock1(Block v) {

    }

    @Override
    public Block getZoneBlock2() {
        return null;
    }

    @Override
    public void setZoneBlock2(Block v) {

    }

    @Override
    public int getZoneBlockCounter() {
        return 0;
    }

    @Override
    public void setZoneBlockCounter(int v) {

    }

    @Override
    public void gotoWorld(Player player, Location loc, String success, String failure) {

    }

    @Override
    public boolean hasBadge(Badge badge) {
        return false;
    }

    @Override
    public boolean hasBlockPermission(Location loc, boolean punish) {
        return false;
    }

    @Override
    public boolean hasCommandStatus(CommandStatus status) {
        return false;
    }

    @Override
    public boolean hasFlag(Flags flag) {
        return false;
    }

    @Override
    public boolean hasNick() {
        return false;
    }

    @Override
    public boolean hasProperty(Property prop) {
        return false;
    }

    @Override
    public void hidePlayer(GenericPlayer player) {

    }

    @Override
    public boolean isAfk() {
        return false;
    }

    @Override
    public void setAfk(boolean value) {

    }

    @Override
    public boolean isCombatLogged() {
        return false;
    }

    @Override
    public boolean isCurseWarned() {
        return false;
    }

    @Override
    public void setCurseWarned(boolean a) {

    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public boolean isInVanillaWorld() {
        return false;
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
        return true;
    }

    @Override
    public void setValid(boolean v) {

    }

    @Override
    public boolean isWhitelisted() {
        return true;
    }

    @Override
    public void setWhitelisted(boolean p0) {

    }

    @Override
    public void kickPlayer(Tregmine instance, String p0) {
        if (this.guild.getSelfMember().canInteract(this.member)) {
            this.guild.getController().kick(this.member).queue(new java.util.function.Consumer<Void>() {
                @Override
                public void accept(Void aVoid) {
                    sendRawMessage(p0);
                }
            });
        }
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> p0) {
        return null;
    }

    @Override
    public boolean leaveVehicle() {
        return false;
    }

    @Override
    public void loadData() {

    }

    @Override
    public InventoryView openEnchanting(Location p0, boolean p1) {
        return null;
    }

    @Override
    public InventoryView openInventory(Inventory p0) {
        return null;
    }

    @Override
    public void openInventory(InventoryView p0) {

    }

    @Override
    public InventoryView openWorkbench(Location p0, boolean p1) {
        return null;
    }

    @Override
    public boolean performCommand(String p0) {
        return false;
    }

    @Override
    public void playEffect(EntityEffect p0) {

    }

    @Override
    public <T> void playEffect(Location p0, Effect p1, T p2) {

    }

    @Override
    public void playNote(Location p0, Instrument p1, Note p2) {

    }

    @Override
    public void playSound(Location p0, Sound p1, float p2, float p3) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @Override
    public void remove() {

    }

    @Override
    public void removeAttachment(PermissionAttachment p0) {

    }

    @Override
    public void removeMetadata(String p0, Plugin p1) {

    }

    @Override
    public void removePotionEffect(PotionEffectType p0) {

    }

    @Override
    public void resetMaxHealth() {

    }

    @Override
    public void resetPlayerTime() {

    }

    @Override
    public void resetPlayerWeather() {

    }

    @Override
    public void saveData() {

    }

    @Override
    public void sendMap(MapView p0) {

    }

    @Override
    public void sendPluginMessage(Plugin p0, String p1, byte[] p2) {

    }

    @Override
    public void sendRawMessage(String p0) {
        this.responseChannel.sendMessage(ChatColor.stripColor(p0)).complete();
    }

    @Override
    public void sendMessage(BaseComponent... a) {
        String message = "";
        for (BaseComponent component : a) {
            message += component.toPlainText();
        }
        this.sendRawMessage(message);
    }

    @Override
    public void sendMessage(String message) {
        this.sendRawMessage(message);
    }

    @Override
    public void sendMessage(String[] strArr) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            strBuilder.append(strArr[i] + " ");
        }
        this.sendMessage(strBuilder.toString());
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    @Override
    public void setBedSpawnLocation(Location p0, boolean p1) {

    }

    @Override
    public boolean setLeashHolder(Entity p0) {
        return false;
    }

    @Override
    public void setMetadata(String p0, MetadataValue p1) {

    }

    @Override
    public boolean addPassenger(Entity p0) {
        return false;
    }

    @Override
    public void setPermission(String p0) {

    }

    @Override
    public void setPlayerTime(long p0, boolean p1) {

    }

    @Override
    public void setResourcePack(String p0) {

    }

    @Override
    public boolean setWindowProperty(InventoryView.Property p0, int p1) {
        return false;
    }

    @Override
    public void showPlayer(Player p0) {

    }

    @Override
    public Player.Spigot spigot() {
        return null;
    }

    @Override
    public boolean teleport(Entity p0) {
        return false;
    }

    @Override
    public boolean teleport(Entity p0, PlayerTeleportEvent.TeleportCause p1) {
        return false;
    }

    @Override
    public boolean teleport(Location p0) {
        return false;
    }

    @Override
    public boolean teleport(Location p0, PlayerTeleportEvent.TeleportCause p1) {
        return false;
    }

    @Override
    public void loadInventory(String name, boolean save) {

    }

    @Override
    public ChatColor RankColor() {
        return this.discord.getPlugin().getRankColor(this.rank);
    }

    @Override
    public void removeCommandStatus(CommandStatus status) {

    }

    @Override
    public void removeFlag(Flags flag) {

    }

    @Override
    public void removeProperty(Property prop) {

    }

    @Override
    public void resetTimeOnline() {

    }

    @Override
    public void saveInventory(String name) {

    }

    @Override
    public void sendNotification(Notification notif) {

    }

    @Override
    public void sendNotification(Notification notif, BaseComponent... message) {
        this.sendMessage(message);
    }

    @Override
    public void sendNotification(Notification notif, BaseComponent message) {
        this.sendMessage(message);
    }

    @Override
    public void setAlerted(boolean a) {

    }

    @Override
    public void setCommandStatus(CommandStatus status) {

    }

    @Override
    public void setCurrentTexture(String url) {

    }

    @Override
    public void setDeathCause(String a) {

    }

    @Override
    public void setFlag(Flags flag) {

    }

    @Override
    public void setPassword(String newPassword) {

    }

    @Override
    public void setProperty(Property prop) {

    }

    @Override
    public void setSilentAfk(boolean value) {

    }

    @Override
    public void setStaff(boolean v) {

    }

    @Override
    public void setTemporaryChatName(String name) {
        this.setCustomName(name);
    }

    @Override
    public void showPlayer(GenericPlayer player) {

    }

    @Override
    public void teleportWithHorse(Location loc) {

    }

    @Override
    public Zone updateCurrentZone() {
        return null;
    }

    @Override
    public boolean verifyPassword(String attempt) {
        return false;
    }

    public DiscordCommandSender setResponseChannel(MessageChannel channel) {
        this.responseChannel = channel;
        return this;
    }
}
