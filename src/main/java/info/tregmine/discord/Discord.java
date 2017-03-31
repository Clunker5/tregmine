/*
 * Based off of the DiscordSRV plugin. https://github.com/Scarsz/DiscordSRV - Necessary attributions are in the LICENSE file
 */

package info.tregmine.discord;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.GenericPlayer.Flags;
import info.tregmine.api.Rank;
import info.tregmine.discord.commands.CommandHandler;
import info.tregmine.discord.entities.EmbedAlertType;
import info.tregmine.discord.entities.TregmineEmbedBuilder;
import info.tregmine.discord.exception.JDAFailedException;
import info.tregmine.discord.listeners.*;
import info.tregmine.discord.threads.ChannelTopicUpdater;
import info.tregmine.discord.threads.ServerLogWatcher;
import info.tregmine.discord.threads.ServerLogWatcherHelper;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Discord {
    public static User selfUser;
    private JDA api;
    private Long startTime = System.nanoTime();
    private ServerLogWatcher serverLogWatcher;
    private ServerLogWatcherHelper serverLogWatcherHelper;
    private ChannelTopicUpdater channelTopicUpdater;
    private List<String> unsubscribedPlayers = new ArrayList<>();
    private Tregmine plugin;
    private Map<Integer, User> confCodes = new HashMap<Integer, User>();
    private Guild guild;
    private TextChannel chatChannel;
    private TextChannel consoleChannel;
    private Member selfMember;
    private CommandHandler commandHandler;

    public static final DiscordUtil DISCORD_UTIL = new DiscordUtil();

    public Discord(Tregmine tregmine) throws JDAFailedException {
        this.plugin = tregmine;
        // login to discord
        this.buildJda();
        if (api == null) {
            throw new JDAFailedException("Could not connect to the Discord API.");
        }

        Discord.selfUser = this.api.getSelfUser();

        // print the servers that the bot can see
        Tregmine.LOGGER.info("Discord: The following servers are visible: " + api.getGuilds().size());

        // check & get location info
        guild = api.getGuildById(this.plugin.getConfig().getString("discord.guild-info.guild-id"));
        chatChannel = api.getTextChannelById(this.plugin.getConfig().getString("discord.guild-info.channels.chat-id"));
        consoleChannel = api
                .getTextChannelById(this.plugin.getConfig().getString("discord.guild-info.channels.console-id"));
        if (guild == null) {
            throw new JDAFailedException("The Guild ID specified does not exist.");
        }
        if (chatChannel == null)
            Tregmine.LOGGER.warning("Specified chat channel ID from config could not be found");
        if (consoleChannel == null)
            Tregmine.LOGGER.warning("Specified console channel ID from config could not be found");
        if (chatChannel == null && consoleChannel == null) {
            throw new JDAFailedException("Neither channels from config exist.");
        }

        selfMember = new MemberImpl(guild, api.getUserById(api.getSelfUser().getId()));

        // send startup message if enabled
        if (plugin.getConfig().getBoolean("discord.bridge-functionality.start-stop.startup.enabled")) {
            this.chatChannel.sendMessage(TregmineEmbedBuilder.genericEmbed(EmbedAlertType.STATUS_UPDATE.getDisplayName(), plugin.getConfig().getString("discord.bridge-functionality.start-stop.startup.message"), Color.GREEN)).complete();
        }

        // in-game chat events
        this.plugin.getServer().getPluginManager().registerEvents(new ChatListener(this), this.plugin);
        this.plugin.getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this.plugin);

        this.commandHandler = new CommandHandler(this);

        // console streaming thread & helper
        startServerLogWatcher();
        serverLogWatcherHelper = new ServerLogWatcherHelper(this);
        serverLogWatcherHelper.start();

        // channel topic updating thread
        if (channelTopicUpdater == null) {
            channelTopicUpdater = new ChannelTopicUpdater(this);
            channelTopicUpdater.start();
        }

        // player join/leave message events
        if (this.plugin.getConfig().getBoolean("discord.bridge-functionality.join-leave.enabled"))
            this.plugin.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this), this.plugin);

        // player achievement events
        if (this.plugin.getConfig().getBoolean("discord.bridge-functionality.achievements.enabled"))
            this.plugin.getServer().getPluginManager().registerEvents(new AchievementListener(this), this.plugin);

        // - chat channel
        if (this.plugin.getConfig().getBoolean("discord.bridge-functionality.discord-to-minecraft")
                || this.plugin.getConfig().getBoolean("discord.bridge-functionality.minecraft-to-discord")) {
            if (!testChannel(chatChannel))
                Tregmine.LOGGER.warning("Channel \"" + chatChannel + "\" was not accessible");
            if (testChannel(chatChannel)
                    && !PermissionUtil.checkPermission(chatChannel, selfMember, Permission.MESSAGE_WRITE))
                Tregmine.LOGGER.warning("The bot does not have access to send messages in " + chatChannel.getName());
            if (testChannel(chatChannel)
                    && !PermissionUtil.checkPermission(chatChannel, selfMember, Permission.MESSAGE_READ))
                // !PermissionUtil.checkPermission(channel, self. null,
                // Permission.MESSAGE_WRITE)
                // !PermissionUtil.checkPermission(
                Tregmine.LOGGER.warning("The bot does not have access to read messages in " + chatChannel.getName());
        }
        // - console channel
        if (consoleChannel != null) {
            if (!testChannel(consoleChannel))
                Tregmine.LOGGER.warning("Channel \"" + consoleChannel + "\" was not accessible");
            if (testChannel(consoleChannel)
                    && !PermissionUtil.checkPermission(consoleChannel, selfMember, Permission.MESSAGE_WRITE))
                Tregmine.LOGGER.warning("The bot does not have access to send messages in " + consoleChannel.getName());
            if (testChannel(consoleChannel)
                    && !PermissionUtil.checkPermission(consoleChannel, selfMember, Permission.MESSAGE_READ))
                Tregmine.LOGGER.warning("The bot does not have access to read messages in " + consoleChannel.getName());
        }
    }

    public void broadcastMessageToMinecraftServer(String message) {
        for (Player player : Bukkit.getOnlinePlayers())
            if (getSubscribed(player.getUniqueId()))
                player.sendMessage(message);
    }

    private void buildJda() throws JDAFailedException {
        // shutdown if already started
        if (this.api != null)
            try {
                this.api.shutdown(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

        try {
            this.api = new JDABuilder(AccountType.BOT).setToken(this.plugin.getConfig().getString("discord.bot-token"))
                    .addListener(new DiscordListener(this)).setAutoReconnect(true).setAudioEnabled(false)
                    .buildBlocking();
        } catch (LoginException | RateLimitedException | IllegalArgumentException | InterruptedException e) {
            throw new JDAFailedException(e.getMessage() + System.lineSeparator() + System.lineSeparator());
        }

        // game status
        if (!this.plugin.getConfig().getString("discord.bot-appearance.game-status").isEmpty())

            api.getPresence().setGame(new TregmineGame(
                    this.plugin.getConfig().getString("discord.bot-appearance.game-status"), GameType.DEFAULT));
    }

    public boolean checkPlayerIsVanished(Player player) {
        Boolean isVanished = false;
        GenericPlayer check = plugin.getPlayer(player);
        if (check.hasFlag(Flags.INVISIBLE))
            isVanished = true;
        if (this.plugin.getConfig().getBoolean("discord.debug.events.player-vanish-lookup-reporting"))
            this.plugin.getLogger().info("Looking up vanish status for " + player + ": " + isVanished);
        return isVanished;
    }

    public String convertMentionsFromNames(String message) {
        if (!message.contains("@"))
            return message;
        List<String> splitMessage = Arrays.asList(message.split("@| "));
        for (Member member : chatChannel.getMembers()) {
            User user = member.getUser();
            for (String segment : splitMessage)
                if (user.getName().equals(segment))
                    splitMessage.set(splitMessage.indexOf(segment), user.getAsMention());
        }

        String newMessage = String.join(" ", splitMessage);

        return newMessage;
    }

    public String convertRoleToMinecraftColor(Role role) {
        if (role == null) {
            return "";
        }
        String before = String.format("%06x", role.getColor().getRGB() & 0x00FFFFFF).toUpperCase();

        if (before.equalsIgnoreCase("99AAB5"))
            return "&f";
        if (before.equalsIgnoreCase("1ABC9C"))
            return "&a";
        if (before.equalsIgnoreCase("2ECC71"))
            return "&a";
        if (before.equalsIgnoreCase("3498DB"))
            return "&9";
        if (before.equalsIgnoreCase("9B59B6"))
            return "&5";
        if (before.equalsIgnoreCase("E91E63"))
            return "&d";
        if (before.equalsIgnoreCase("F1C40F"))
            return "&e";
        if (before.equalsIgnoreCase("E67E22"))
            return "&6";
        if (before.equalsIgnoreCase("E74C3C"))
            return "&c";
        if (before.equalsIgnoreCase("95A5A6"))
            return "&7";
        if (before.equalsIgnoreCase("607D8B"))
            return "&8";
        if (before.equalsIgnoreCase("11806A"))
            return "&2";
        if (before.equalsIgnoreCase("1F8B4C"))
            return "&2";
        if (before.equalsIgnoreCase("206694"))
            return "&9";
        if (before.equalsIgnoreCase("71368A"))
            return "&5";
        if (before.equalsIgnoreCase("AD1457"))
            return "&d";
        if (before.equalsIgnoreCase("C27C0E"))
            return "&6";
        if (before.equalsIgnoreCase("A84300"))
            return "&6";
        if (before.equalsIgnoreCase("992D22"))
            return "&4";
        if (before.equalsIgnoreCase("979C9F"))
            return "&7";
        if (before.equalsIgnoreCase("546E7A"))
            return "&8";
        return "";
    }

    public String getAllRoles(MessageReceivedEvent event) {
        String roles = "";
        for (Role role : event.getGuild().getMember(event.getAuthor()).getRoles()) {
            roles += role.getName() + this.plugin.getConfig()
                    .getString("discord.bridge-functionality.formatting.from-discord.multiple-role-separator");
        }
        if (!roles.isEmpty())
            roles = roles.substring(0,
                    roles.length() - this.plugin.getConfig()
                            .getString("discord.bridge-functionality.formatting.from-discord.multiple-role-separator")
                            .length());
        return roles;
    }

    public JDA getAPI() {
        return this.api;
    }

    public TextChannel getChatChannel() {
        return this.chatChannel;
    }

    public Map<Integer, User> getConfCodes() {
        return this.confCodes;
    }

    public TextChannel getConsoleChannel() {
        return this.consoleChannel;
    }

    public long getDiscordIDAndDeregisterCode(int confcode) {
        if (!confCodes.containsKey(confcode)) {
            return -1;
        }
        User usr = confCodes.get(confcode);
        confCodes.remove(confcode);
        return Long.parseLong(usr.getId());
    }

    public List<Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        List<Player> playersToRemove = new ArrayList<>();
        for (Player player : players) {
            if (this.checkPlayerIsVanished(player))
                playersToRemove.add(player);
        }
        players.removeAll(playersToRemove);
        return players;
    }

    public Tregmine getPlugin() {
        return this.plugin;
    }

    public String getPrimaryGroup(Player player) {
        // Begin Tregmine interjection
        GenericPlayer sender = this.plugin.getPlayer(player);
        if (sender.getRank().getsDiscordRank()) {
            return sender.getRank().getProperDiscordName();
        } else {
            return "";
        }
    }

    public String getRoleName(Role role) {
        return role == null ? "" : role.getName();
    }

    public Member getSelfMember() {
        return this.selfMember;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public boolean getSubscribed(UUID uniqueId) {
        return !unsubscribedPlayers.contains(uniqueId.toString());
    }

    public Role getTopRole(MessageReceivedEvent event) {
        Role highestRole = null;
        for (Role role : event.getGuild().getMember(event.getAuthor()).getRoles()) {
            if (highestRole == null)
                highestRole = role;
            else if (highestRole.getPosition() < role.getPosition())
                highestRole = role;
        }
        return highestRole;
    }

    public void onDisable() {
        // kill server log watcher & helper
        if (serverLogWatcher != null && !serverLogWatcher.isInterrupted())
            serverLogWatcher.interrupt();
        serverLogWatcher = null;
        if (serverLogWatcherHelper != null && !serverLogWatcherHelper.isInterrupted())
            serverLogWatcherHelper.interrupt();
        serverLogWatcherHelper = null;

        // server shutdown message
        if (chatChannel != null
                && this.plugin.getConfig().getBoolean("discord.bridge-functionality.start-stop.shutdown.enabled"))
            sendMessage(this.chatChannel,
                    /*new TregmineEmbedBuilder().createEmbed(EmbedAlertType.STATUS_UPDATE,
							*/
                    TregmineEmbedBuilder.genericEmbed(EmbedAlertType.STATUS_UPDATE.getDisplayName(), plugin.getConfig().getString("discord.bridge-functionality.start-stop.shutdown.message"),
                            Color.RED));

        // disconnect from discord
        try {
            api.shutdown(false);
        } catch (Exception e) {
            Tregmine.LOGGER.info("Discord shutting down before logged in");
        }
        api = null;

        // save unsubscribed users
        if (new File(this.plugin.getDataFolder(), "discord_unsubscribed.txt").exists())
            new File(this.plugin.getDataFolder(), "discord_unsubscribed.txt").delete();
        String players = "";
        for (String id : unsubscribedPlayers)
            players += id + "\n";
        if (players.length() > 0) {
            players = players.substring(0, players.length() - 1);
            try {
                FileUtils.writeStringToFile(new File(this.plugin.getDataFolder(), "discord_unsubscribed.txt"), players);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    public int sendConfirmationCode(String discordID) {
        User usr = null;
        for (Member member : chatChannel.getMembers()) {
            User user = member.getUser();
            Tregmine.LOGGER.info(user.getName());
            if (user.getName().equals(discordID)) {
                usr = user;
            }
        }

        if (usr == null) {
            return -1;
        } else {
            if (confCodes.containsValue(usr)) {
                confCodes.values().remove(usr);
            }
            Random rnd = new Random();
            int n = 100000 + rnd.nextInt(900000);
            confCodes.put(n, usr);
            Tregmine.LOGGER.info("DSV: Dispatching confirmation code " + n + " to Discord user " + usr.getName());
            usr.openPrivateChannel().complete().sendMessage(TregmineEmbedBuilder.genericOperationEmbed(EmbedAlertType.CONF_CODE.getDisplayName(), "/discord verify " + n)).complete();
            return n;
        }
    }

    public void sendMessage(TextChannel channel, MessageEmbed embed) {
        if (api == null || channel == null
                || (!PermissionUtil.checkPermission(channel, selfMember, Permission.MESSAGE_READ)
                || !PermissionUtil.checkPermission(channel, selfMember, Permission.MESSAGE_WRITE))) {
            Tregmine.LOGGER.warning("DSV: No Read/Write Permissions!");
            return;
        }
        channel.sendMessage(embed).complete();
    }

    public void sendMessage(TextChannel channel, String message) {
        sendMessage(channel, message, true);
    }

    public void sendMessage(TextChannel channel, String message, boolean editMessage) {
        if (api == null || channel == null
                || (!PermissionUtil.checkPermission(channel, selfMember, Permission.MESSAGE_READ)
                || !PermissionUtil.checkPermission(channel, selfMember, Permission.MESSAGE_WRITE))) {
            Tregmine.LOGGER.warning("DSV: No Read/Write Permissions!");
            return;
        }

        message = ChatColor.stripColor(message).replaceAll("[&§][0-9a-fklmnor]", "") // removing
                // &'s
                // with
                // addition
                // of
                // non-caught
                // §'s
                // if
                // they
                // get
                // through
                // somehow
                .replaceAll("\\[[0-9]{1,2};[0-9]{1,2};[0-9]{1,2}m", "").replaceAll("\\[[0-9]{1,3}m", "")
                .replace("[m", "");

        if (editMessage)
            for (String phrase : this.plugin.getConfig().getStringList("discord.bridge-functionality.censor-phrases"))
                message = message.replace(phrase, "");

        String overflow = null;
        if (message.length() > 2000) {
            Tregmine.LOGGER.warning("Tried sending message with length of " + message.length() + " ("
                    + (message.length() - 2000) + " over limit)");
            overflow = message.substring(1999);
            message = message.substring(0, 1999);
        }

        channel.sendMessage(message).complete();
        if (overflow != null)
            sendMessage(channel, overflow, editMessage);
    }

    public void sendMessageToChatChannel(String message) {
        sendMessage(chatChannel, message);
    }

    public void sendMessageToConsoleChannel(String message) {
        sendMessage(consoleChannel, message);
    }

    public void setSubscribed(UUID uniqueId, boolean subscribed) {
        if (subscribed && unsubscribedPlayers.contains(uniqueId.toString()))
            unsubscribedPlayers.remove(uniqueId.toString());
        if (!subscribed && !unsubscribedPlayers.contains(uniqueId.toString()))
            unsubscribedPlayers.add(uniqueId.toString());
    }

    public void notifyRank(String from, String message, Rank... rank){
        List<Member> notified = new ArrayList<>();
        for(Rank r : rank){
            List<Role> roles = this.guild.getRolesByName(r.getDiscordEquivalent(), true);
            if(roles.size() < 1){
                return;
            }
            Role role = roles.get(0);
            for(Member member : this.guild.getMembersWithRoles(role)){
                if(!member.getUser().hasPrivateChannel())
                    member.getUser().openPrivateChannel().complete();
                MessageEmbed embed = TregmineEmbedBuilder.genericEmbed("From " + from, message, Color.ORANGE);
                embed = new EmbedBuilder(embed).setAuthor(r.getDiscordEquivalent() + " Alert", null, null).build();
                member.getUser().getPrivateChannel().sendMessage(embed).complete();
            }
        }
    }

    public void startServerLogWatcher() {
        // kill server log watcher if it's already started
        if (serverLogWatcher != null && !serverLogWatcher.isInterrupted())
            serverLogWatcher.interrupt();
        serverLogWatcher = null;

        if (consoleChannel != null) {
            serverLogWatcher = new ServerLogWatcher(this);
            serverLogWatcher.start();
        }
    }

    private Boolean testChannel(TextChannel channel) {
        return channel != null;
    }
}
