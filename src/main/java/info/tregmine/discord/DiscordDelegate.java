package info.tregmine.discord;

import info.tregmine.Tregmine;
import info.tregmine.discord.entities.EmbedType;
import info.tregmine.discord.entities.TregmineEmbedBuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.impl.GameImpl;
import net.dv8tion.jda.core.exceptions.GuildUnavailableException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.awt.*;

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

    public Message sendChat(MessageEmbed embed) {
        return this.chatChannel.sendMessage(embed).complete();
    }

    public Message sendChat(String message) {
        return this.chatChannel.sendMessage(message).complete();
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

    public TregmineEmbedBuilder getEmbedBuilder() {
        return this.embedBuilder;
    }
}
