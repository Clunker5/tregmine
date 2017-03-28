package info.tregmine.discord.commands;

import info.tregmine.Tregmine;
import info.tregmine.discord.DiscordUtil;
import info.tregmine.discord.entities.TregmineEmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.util.ArrayList;
import java.util.List;

public class PurgeCommand extends DiscordCommand {

    private int purgeIndex = 0;

    public PurgeCommand(Tregmine tregmine) {
        super(tregmine, "!purge", "!purge [Integer]", "Utility for cleaning out cluttered channels");
    }

    @Override
    public boolean handleExecution(Message message, String arguments) {
        purgeIndex = 0;
        try {
            purgeIndex = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            new DiscordUtil().badNumber(message, 2, 100);
            return true;
        }
        if (purgeIndex < 2 || purgeIndex > 100) {
            new DiscordUtil().badNumber(message, 2, 100);
            return true;
        }
        Message alert = TregmineEmbedBuilder.genericOperationEmbedForUser("Purging " + purgeIndex + " most recent messages...", "This message will self-destruct in 5 seconds.", message.getAuthor());
        Message msg = new DiscordUtil().sendDestructiveMessage(message.getChannel(), alert, 5);
        MessageHistory history = message.getChannel().getHistoryAround(message, purgeIndex + 1).complete();
        Thread t = new Thread(new Runnable() {
            public void run() {
                for (Message m : history.getRetrievedHistory()) {
                    System.out.print(m.getId());
                    System.out.println(msg.getId());
                    try {
                        m.delete().complete(true);
                    } catch (RateLimitedException e) {
                        try {
                            Thread.sleep(10000);
                            m.delete().complete(true);
                            continue;
                        } catch (InterruptedException | RateLimitedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                return;
            }
        });
        t.start();
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
