package info.tregmine.discord.entities;

import info.tregmine.discord.DiscordDelegate;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

public class TregmineEmbedBuilder {

    private DiscordDelegate delegate;

    private String footer;
    private String footerIcon;

    public TregmineEmbedBuilder(DiscordDelegate delegate, String footer) {
        this.delegate = delegate;
        this.footer = footer;
        this.footerIcon = this.delegate.getClient().getSelfUser().getAvatarUrl();
    }

    public Message errorEmbedForUser(String title, String description, User forUser) {
        return new MessageBuilder().append("(" + forUser.getAsMention() + ")").setEmbed(new
                EmbedBuilder(null)
                .setColor(Color.RED)
                .setTitle(title, null)
                .setDescription(description)
                .setFooter(this.footer, this.footerIcon)
                .build())
                .build();
    }

    public String getFooter() {
        return this.footer;
    }

    public MessageEmbed errorEmbed(String title, String description) {
        return new
                EmbedBuilder(null)
                .setColor(Color.RED)
                .setTitle(title, null)
                .setDescription(description)
                .setFooter(this.footer, this.footerIcon)
                .build();
    }

    public Message genericOperationEmbedForUser(String title, String description, User forUser) {
        return
                new MessageBuilder().append(generateMention(forUser)).setEmbed(
                        new EmbedBuilder(null)
                                .setColor(Color.CYAN)
                                .setTitle(title, null)
                                .setDescription(description)
                                .setFooter("This operation was authorized by " + forUser.getName(), forUser.getAvatarUrl())
                                .build())
                        .build();
    }

    public MessageEmbed genericOperationEmbed(String title, String description) {
        return new EmbedBuilder(null)
                .setColor(Color.CYAN)
                .setTitle(title, null)
                .setDescription(description)
                .setFooter(this.footer, this.footerIcon)

                .build();
    }

    public Message genericEmbedForUser(String title, String description, Color color, User forUser) {
        return
                new MessageBuilder().append(generateMention(forUser)).setEmbed(
                        new EmbedBuilder(null)
                                .setColor(color)
                                .setTitle(title, null)
                                .setDescription(description)
                                .setFooter(this.footer, this.footerIcon)
                                .build())
                        .build();
    }

    public Message wrapEmbed(MessageEmbed embed) {
        return new MessageBuilder().setEmbed(embed).build();
    }

    public Message wrapEmbed(MessageEmbed embed, User forUser) {
        return new MessageBuilder().append(generateMention(forUser)).setEmbed(embed).build();
    }

    public MessageEmbed genericEmbed(String title, String description, Color color) {
        return new EmbedBuilder(null)
                .setColor(color)
                .setTitle(title, null)
                .setDescription(description)
                .setFooter(this.footer, this.footerIcon)
                .build();
    }


    private String generateMention(User user) {
        return "(" + user.getAsMention() + ")";
    }

}
