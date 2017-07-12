package info.tregmine;

import com.maxmind.geoip.LookupService;
import info.tregmine.api.*;
import info.tregmine.api.Timer;
import info.tregmine.api.util.TregmineFileUtil;
import info.tregmine.commands.*;
import info.tregmine.database.*;
import info.tregmine.database.db.DBContextFactory;
import info.tregmine.discord.DiscordDelegate;
import info.tregmine.events.CallEventListener;
import info.tregmine.events.TregmineChatEvent;
import info.tregmine.listeners.*;
import info.tregmine.quadtree.IntersectionException;
import info.tregmine.tools.*;
import info.tregmine.zones.Lot;
import info.tregmine.zones.Zone;
import info.tregmine.zones.ZoneWorld;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.io.IOUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ein Andersson
 * @author Emil Hernvall
 * @author Eric Rabil
 */
public class Tregmine extends JavaPlugin {
    public final static int VERSION = 0;
    public final static int AMOUNT = 0;

    public final static Logger LOGGER = Logger.getLogger("Minecraft");
    private static Boolean coreProtectEnabled = null;
    public final TregmineConsolePlayer consolePlayer = new TregmineConsolePlayer(this);
    public Tregmine plugin;
    public String releaseType = "re";
    public String serverName;
    FileConfiguration config;
    private IContextFactory contextFactory;
    private Server server;
    private WebServer webServer;
    private Map<UUID, GenericPlayer> players;
    private Map<Integer, GenericPlayer> playersById;
    private Map<Location, Integer> blessedBlocks;
    private Map<Location, FishyBlock> fishyBlocks;
    private Map<Material, Integer> minedBlockPrices;
    private Map<String, ZoneWorld> worlds;
    private Map<Integer, Zone> zones;
    private List<String> insults;
    private List<String> quitMessages;
    private List<String> bannedWords;
    private List<TregmineChatEvent> blockedChats;
    private Queue<GenericPlayer> mentors;
    private Queue<GenericPlayer> students;
    private boolean lockdown = false;
    private File configFile;
    private SecureRandom random = new SecureRandom();
    private LookupService cl = null;
    private boolean keywordsEnabled;
    private World vanillaWorld;
    private World vanillaNetherWorld;
    private World vanillaEndWorld;
    private ChatColor[] rankcolors = new ChatColor[9];
    // Special!
    private World world2;
    private World world2nether;
    private World world2end;
    private boolean secondaryworld;
    // Statistics
    private int onlineGuards = 0;
    private int onlineJuniors = 0;
    private int onlineSeniors = 0;
    private int onlineTeachers = 0;
    private DiscordDelegate discord;
    private Lag lag = new Lag();

    public static boolean coreProtectEnabled() {
        if (coreProtectEnabled == null) {
            coreProtectEnabled = Bukkit.getPluginManager().isPluginEnabled("CoreProtect");
        }
        return coreProtectEnabled;
    }

    public void addBlockedChat(TregmineChatEvent e) {
        this.blockedChats.add(e);
    }

    // End interject
    public GenericPlayer addPlayer(Player srcPlayer, InetAddress addr) throws PlayerBannedException {
        // if (players.containsKey(srcPlayer.getName())) {
        // return players.get(srcPlayer.getName());
        // }
        GenericPlayer plr = players.get(srcPlayer.getName());
        if (plr != null) {
            return plr;
        }
        try (IContext ctx = contextFactory.createContext()) {
            IPlayerDAO playerDAO = ctx.getPlayerDAO();

            GenericPlayer player = playerDAO.getPlayer(srcPlayer.getPlayer());

            if (player == null) {
                player = playerDAO.createPlayer(srcPlayer);
            }

            player.removeFlag(GenericPlayer.Flags.SOFTWARNED);
            player.removeFlag(GenericPlayer.Flags.HARDWARNED);

            IPlayerReportDAO reportDAO = ctx.getPlayerReportDAO();
            List<PlayerReport> reports = reportDAO.getReportsBySubject(player);
            for (PlayerReport report : reports) {
                Date validUntil = report.getValidUntil();
                if (validUntil == null) {
                    continue;
                }
                if (validUntil.getTime() < System.currentTimeMillis()) {
                    continue;
                }

                if (report.getAction() == PlayerReport.Action.SOFTWARN) {
                    player.setFlag(GenericPlayer.Flags.SOFTWARNED);
                } else if (report.getAction() == PlayerReport.Action.HARDWARN) {
                    player.setFlag(GenericPlayer.Flags.HARDWARNED);
                } else if (report.getAction() == PlayerReport.Action.BAN) {
                    throw new PlayerBannedException(report.getMessage());
                }
            }

            player.setIp(addr.getHostAddress());
            player.setHost(addr.getCanonicalHostName());

            if (cl != null) {
                com.maxmind.geoip.Location l1 = cl.getLocation(player.getIp());
                if (l1 != null) {
                    Tregmine.LOGGER.info(player.getName() + ": " + l1.countryName + ", " + l1.city + ", "
                            + player.getIp() + ", " + player.getHost());
                    player.setCountry(l1.countryName);
                    player.setCity(l1.city);
                } else {
                    Tregmine.LOGGER.info(player.getName() + ": " + player.getIp() + ", " + player.getHost());
                }
            } else {
                Tregmine.LOGGER.info(player.getName() + ": " + player.getIp() + ", " + player.getHost());
            }

            int onlinePlayerCount = 0;
            Collection<? extends Player> onlinePlayers = getServer().getOnlinePlayers();
            if (onlinePlayers != null) {
                onlinePlayerCount = onlinePlayers.size();
            }

            ILogDAO logDAO = ctx.getLogDAO();
            logDAO.insertLogin(player, false, onlinePlayerCount);

            player.setTemporaryChatName(player.getNameColor() + player.getName());

            players.put(player.getUniqueId(), player);
            playersById.put(player.getId(), player);
            return player;
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcast(BaseComponent a) {
        for (GenericPlayer player : this.getOnlinePlayers()) {
            player.sendMessage(a);
        }
    }

    public void broadcast(BaseComponent... a) {
        for (GenericPlayer player : this.getOnlinePlayers()) {
            player.sendMessage(a);
        }
    }

    public HoverEvent buildHover(String abc) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(abc).create());
    }

    public IContext createContext() throws DAOException {
        return contextFactory.createContext();
    }

    public boolean discordEnabled() {
        return this.discord != null;
    }

    public List<String> getBannedWords() {
        return bannedWords;
    }

    public Map<Location, Integer> getBlessedBlocks() {
        return blessedBlocks;
    }

    public List<TregmineChatEvent> getBlockedChats() {
        return this.blockedChats;
    }

    public IContextFactory getContextFactory() {
        return contextFactory;
    }

    public DiscordDelegate getDiscordDelegate() {
        return this.discord;
    }

    public Map<Location, FishyBlock> getFishyBlocks() {
        return fishyBlocks;
    }

    public List<String> getInsults() {
        return insults;
    }

    public Lag getLag() {
        return this.lag;
    }

    public boolean getLockdown() {
        return lockdown;
    }

    public void setLockdown(boolean v) {
        if (v) {
            Bukkit.broadcastMessage(
                    ChatColor.RED + "The server is now on lockdown. Only staff will be able to connect.");
        } else {
            Bukkit.broadcastMessage(ChatColor.GREEN + "The server is no longer on lockdown.");
        }
        this.lockdown = v;
    }

    public Queue<GenericPlayer> getMentorQueue() {
        return mentors;
    }

    public int getMinedPrice(Material q) {
        return this.minedBlockPrices.get(q);
    }

    public int getOnlineGuardians() {
        return this.onlineGuards;
    }

    public int getOnlineJuniors() {
        return this.onlineJuniors;
    }

    public List<GenericPlayer> getOnlinePlayers() {
        List<GenericPlayer> players = new ArrayList<>();
        for (Player player : server.getOnlinePlayers()) {
            players.add(getPlayer(player));
        }

        return players;
    }

    public int getOnlineSeniors() {
        return this.onlineSeniors;
    }

    // ============================================================================
    // Data structure accessors
    // ============================================================================

    public int getOnlineTeachers() {
        return this.onlineTeachers;
    }

    public GenericPlayer getPlayer(int id) {
        return playersById.get(id);
    }

    public GenericPlayer getPlayer(Player player) {
        try {
            return players.get(player.getUniqueId());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public GenericPlayer getPlayer(String name) {
        try {
            return players.get(name);
        } catch (Exception e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            return null;
        }
    }

    public GenericPlayer getPlayerOffline(int id) {
        GenericPlayer plr = playersById.get(id);
        if (plr != null) {
            return plr;
        }

        try (IContext ctx = contextFactory.createContext()) {
            IPlayerDAO playerDAO = ctx.getPlayerDAO();
            return playerDAO.getPlayer(id);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    public GenericPlayer getPlayerOffline(OfflinePlayer player) {
        GenericPlayer plr = players.get(player.getUniqueId());
        if (plr != null) {
            return plr;
        }

        try (IContext ctx = contextFactory.createContext()) {
            IPlayerDAO playerDAO = ctx.getPlayerDAO();
            return playerDAO.getPlayer(player.getUniqueId());
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    public GenericPlayer getPlayerOffline(String username) {
        GenericPlayer plr = players.get(username);
        if (plr != null) {
            return plr;
        }

        try (IContext ctx = contextFactory.createContext()) {
            IPlayerDAO playerDAO = ctx.getPlayerDAO();
            return playerDAO.getPlayer(username);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    public GenericPlayer getPlayerOffline(UUID uuid) {
        // if (players.containsKey(name)) {
        // return players.get(name);
        // }
        GenericPlayer plr = players.get(uuid);
        if (plr != null) {
            return plr;
        }

        try (IContext ctx = contextFactory.createContext()) {
            IPlayerDAO playerDAO = ctx.getPlayerDAO();
            return playerDAO.getPlayer(uuid);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPluginFolder() {
        return this.getDataFolder().getAbsolutePath();
    }

    public List<String> getQuitMessages() {
        return quitMessages;
    }

    public ChatColor getRankColor(Rank rank) {
        if (this.rankcolors[0] == null) {
            Tools tools = new Tools();
            this.rankcolors[0] = tools.toColor(this.getConfig().getString("ranks.colors.tourist"));
            this.rankcolors[1] = tools.toColor(this.getConfig().getString("ranks.colors.settler"));
            this.rankcolors[2] = tools.toColor(this.getConfig().getString("ranks.colors.resident"));
            this.rankcolors[3] = tools.toColor(this.getConfig().getString("ranks.colors.donator"));
            this.rankcolors[4] = tools.toColor(this.getConfig().getString("ranks.colors.guardian"));
            this.rankcolors[5] = tools.toColor(this.getConfig().getString("ranks.colors.coder"));
            this.rankcolors[6] = tools.toColor(this.getConfig().getString("ranks.colors.builder"));
            this.rankcolors[7] = tools.toColor(this.getConfig().getString("ranks.colors.junior"));
            this.rankcolors[8] = tools.toColor(this.getConfig().getString("ranks.colors.senior"));
        }
        if (rank == Rank.TOURIST) {
            return this.rankcolors[0];
        } else if (rank == Rank.SETTLER) {
            return this.rankcolors[1];
        } else if (rank == Rank.RESIDENT) {
            return this.rankcolors[2];
        } else if (rank == Rank.DONATOR) {
            return this.rankcolors[3];
        } else if (rank == Rank.GUARDIAN) {
            return this.rankcolors[4];
        } else if (rank == Rank.CODER) {
            return this.rankcolors[5];
        } else if (rank == Rank.BUILDER) {
            return this.rankcolors[6];
        } else if (rank == Rank.JUNIOR_ADMIN) {
            return this.rankcolors[7];
        } else if (rank == Rank.SENIOR_ADMIN) {
            return this.rankcolors[8];
        } else {
            return ChatColor.WHITE;
        }
    }

    public SecureRandom getSecureRandom() {
        return this.random;
    }

    // ============================================================================
    // Player methods
    // ============================================================================

    public Queue<GenericPlayer> getStudentQueue() {
        return students;
    }

    public World getSWorld() {
        return this.world2;
    }

    public World getSWorldEnd() {
        return this.world2end;
    }

    // Interjection point for other stuff

    public World getSWorldNether() {
        return this.world2nether;
    }

    public Tregmine getTregmine() {
        return plugin;
    }

    public World getVanillaEnd() {
        return vanillaEndWorld;
    }

    public World getVanillaNether() {
        return vanillaNetherWorld;
    }

    public World getVanillaWorld() {
        return vanillaWorld;
    }

    public WebServer getWebServer() {
        return webServer;
    }

    public ZoneWorld getWorld(World world) {
        ZoneWorld zoneWorld = worlds.get(world.getName());

        // lazy load zone worlds as required
        if (zoneWorld == null) {
            try (IContext ctx = contextFactory.createContext()) {
                IZonesDAO dao = ctx.getZonesDAO();

                zoneWorld = new ZoneWorld(world);
                List<Zone> zones = dao.getZones(world.getName());
                for (Zone zone : zones) {
                    try {
                        zoneWorld.addZone(zone);
                        this.zones.put(zone.getId(), zone);
                    } catch (IntersectionException e) {
                        LOGGER.warning("Failed to load zone " + zone.getName() + " with id " + zone.getId() + ".");
                    }
                }

                List<Lot> lots = dao.getLots(world.getName());
                for (Lot lot : lots) {
                    try {
                        zoneWorld.addLot(lot);
                    } catch (IntersectionException e) {
                        LOGGER.warning("Failed to load lot " + lot.getName() + " with id " + lot.getId() + ".");
                    }
                }

                worlds.put(world.getName(), zoneWorld);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
        }

        return zoneWorld;
    }

    public Zone getZone(int zoneId) {
        return zones.get(zoneId);
    }

    public boolean hasSecondaryWorld() {
        return this.secondaryworld;
    }

    public boolean isInVanilla(GenericPlayer player) {
        return player.getWorld() == this.vanillaWorld || player.getWorld() == this.vanillaEndWorld
                || player.getWorld() == this.vanillaNetherWorld;
    }

    public boolean keywordsEnabled() {
        return keywordsEnabled;
    }

    public int kickAll(String message) {
        Player[] plrs = this.getServer().getOnlinePlayers()
                .toArray(new Player[this.getServer().getOnlinePlayers().size()]);
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : plrs) {
                    player.kickPlayer(message);
                }
            }
        });
        return 0;
    }

    public List<GenericPlayer> matchPlayer(String pattern) {
        if (pattern.toLowerCase() == "console") {
            List<GenericPlayer> list = new ArrayList<>();
            list.add(this.consolePlayer);
            return list;
        }
        List<Player> matches = server.matchPlayer(pattern);
        if (matches.size() == 0) {
            return new ArrayList<>();
        }

        List<GenericPlayer> decoratedMatches = new ArrayList<>();
        for (Player match : matches) {
            GenericPlayer decoratedMatch = getPlayer(match);
            if (decoratedMatch == null) {
                continue;
            }
            decoratedMatches.add(decoratedMatch);
        }

        return decoratedMatches;
    }

    // run when plugin is disabled
    @Override
    public void onDisable() {
        server.getScheduler().cancelTasks(this);
        if (this.discord != null) {
            this.discord.sendShutdownSignal();
            this.discord.getClient().shutdown(false);
        }
        // Add a record of logout to db for all players

        try {
            for (GenericPlayer player : getOnlinePlayers()) {
                player.sendMessage(new TextComponent(ChatColor.GOLD + this.serverName + ChatColor.DARK_AQUA
                        + " may be shutting down soon! Please prepare to be kicked."));
                player.saveInventory(player.getCurrentInventory());
                removePlayer(player);
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (webServer == null)
            return;
        try {
            webServer.stop();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to stop web server!", e);
        }
    }

    private void setupInstructions() {
        LOGGER.info("[tregmine] It appears you have not set up the tregmine plugin!");
        LOGGER.info("[tregmine] Please import the generated tregmine.sql file to the desired database.");
        LOGGER.info("[tregmine] Please configure the generated config.yml, then set enabled to true on the top of the file.");
        LOGGER.info("[tregmine] Restart the server once you have done this; Hopefully tregmine will start!");
    }

    @Override
    public void onEnable() {

        this.server = getServer();
        plugin = this;

        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        File schemaFile = new File(plugin.getDataFolder(), "tregmine.sql");

        if (!schemaFile.exists()) {
            try {
                schemaFile.createNewFile();
                FileOutputStream os = new FileOutputStream(schemaFile);
                IOUtils.copy(this.getClassLoader().getResourceAsStream("tregmine.sql"), os);
            } catch (IOException e) {
                LOGGER.info("[tregmine] Failed to generate database file, see GitHub for instructions.");
                e.printStackTrace();
            }
        }

        if (!this.configFile.exists()) {
            this.saveDefaultConfig();
            this.setupInstructions();
            this.server.getPluginManager().disablePlugin(this);
            return;
        }

        if (!this.getConfig().getBoolean("enabled")) {
            this.setupInstructions();
            this.server.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, this.lag, 100L, 1L);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Timer(this), 100L, 1L);
        if (getConfig().getBoolean("worlds.enabled")) {
            List<?> configWorlds = getConfig().getList("worlds.names");
            if (getConfig().getString("worlds.vanillaworld") == "true") {
                WorldCreator addWorld = new WorldCreator("vanilla");
                addWorld.environment(World.Environment.NORMAL);
                addWorld.generateStructures(true);
                addWorld.type(WorldType.NORMAL);
                this.vanillaWorld = addWorld.createWorld();

                // Nether
                WorldCreator addNether = new WorldCreator("vanilla_nether").environment(World.Environment.NETHER)
                        .type(WorldType.NORMAL);
                this.vanillaNetherWorld = addNether.createWorld();

                // End
                WorldCreator addEnd = new WorldCreator("vanilla_the_end").environment(World.Environment.THE_END)
                        .type(WorldType.NORMAL);
                this.vanillaEndWorld = addEnd.createWorld();
            }
            if (config.getBoolean("worlds.special.newworld")) {
                WorldCreator world2g = new WorldCreator("world_2");
                WorldCreator world2netherg = new WorldCreator("world_2_nether");
                WorldCreator world2endg = new WorldCreator("world_2_the_end");
                world2g.environment(World.Environment.NORMAL);
                world2g.generateStructures(true);
                world2g.type(WorldType.NORMAL);
                this.world2 = world2g.createWorld();
                world2netherg.environment(World.Environment.NETHER);
                world2netherg.generateStructures(true);
                world2netherg.type(WorldType.NORMAL);
                this.world2nether = world2netherg.createWorld();
                world2endg.environment(World.Environment.THE_END);
                world2endg.generateStructures(true);
                world2endg.type(WorldType.NORMAL);
                this.world2end = world2endg.createWorld();
                this.secondaryworld = true;
            }
            this.serverName = getConfig().getString("general.servername");
            this.keywordsEnabled = getConfig().getBoolean("general.keywords");
            if (getConfig().getString("worlds.enabled") == "true") {
                String[] worlds = configWorlds.toArray(new String[configWorlds.size()]);
                for (String worldName : worlds) {
                    if (worldName.contains("the_end")) {
                        WorldCreator addWorld = new WorldCreator(worldName);
                        addWorld.environment(World.Environment.THE_END);
                        addWorld.generateStructures(false);
                        addWorld.createWorld();
                        continue;
                    }
                    if (worldName.contains("nether")) {
                        WorldCreator addWorld = new WorldCreator(worldName);
                        addWorld.environment(World.Environment.NETHER);
                        addWorld.generateStructures(false);
                        addWorld.createWorld();
                        continue;
                    }
                    WorldCreator addWorld = new WorldCreator(worldName);
                    addWorld.environment(World.Environment.NORMAL);
                    addWorld.generateStructures(false);
                    addWorld.type(WorldType.NORMAL);
                    addWorld.createWorld();
                }

                LOGGER.info("" + configWorlds.size() + " extra worlds attempted to load.");
            }
        }

        DatabaseToolCommand dbTool = new DatabaseToolCommand(this);

        // Loads blessed blocks, fishy blocks, insults, quit messages, banned words
        dbTool.loadBFS();

        // Set up web server
        webServer = new WebServer(this);
        webServer.start();

        // Register all listeners
        PluginManager pluginMgm = server.getPluginManager();
        pluginMgm.registerEvents(new CraftListener(), this);
        pluginMgm.registerEvents(new AfkListener(this), this);
        pluginMgm.registerEvents(new BlockStats(this), this);
        pluginMgm.registerEvents(new BlessedBlockListener(this), this);
        pluginMgm.registerEvents(new BoxFillBlockListener(this), this);
        pluginMgm.registerEvents(new ChatListener(this), this);
        pluginMgm.registerEvents(new CompassListener(this), this);
        pluginMgm.registerEvents(new PlayerLookupListener(this), this);
        pluginMgm.registerEvents(new SetupListener(this), this);
        pluginMgm.registerEvents(new SignColorListener(), this);
        pluginMgm.registerEvents(new TabListener(this), this);
        pluginMgm.registerEvents(new TauntListener(this), this);
        pluginMgm.registerEvents(new TregmineBlockListener(this), this);
        pluginMgm.registerEvents(new TregminePlayerListener(this), this);
        pluginMgm.registerEvents(new ZoneBlockListener(this), this);
        pluginMgm.registerEvents(new ZoneEntityListener(this), this);
        pluginMgm.registerEvents(new ZonePlayerListener(this), this);
        pluginMgm.registerEvents(new FishyBlockListener(this), this);
        pluginMgm.registerEvents(new InventoryListener(this), this);
        pluginMgm.registerEvents(new DonationSigns(this), this);
        pluginMgm.registerEvents(new ExpListener(this), this);
        pluginMgm.registerEvents(new ItemFrameListener(this), this);
        pluginMgm.registerEvents(new EggListener(this), this);
        pluginMgm.registerEvents(new PistonListener(this), this);
        pluginMgm.registerEvents(new ToolCraft(this), this);
        pluginMgm.registerEvents(new LumberListener(this), this);
        pluginMgm.registerEvents(new VeinListener(this), this);
        pluginMgm.registerEvents(new CallEventListener(this), this);
        pluginMgm.registerEvents(new PortalListener(this), this);
        pluginMgm.registerEvents(new BankListener(this), this);
        pluginMgm.registerEvents(new RareDropListener(this), this);
        pluginMgm.registerEvents(new DamageListener(this), this);
        pluginMgm.registerEvents(new ChunkListener(this), this);

        // Declaration of all commands

        getCommand("admins").setExecutor(new NotifyCommand(this, "admins", Rank.JUNIOR_ADMIN, Rank.SENIOR_ADMIN) {


            @Override
            public ChatColor getColor() {
                return plugin.getRankColor(Rank.JUNIOR_ADMIN);
            }

            @Override
            public boolean isTarget(GenericPlayer player) {
                return player.getRank() == Rank.JUNIOR_ADMIN || player.getRank() == Rank.SENIOR_ADMIN;
            }


        });

        getCommand("guardians").setExecutor(new NotifyCommand(this, "guardians", Rank.GUARDIAN, Rank.JUNIOR_ADMIN, Rank.SENIOR_ADMIN) {
            @Override
            public ChatColor getColor() {
                return plugin.getRankColor(Rank.GUARDIAN);
            }

            @Override
            public boolean isTarget(GenericPlayer player) {
                return player.getRank() == Rank.GUARDIAN || player.getRank() == Rank.JUNIOR_ADMIN
                        || player.getRank() == Rank.SENIOR_ADMIN;
            }
        });

        getCommand("coders").setExecutor(new NotifyCommand(this, "coders", Rank.CODER, Rank.JUNIOR_ADMIN, Rank.SENIOR_ADMIN) {
            @Override
            public ChatColor getColor() {
                return plugin.getRankColor(Rank.CODER);
            }

            @Override
            public boolean isTarget(GenericPlayer player) {
                return player.getRank() == Rank.CODER || player.getRank() == Rank.JUNIOR_ADMIN
                        || player.getRank() == Rank.SENIOR_ADMIN;
            }
        });

        for (GameMode gm : GameMode.values()) {
            getCommand(gm.name().toLowerCase()).setExecutor(new GameModeCommand(this, gm.name().toLowerCase(), gm));
        }


        getCommand("internal").setExecutor(new InternalCommand(this));
        getCommand("taxi").setExecutor(new OWCommand(this));
        getCommand("property").setExecutor(new PropertyCommand(this));
        getCommand("staffbook").setExecutor(new StaffHandbookCommand(this));
        getCommand("action").setExecutor(new ActionCommand(this));
        getCommand("afk").setExecutor(new AfkCommand(this));
        getCommand("alert").setExecutor(new AlertCommand(this));
        getCommand("allclear").setExecutor(new CheckBlocksCommand(this));
        getCommand("back").setExecutor(new BackCommand(this));
        getCommand("badge").setExecutor(new BadgeCommand(this));
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("bless").setExecutor(new BlessCommand(this));
        getCommand("blockhere").setExecutor(new BlockHereCommand(this));
        getCommand("brush").setExecutor(new BrushCommand(this));
        getCommand("buytool").setExecutor(new BuyToolCommand(this));
        getCommand("channel").setExecutor(new ChannelCommand(this));
        getCommand("channelview").setExecutor(new ChannelViewCommand(this));
        getCommand("clean").setExecutor(new CleanInventoryCommand(this));
        getCommand("cname").setExecutor(new ChangeNameCommand(this));
        getCommand("createmob").setExecutor(new CreateMobCommand(this));
        getCommand("createwarp").setExecutor(new CreateWarpCommand(this));
        getCommand("gamemode").setExecutor(new GameModeCommand(this, "gamemode", null));
        getCommand("fill").setExecutor(new FillCommand(this, "fill"));
        getCommand("database").setExecutor(dbTool);
        getCommand("suicide").setExecutor(new SuicideCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("force").setExecutor(new ForceCommand(this));
        getCommand("forceblock").setExecutor(new ForceShieldCommand(this));
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("give").setExecutor(new GiveCommand(this));
        getCommand("head").setExecutor(new HeadCommand(this));
        getCommand("hide").setExecutor(new HideCommand(this));
        getCommand("mail").setExecutor(new MailCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("ignore").setExecutor(new IgnoreCommand(this));
        getCommand("inv").setExecutor(new InventoryCommand(this));
        getCommand("invlog").setExecutor(new InventoryLogCommand(this));
        getCommand("item").setExecutor(new ItemCommand(this));
        getCommand("keyword").setExecutor(new KeywordCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("kill").setExecutor(new KillCommand(this));
        getCommand("lockdown").setExecutor(new LockdownCommand(this));
        getCommand("lot").setExecutor(new LotCommand(this));
        getCommand("lottery").setExecutor(new LotteryCommand(this));
        getCommand("mentor").setExecutor(new MentorCommand(this));
        getCommand("msg").setExecutor(new MsgCommand(this));
        getCommand("newspawn").setExecutor(new NewSpawnCommand(this));
        getCommand("nuke").setExecutor(new NukeCommand(this));
        getCommand("password").setExecutor(new PasswordCommand(this));
        getCommand("pos").setExecutor(new PositionCommand(this));
        getCommand("promote").setExecutor(new PromoteCommand(this));
        getCommand("quitmessage").setExecutor(new QuitMessageCommand(this));
        getCommand("regeneratechunk").setExecutor(new RegenerateChunkCommand(this));
        getCommand("remitems").setExecutor(new RemItemsCommand(this));
        getCommand("repair").setExecutor(new ToolRepairCommand(this));
        getCommand("reply").setExecutor(new ReplyCommand(this));
        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("rname").setExecutor(new ResetNameCommand(this));
        getCommand("resetlore").setExecutor(new ResetLoreCommand(this));
        getCommand("say").setExecutor(new SayCommand(this));
        getCommand("seen").setExecutor(new SeenCommand(this));
        getCommand("sell").setExecutor(new SellCommand(this));
        getCommand("sendback").setExecutor(new SendBackCommand(this));
        getCommand("sendto").setExecutor(new SendToCommand(this));
        getCommand("setbiome").setExecutor(new SetBiomeCommand(this));
        getCommand("setspawner").setExecutor(new SetSpawnerCommand(this));
        getCommand("skipmentor").setExecutor(new SkipMentorCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("summon").setExecutor(new SummonCommand(this));
        getCommand("support").setExecutor(new SupportCommand(this));
        getCommand("staffnews").setExecutor(new StaffNewsCommand(this));
        getCommand("testfill").setExecutor(new FillCommand(this, "testfill"));
        getCommand("time").setExecutor(new TimeCommand(this));
        getCommand("tool").setExecutor(new ToolSpawnCommand(this));
        getCommand("town").setExecutor(new ZoneCommand(this, "town"));
        getCommand("tp").setExecutor(new TeleportCommand(this));
        getCommand("ttps").setExecutor(new TpsCommand(this));
        getCommand("tpshield").setExecutor(new TeleportShieldCommand(this));
        getCommand("tpto").setExecutor(new TeleportToCommand(this));
        getCommand("trade").setExecutor(new TradeCommand(this));
        getCommand("update").setExecutor(new UpdateCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("wallet").setExecutor(new WalletCommand(this));
        getCommand("watchchunks").setExecutor(new WatchChunksCommand(this));
        getCommand("warn").setExecutor(new WarnCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("weather").setExecutor(new WeatherCommand(this));
        getCommand("webkick").setExecutor(new WebKickCommand(this));
        getCommand("who").setExecutor(new WhoCommand(this));
        getCommand("vanilla").setExecutor(new VanillaCommand(this));
        getCommand("zone").setExecutor(new ZoneCommand(this, "zone"));
        getCommand("chunkcount").setExecutor(new ChunkCountCommand(this));
        getCommand("rcode").setExecutor(new ReferralCodeCommand(this, "rcode"));
        getCommand("referralcode").setExecutor(new ReferralCodeCommand(this, "referralcode"));
        getCommand("version").setExecutor(new VersionCommand(this));
        ToolCraftRegistry.RegisterRecipes(getServer()); // Registers all tool
        // recipes

        try {
            for (GenericPlayer player : getOnlinePlayers()) {
                player.sendMessage(ChatColor.AQUA + "Tregmine has been reloaded!");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    for (GenericPlayer player : getOnlinePlayers()) {
                        if (player.getCombatLog() > 0) {
                            player.setCombatLog(player.getCombatLog() - 1);

                            if (player.getCombatLog() == 0) {
                                player.sendMessage(new TextComponent(
                                        ChatColor.GREEN + "Combat log has warn off... Safe to log off!"));
                            }
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, 20L, 20L);
        if (this.getConfig().getBoolean("discord-bot.enabled")) {
            try {
                this.discord = new DiscordDelegate(this);
            } catch (InterruptedException | RateLimitedException | LoginException e) {
                e.printStackTrace();
                this.discord = null;
            }
        }

    }

    @Override
    public void onLoad() {
        File folder = getDataFolder();
        Tregmine.LOGGER.info("Data folder is: " + folder);

        reloadConfig();

        config = getConfig();

        contextFactory = new DBContextFactory(config, this);

        // Set up all data structures
        players = new HashMap<>();
        playersById = new HashMap<>();

        mentors = new LinkedList<>();
        students = new LinkedList<>();

        worlds = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareToIgnoreCase(b);
            }
        });

        zones = new HashMap<>();

        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        for (Player player01 : players) {
            try {
                GenericPlayer tp = addPlayer(player01, player01.getAddress().getAddress());
                if (tp.getRank() == Rank.TOURIST) {
                    students.offer(tp);
                }
            } catch (PlayerBannedException e) {
                player01.kickPlayer(e.getMessage());
            }
        }

        try {
            cl = new LookupService(TregmineFileUtil.fileFromInputStream(this.getClassLoader().getResourceAsStream("GeoIPCity.dat")), LookupService.GEOIP_MEMORY_CACHE);
        } catch (IOException e) {
            Tregmine.LOGGER.warning("GeoIPCity.dat was not found! ");
        }
    }

    public FileConfiguration plConfig() {
        return this.config;
    }

    // ============================================================================
    // Zone methods
    // ============================================================================

    public void reloadPlayer(GenericPlayer player) {
        try {
            addPlayer(player.getDelegate(), player.getAddress().getAddress());
        } catch (PlayerBannedException e) {
            player.kickPlayer(plugin, e.getMessage());
        }
    }

    public void removePlayer(GenericPlayer player) {
        try (IContext ctx = contextFactory.createContext()) {
            int onlinePlayerCount = 0;
            Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
            if (onlinePlayers != null) {
                onlinePlayerCount = onlinePlayers.size();
            }

            ILogDAO logDAO = ctx.getLogDAO();
            logDAO.insertLogin(player, true, onlinePlayerCount);

            IPlayerDAO playerDAO = ctx.getPlayerDAO();
            playerDAO.updatePlayTime(player);
            playerDAO.updateBadges(player);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        player.setValid(false);

        players.remove(player.getName());
        playersById.remove(player.getId());
        mentors.remove(player);
        students.remove(player);
    }

    public String serverName() {
        return this.serverName;
    }

    public void setBlessedBlocks(Map<Location, Integer> blocks) {
        this.blessedBlocks = blocks;
    }

    public void setFishyBlocks(Map<Location, FishyBlock> blocks) {
        this.fishyBlocks = blocks;
    }

    public void setMinedBlockPrices(Map<Material, Integer> blocks) {
        this.minedBlockPrices = blocks;
    }

    public void setInsults(List<String> insults) {
        this.insults = insults;
    }

    public void setQuitMessages(List<String> quitMessages) {
        this.quitMessages = quitMessages;
    }

    public void setBannedWords(List<String> bannedWords) {
        this.bannedWords = bannedWords;
    }

    public void setReconnecting(boolean v) {
        if (v) {
            Bukkit.broadcastMessage(ChatColor.RED
                    + "The server lost connection to the database. Nobody will be able to join until a connection is restored.");
        } else {
            Bukkit.broadcastMessage(ChatColor.GREEN + "Connection has been restored! Players can now re-connect.");
        }
        this.lockdown = v;
    }

    public void updateStatistics() {
        int g = 0;
        int j = 0;
        int s = 0;
        int t = 0;
        for (GenericPlayer yeezy : this.getOnlinePlayers()) {
            if (yeezy.getRank() == Rank.GUARDIAN)
                g++;
            else if (yeezy.getRank() == Rank.JUNIOR_ADMIN)
                j++;
            else if (yeezy.getRank() == Rank.SENIOR_ADMIN)
                s++;

            if (yeezy.getRank().canMentor())
                t++;
        }
        this.onlineGuards = g;
        this.onlineJuniors = j;
        this.onlineSeniors = s;
        this.onlineTeachers = t;
    }
}
