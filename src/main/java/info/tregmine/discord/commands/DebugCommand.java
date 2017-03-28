package info.tregmine.discord.commands;

import info.tregmine.Tregmine;
import info.tregmine.discord.DiscordUtil;
import info.tregmine.discord.RandomString;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

import java.util.ArrayList;
import java.util.List;

public class DebugCommand extends DiscordCommand {

    private static final String cmdSyntax = "[fillChatWithJunk] [Integer]";

    private int junkToSend = 0;

    public DebugCommand(Tregmine tregmine) {
        super(tregmine, "!debug", "!debug " + cmdSyntax, "Debugging command for Tregmine's Discord component.");
    }

    @Override
    public boolean handleExecution(Message message, String arguments) {
        if (arguments.trim().isEmpty()) {
            this.invalidSyntax(message);
            return true;
        }
        String[] args = arguments.split(" ");
        if (args.length < 2) {
            this.invalidSyntax(message);
            return true;
        }
        switch (args[0]) {
            case "fillChatWithJunk":
                try {
                    junkToSend = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    this.invalidSyntax(message);
                    return true;
                }
                if (junkToSend < 1) {
                    new DiscordUtil().badNumber(message, 1, false);
                    return true;
                }
                RandomString strBldr = new RandomString(10);
                while (junkToSend != 0) {
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            while (junkToSend != 0) {
                                junkToSend--;
                                try {
                                    message.getChannel().sendMessage(strBldr.nextString());
                                } catch (ErrorResponseException e) {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                            return;
                        }
                    });
                    t.start();
                }

        }
        return true;
    }

    @Override
    public List<String> rolesPermitted() {
        List<String> roles = new ArrayList<String>();
        roles.add("Senior Admin");
        return roles;
    }

}
