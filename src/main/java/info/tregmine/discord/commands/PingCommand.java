package info.tregmine.discord.commands;

import info.tregmine.Tregmine;
import info.tregmine.discord.Discord;
import info.tregmine.discord.DiscordUtil;
import info.tregmine.discord.entities.EmbedAlertType;
import info.tregmine.discord.entities.TregmineEmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class PingCommand extends DiscordCommand {

    public PingCommand(Tregmine tregmine) {
        super(tregmine, "!ping", "!ping", "Simple ping command to ensure bot is online.");
    }

    @Override
    public boolean handleExecution(Message message, String arguments) {
        MessageChannel fromChannel = message.getChannel();
        Discord.DISCORD_UTIL.flagDestructive(message);
        Discord.DISCORD_UTIL.sendDestructiveMessage(fromChannel, TregmineEmbedBuilder.genericEmbedForUser(EmbedAlertType.PING.getDisplayName(), "All systems are A-OK!", EmbedAlertType.PING.getColor(), message.getAuthor()), 10);
        return true;
    }

}
