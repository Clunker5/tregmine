package info.tregmine.discord.commands;

import java.util.List;

import net.dv8tion.jda.core.entities.Message;

public interface IDiscordCommand {
	public boolean handleExecution(Message message, String arguments);

	/**
	 * The roles permitted to use the specified command.
	 * 
	 * Possibly-null String List of roles permitted to use a command. Null if
	 * everyone.
	 */
	public List<String> rolesPermitted();
	
	public String getName();
	
	public String[] getAliases();
}
