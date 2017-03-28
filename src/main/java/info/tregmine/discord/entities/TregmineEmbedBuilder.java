package info.tregmine.discord.entities;

import info.tregmine.discord.DiscordSRV;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

public class TregmineEmbedBuilder {

    public static final String TREGMINE_FOOTER = "Made by TregBot with lots of love";
    public static final String TREGMINE_FOOTER_ICON = DiscordSRV.selfUser.getAvatarUrl();

    public static Message errorEmbedForUser(String title, String description, User forUser) {
        return new MessageBuilder().append("(" + forUser.getAsMention() + ")").setEmbed(new
                EmbedBuilder(null)
                .setColor(Color.RED)
                .setTitle(title, null)
                .setDescription(description)
                .setFooter(TREGMINE_FOOTER, TREGMINE_FOOTER_ICON)
                .build())
                .build();
    }

    public static MessageEmbed errorEmbed(String title, String description) {
        return new
                EmbedBuilder(null)
                .setColor(Color.RED)
                .setTitle(title, null)
                .setDescription(description)
                .setFooter(TREGMINE_FOOTER, TREGMINE_FOOTER_ICON)
                .build();
    }

    public static Message genericOperationEmbedForUser(String title, String description, User forUser) {
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

    public static MessageEmbed genericOperationEmbed(String title, String description) {
        return new EmbedBuilder(null)
                .setColor(Color.CYAN)
                .setTitle(title, null)
                .setDescription(description)
                .setFooter(TREGMINE_FOOTER, TREGMINE_FOOTER_ICON)

                .build();
    }

    public static Message genericEmbedForUser(String title, String description, Color color, User forUser) {
        return
                new MessageBuilder().append(generateMention(forUser)).setEmbed(
                        new EmbedBuilder(null)
                                .setColor(color)
                                .setTitle(title, null)
                                .setDescription(description)
                                .setFooter(TREGMINE_FOOTER, TREGMINE_FOOTER_ICON)
                                .build())
                        .build();
    }

    public static Message wrapEmbed(MessageEmbed embed) {
        return new MessageBuilder().setEmbed(embed).build();
    }

    public static Message wrapEmbed(MessageEmbed embed, User forUser) {
        return new MessageBuilder().append(generateMention(forUser)).setEmbed(embed).build();
    }

    public static MessageEmbed genericEmbed(String title, String description, Color color) {
        return new EmbedBuilder(null)
                .setColor(color)
                .setTitle(title, null)
                .setDescription(description)
                .setFooter(TREGMINE_FOOTER, TREGMINE_FOOTER_ICON)
                .build();
    }


    private static String generateMention(User user) {
        return "(" + user.getAsMention() + ")";
    }

}
