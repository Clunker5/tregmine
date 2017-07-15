package info.tregmine.discord;

import info.tregmine.Tregmine;
import info.tregmine.discord.entities.EmbedType;
import info.tregmine.discord.entities.TregmineEmbedBuilder;
import info.tregmine.discord.listeners.DiscordListener;
import info.tregmine.discord.listeners.MinecraftListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.impl.GameImpl;
import net.dv8tion.jda.core.exceptions.GuildUnavailableException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eric on 7/6/17.
 */
public class DiscordDelegate {

    private Tregmine plugin;
    private JDA client = null;

    /* The guilds and channels */
    private Guild guild;
    private TextChannel chatChannel;

    /* Utilities */
    private TregmineEmbedBuilder embedBuilder;

    public DiscordDelegate(Tregmine plugin) throws GuildUnavailableException, LoginException, InterruptedException, RateLimitedException {
        this.plugin = plugin;

        this.connect(this.plugin.getConfig().getString("discord-bot.token"), this.plugin.getConfig().getString("discord-bot.status"));

        this.guild = this.client.getGuildById(this.plugin.getConfig().getString("discord-bot.guild.guild-id"));
        this.chatChannel = this.client.getTextChannelById(this.plugin.getConfig().getString("discord-bot.guild.chat-channel-id"));

        if (this.guild == null || this.chatChannel == null) {
            throw new GuildUnavailableException("The guild or channel provided in the configuration does not exist. Please fix the configuration and restart Tregmine.");
        }

        this.plugin.getServer().getPluginManager().registerEvents(new MinecraftListener(this), this.plugin);

        this.client.addEventListener(new DiscordListener(this));

        this.embedBuilder = new TregmineEmbedBuilder(this, this.getString("behavior.embed-footer"));

        if (this.getBoolean("behavior.startup.enabled")) {
            this.sendChat(this.embedBuilder.genericEmbed(EmbedType.STATUS_UPDATE.getTitle(), this.getString("behavior.startup.message"), Color.GREEN));
        }


    }

    public void connect(String token, String status) throws LoginException, InterruptedException, RateLimitedException {
        if (this.client != null) this.client.shutdown(false);
        this.client = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
        if (status != null) {
            Game game = new GameImpl(status, null, Game.GameType.DEFAULT);
            this.client.getPresence().setGame(game);
        }
    }

    public void log(String... messages) {
        Tregmine.LOGGER.info(String.join(" ", messages));
    }

    public boolean getBoolean(String nodes) {
        return this.plugin.getConfig().getBoolean("discord-bot." + nodes);
    }

    public String getString(String nodes) {
        return this.plugin.getConfig().getString("discord-bot." + nodes);
    }

    public void sendChat(MessageEmbed embed) {
        this.chatChannel.sendMessage(embed).queue();
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

    public void sendChat(String message) {
        this.chatChannel.sendMessage(this.convertMentionsFromNames(message)).queue();
    }

    public TextChannel getChatChannel() {
        return this.chatChannel;
    }

    public Tregmine getPlugin() {
        return this.plugin;
    }

    public JDA getClient() {
        return this.client;
    }

    public void sendShutdownSignal() {
        if (this.getBoolean("behavior.shutdown.enabled"))
            this.sendChat(this.embedBuilder.errorEmbed("Server Offline", this.getString("behavior.shutdown.message")));
    }

    public TregmineEmbedBuilder getEmbedBuilder() {
        return this.embedBuilder;
    }

    public String convertRoleToMinecraftColor(Role role) {
        if (role == null || role.getColor() == null) {
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

    public Role getTopRole(List<Role> roles) {
        Role highestRole = null;
        for (Role role : roles) {
            if (highestRole == null)
                highestRole = role;
            else if (highestRole.getPosition() < role.getPosition())
                highestRole = role;
        }
        return highestRole == null ? this.chatChannel.getGuild().getRolesByName("@everyone", true).get(0) : highestRole;
    }
}
