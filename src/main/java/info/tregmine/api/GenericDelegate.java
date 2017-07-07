package info.tregmine.api;

import info.tregmine.Tregmine;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by ericrabil on 3/29/17.
 */
public interface GenericDelegate {
    void abandonConversation(org.bukkit.conversations.Conversation p0);

    void abandonConversation(org.bukkit.conversations.Conversation p0,
                             org.bukkit.conversations.ConversationAbandonedEvent p1);

    void acceptConversationInput(String p0);

    org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin p0);

    org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin p0, int p1);

    org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin p0, String p1,
                                                              boolean p2);

    org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin p0, String p1,
                                                              boolean p2, int p3);

    boolean addPotionEffect(org.bukkit.potion.PotionEffect p0);

    boolean addPotionEffect(org.bukkit.potion.PotionEffect p0, boolean p1);

    boolean addPotionEffects(java.util.Collection<org.bukkit.potion.PotionEffect> p0);

    void awardAchievement(org.bukkit.Achievement p0);

    boolean beginConversation(org.bukkit.conversations.Conversation p0);

    boolean canSee(org.bukkit.entity.Player p0);

    void chat(String p0);

    void closeInventory();

    CraftPlayer craftPlayer();

    void damage(double p0);

    void damage(double p0, org.bukkit.entity.Entity p1);

    boolean eject();

    java.util.Collection<org.bukkit.potion.PotionEffect> getActivePotionEffects();

    java.net.InetSocketAddress getAddress();

    boolean getAllowFlight();

    void setAllowFlight(boolean p0);

    org.bukkit.Location getBedSpawnLocation();

    void setBedSpawnLocation(org.bukkit.Location p0);

    boolean getCanPickupItems();

    void setCanPickupItems(boolean p0);

    org.bukkit.Location getCompassTarget();

    void setCompassTarget(org.bukkit.Location p0);

    String getCustomName();

    void setCustomName(String p0);

    org.bukkit.entity.Player getDelegate();

    void setDelegate(org.bukkit.entity.Player v);

    String getDisplayName();

    void setDisplayName(String p0);

    java.util.Set<org.bukkit.permissions.PermissionAttachmentInfo> getEffectivePermissions();

    org.bukkit.inventory.Inventory getEnderChest();

    int getEntityId();

    org.bukkit.inventory.EntityEquipment getEquipment();

    float getExhaustion();

    void setExhaustion(float p0);

    float getExp();

    void setExp(float p0);

    int getExpToLevel();

    double getEyeHeight();

    double getEyeHeight(boolean p0);

    org.bukkit.Location getEyeLocation();

    float getFallDistance();

    void setFallDistance(float p0);

    int getFireTicks();

    void setFireTicks(int p0);

    long getFirstPlayed();

    float getFlySpeed();

    void setFlySpeed(float p0);

    int getFoodLevel();

    void setFoodLevel(int p0);

    org.bukkit.GameMode getGameMode();

    void setGameMode(org.bukkit.GameMode p0);

    EntityPlayer getHandle();

    double getHealth();

    void setHealth(double p0);

    double getHealthScale();

    void setHealthScale(double p0);

    org.bukkit.inventory.PlayerInventory getInventory();

    org.bukkit.inventory.ItemStack getItemInHand();

    void setItemInHand(org.bukkit.inventory.ItemStack p0);

    org.bukkit.inventory.ItemStack getItemOnCursor();

    void setItemOnCursor(org.bukkit.inventory.ItemStack p0);

    org.bukkit.entity.Player getKiller();

    double getLastDamage();

    void setLastDamage(double p0);

    org.bukkit.event.entity.EntityDamageEvent getLastDamageCause();

    void setLastDamageCause(org.bukkit.event.entity.EntityDamageEvent p0);

    long getLastPlayed();

    org.bukkit.entity.Entity getLeashHolder();

    int getLevel();

    void setLevel(int p0);

    java.util.Set<String> getListeningPluginChannels();

    org.bukkit.Location getLocation();

    org.bukkit.Location getLocation(org.bukkit.Location p0);

    int getMaxFireTicks();

    double getMaxHealth();

    void setMaxHealth(double p0);

    int getMaximumAir();

    void setMaximumAir(int p0);

    int getMaximumNoDamageTicks();

    void setMaximumNoDamageTicks(int p0);

    java.util.List<org.bukkit.metadata.MetadataValue> getMetadata(String p0);

    String getName();

    java.util.List<org.bukkit.entity.Entity> getNearbyEntities(double p0, double p1, double p2);

    int getNoDamageTicks();

    void setNoDamageTicks(int p0);

    org.bukkit.inventory.InventoryView getOpenInventory();

    java.util.List<org.bukkit.entity.Entity> getPassengers();

    org.bukkit.entity.Player getPlayer();

    String getPlayerListName();

    void setPlayerListName(String p0);

    long getPlayerTime();

    long getPlayerTimeOffset();

    org.bukkit.WeatherType getPlayerWeather();

    void setPlayerWeather(org.bukkit.WeatherType p0);

    int getRemainingAir();

    void setRemainingAir(int p0);

    boolean getRemoveWhenFarAway();

    void setRemoveWhenFarAway(boolean p0);

    float getSaturation();

    void setSaturation(float p0);

    org.bukkit.scoreboard.Scoreboard getScoreboard();

    void setScoreboard(org.bukkit.scoreboard.Scoreboard p0);

    org.bukkit.Server getServer();

    int getSleepTicks();

    Player.Spigot getSpigot();

    int getTicksLived();

    void setTicksLived(int p0);

    int getTotalExperience();

    void setTotalExperience(int p0);

    org.bukkit.entity.EntityType getType();

    java.util.UUID getUniqueId();

    org.bukkit.entity.Entity getVehicle();

    org.bukkit.util.Vector getVelocity();

    void setVelocity(org.bukkit.util.Vector p0);

    float getWalkSpeed();

    void setWalkSpeed(float p0);

    org.bukkit.World getWorld();

    void giveExp(int p0);

    void giveExpLevels(int p0);

    boolean hasLineOfSight(org.bukkit.entity.Entity p0);

    boolean hasMetadata(String p0);

    boolean hasPermission(String p0);

    boolean hasPermission(org.bukkit.permissions.Permission p0);

    boolean hasPlayedBefore();

    boolean hasPotionEffect(org.bukkit.potion.PotionEffectType p0);

    void hidePlayer(Player p0);

    void incrementStatistic(org.bukkit.Statistic p0);

    void incrementStatistic(org.bukkit.Statistic p0, int p1);

    void incrementStatistic(org.bukkit.Statistic p0, org.bukkit.Material p1);

    void incrementStatistic(org.bukkit.Statistic p0, org.bukkit.Material p1, int p2);

    boolean isBanned();

    void setBanned(boolean p0);

    boolean isBlocking();

    boolean isConversing();

    boolean isCustomNameVisible();

    void setCustomNameVisible(boolean p0);

    boolean isDead();

    boolean isEmpty();

    boolean isFlying();

    void setFlying(boolean p0);

    boolean isHealthScaled();

    void setHealthScaled(boolean p0);

    boolean isInsideVehicle();

    boolean isLeashed();

    boolean isOnline();

    boolean isOp();

    void setOp(boolean p0);

    boolean isPermissionSet(String p0);

    boolean isPermissionSet(org.bukkit.permissions.Permission p0);

    boolean isPlayerTimeRelative();

    boolean isSleeping();

    boolean isSleepingIgnored();

    void setSleepingIgnored(boolean p0);

    boolean isSneaking();

    void setSneaking(boolean p0);

    boolean isSprinting();

    void setSprinting(boolean p0);

    boolean isValid();

    boolean isWhitelisted();

    void setWhitelisted(boolean p0);

    void kickPlayer(Tregmine instance, String p0);

    <T extends org.bukkit.entity.Projectile> T launchProjectile(Class<? extends T> p0);

    boolean leaveVehicle();

    void loadData();

    org.bukkit.inventory.InventoryView openEnchanting(org.bukkit.Location p0, boolean p1);

    org.bukkit.inventory.InventoryView openInventory(org.bukkit.inventory.Inventory p0);

    void openInventory(org.bukkit.inventory.InventoryView p0);

    org.bukkit.inventory.InventoryView openWorkbench(org.bukkit.Location p0, boolean p1);

    boolean performCommand(String p0);

    void playEffect(org.bukkit.EntityEffect p0);

    <T extends Object> void playEffect(org.bukkit.Location p0, org.bukkit.Effect p1, T p2);

    void playNote(org.bukkit.Location p0, org.bukkit.Instrument p1, org.bukkit.Note p2);

    void playSound(org.bukkit.Location p0, org.bukkit.Sound p1, float p2, float p3);

    void recalculatePermissions();

    void remove();

    void removeAttachment(org.bukkit.permissions.PermissionAttachment p0);

    void removeMetadata(String p0, org.bukkit.plugin.Plugin p1);

    void removePotionEffect(org.bukkit.potion.PotionEffectType p0);

    void resetMaxHealth();

    void resetPlayerTime();

    void resetPlayerWeather();

    void saveData();

    void sendMap(org.bukkit.map.MapView p0);

    void sendPluginMessage(org.bukkit.plugin.Plugin p0, String p1, byte[] p2);

    void sendRawMessage(String p0);

    void sendMessage(BaseComponent... a);

    void sendMessage(String message);

    java.util.Map<String, Object> serialize();

    void setBedSpawnLocation(org.bukkit.Location p0, boolean p1);

    boolean setLeashHolder(org.bukkit.entity.Entity p0);

    void setMetadata(String p0, org.bukkit.metadata.MetadataValue p1);

    boolean addPassenger(org.bukkit.entity.Entity p0);

    void setPermission(String p0);

    void setPlayerTime(long p0, boolean p1);

    void setResourcePack(String p0);

    boolean setWindowProperty(org.bukkit.inventory.InventoryView.Property p0, int p1);

    void showPlayer(Player p0);

    Player.Spigot spigot();

    boolean teleport(org.bukkit.entity.Entity p0);

    boolean teleport(org.bukkit.entity.Entity p0, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause p1);

    boolean teleport(org.bukkit.Location p0);

    boolean teleport(org.bukkit.Location p0, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause p1);
}
