package info.tregmine.discord.commands;

import java.util.ArrayList;
import java.util.List;

import info.tregmine.Tregmine;
import info.tregmine.discord.DiscordUtil;
import info.tregmine.discord.entities.TregmineEmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.impl.MessageImpl;

public class PurgeCommand extends DiscordCommand{

	public PurgeCommand(Tregmine tregmine) {
		super(tregmine, "!purge");
	}
	
	@Override
	public boolean handleExecution(Message message, String arguments){
		int purgeIndex = 0;
		try{
			purgeIndex = Integer.parseInt(arguments);
		}catch(NumberFormatException e){
			badNumber(message);
			return true;
		}
		if(purgeIndex < 2 || purgeIndex > 100){
			badNumber(message);
			return true;
		}
		MessageEmbed alert = TregmineEmbedBuilder.genericOperationEmbed("Purging " + purgeIndex + " most recent messages...", "This message will self-destruct in 5 seconds.", message.getAuthor());
		
		DiscordUtil.sendDestructiveMessage(message.getChannel(), alert, 5);
		MessageHistory history = message.getChannel().getHistoryAround(message, purgeIndex + 1).complete();
		for(Message m : history.getRetrievedHistory()){
			if(m.getCreationTime() == alert.getTimestamp())
				continue;
			m.delete().complete();
		}
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
	
	private void badNumber(Message message){
		DiscordUtil.sendDestructiveMessage(message.getChannel(), TregmineEmbedBuilder.errorEmbed("Bad Number", "Please enter a number between 2 and 99.", message.getAuthor()), 10);
		
	}


}
