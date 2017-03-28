package info.tregmine.discord.commands;

import net.dv8tion.jda.core.entities.Message;

import java.util.List;

public interface IDiscordCommand {
    boolean handleExecution(Message message, String arguments);

    /**
     * The roles permitted to use the specified command.
     * <p>
     * Possibly-null String List of roles permitted to use a command. Null if
     * everyone.
     */
    List<String> rolesPermitted();

    String getName();

    String[] getAliases();

    String getDescription();

    String getSyntax();
}
