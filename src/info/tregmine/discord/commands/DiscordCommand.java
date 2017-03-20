package info.tregmine.discord.commands;

import java.util.List;

import info.tregmine.Tregmine;
import net.dv8tion.jda.core.entities.Message;

public class DiscordCommand implements IDiscordCommand{
	
	private String name;
	private Tregmine plugin;
	
	public DiscordCommand(Tregmine tregmine, String command){
		this.name = command;
		this.plugin = tregmine;
	}
	
	public String getName(){
		return this.name;
	}

	@Override
	public boolean handleExecution(Message message, String arguments) {
		return true;
	}

	@Override
	public List<String> rolesPermitted() {
		return null;
	}

	@Override
	public String[] getAliases() {
		return new String[0];
	}

}
