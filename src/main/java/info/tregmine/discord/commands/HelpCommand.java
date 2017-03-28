package info.tregmine.discord.commands;

import info.tregmine.Tregmine;
import info.tregmine.discord.DiscordUtil;
import info.tregmine.discord.entities.TregmineEmbedBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.util.Map;

/**
 * Created by ericrabil on 3/27/17.
 */
public class HelpCommand extends DiscordCommand {

    private static final String HEADER = "Use `!command [command]` to get detailed information on a command.\n\n";

    public HelpCommand(Tregmine tregmine, String command, String syntax, String description) {
        super(tregmine, command, syntax, description);
    }

    @Override
    public boolean handleExecution(Message message, String arguments) {
        message.delete().complete();
        if (arguments == "") {
            Map<String, IDiscordCommand> commands = this.plugin.getDiscordSRV().getCommandHandler().getExecutors();
            EmbedBuilder builder = new EmbedBuilder(null);
            String helper = HEADER;
            for (String name : commands.keySet()) {
                IDiscordCommand command = commands.get(name);
                helper += "**" + command.getName() + "**: " + command.getDescription() + "\n";
            }
            builder.setColor(Color.MAGENTA).setTitle("Command Index", null).setFooter(TregmineEmbedBuilder.TREGMINE_FOOTER, TregmineEmbedBuilder.TREGMINE_FOOTER_ICON)
                    .setDescription(helper);
            new DiscordUtil().sendDestructiveMessageWithDuration(message.getChannel(), TregmineEmbedBuilder.wrapEmbed(builder.build(), message.getAuthor()), 10).flagDestructive(message, 10);

            return true;
        } else {
            String commandInput = arguments.split(" ")[0];
            if (!commandInput.startsWith("!")) {
                commandInput = "!" + commandInput;
            }
            IDiscordCommand command = this.plugin.getDiscordSRV().getCommandHandler().getExecutors().get(commandInput);
            if (command == null) {
                new DiscordUtil().sendDestructiveMessage(message.getChannel(), TregmineEmbedBuilder.errorEmbedForUser("Command Not Found", "Sorry, `" + commandInput + "` is not a valid command. See `!help` for a list of commands.", message.getAuthor()));
            }
            EmbedBuilder builder = new EmbedBuilder(null);
            String helper = "";
            helper += "**" + command.getSyntax() + "**: " + command.getDescription() + "\n";
            builder.setColor(Color.MAGENTA).setTitle("Command Index", null).setFooter(TregmineEmbedBuilder.TREGMINE_FOOTER, TregmineEmbedBuilder.TREGMINE_FOOTER_ICON)
                    .setDescription(helper);
            new DiscordUtil().sendDestructiveMessageWithDuration(message.getChannel(), TregmineEmbedBuilder.wrapEmbed(builder.build(), message.getAuthor()), 10).flagDestructive(message, 10);
            return true;
        }
    }
}
