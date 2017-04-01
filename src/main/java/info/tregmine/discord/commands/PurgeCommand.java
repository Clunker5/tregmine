package info.tregmine.discord.commands;

import info.tregmine.Tregmine;
import info.tregmine.discord.Discord;
import info.tregmine.discord.DiscordUtil;
import info.tregmine.discord.entities.TregmineEmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.util.ArrayList;
import java.util.List;

public class PurgeCommand extends DiscordCommand {

    private int purgeIndex = 0;

    private Tregmine plugin;

    public PurgeCommand(Tregmine tregmine) {
        super(tregmine, "!purge", "!purge [Integer]", "Utility for cleaning out cluttered channels");
        this.plugin = tregmine;
    }

    @Override
    public boolean handleExecution(Message message, String arguments) {
        purgeIndex = 0;
        try {
            purgeIndex = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            Discord.DISCORD_UTIL.badNumber(message, 2, 100);
            return true;
        }
        if (purgeIndex < 2 || purgeIndex > 100) {
            Discord.DISCORD_UTIL.badNumber(message, 2, 100);
            return true;
        }
        Message alert = TregmineEmbedBuilder.genericOperationEmbedForUser("Purging " + purgeIndex + " most recent messages...", "This message will self-destruct in 5 seconds.", message.getAuthor());
        Discord.DISCORD_UTIL.sendDestructiveMessage(message.getChannel(), alert, 5);
        MessageHistory history = message.getChannel().getHistoryAround(message, purgeIndex + 1).complete();
        message.getTextChannel().deleteMessages(history.getRetrievedHistory()).complete();
        return true;
    }

    @Override
    public List<String> rolesPermitted() {
        List<String> roles = new ArrayList<String>();
        roles.add("Guardian");
        roles.add("Junior Admin");
        roles.add("Senior Admin");
        return roles;
    }


}
