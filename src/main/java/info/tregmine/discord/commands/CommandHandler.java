package info.tregmine.discord.commands;

import info.tregmine.Tregmine;
import info.tregmine.discord.DiscordSRV;
import info.tregmine.discord.DiscordUtil;
import info.tregmine.discord.entities.ExecutorHashMap;
import info.tregmine.discord.entities.TregmineEmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter{
	
	private DiscordSRV srv;
	private Tregmine plugin;
	
	private ExecutorHashMap<String, IDiscordCommand> executors = new ExecutorHashMap<String, IDiscordCommand>();
	
	public CommandHandler(DiscordSRV srv){
		this.srv = srv;
		this.srv.getAPI().addEventListener(this);
		this.plugin = this.srv.getPlugin();
		
		executors.addExecutor(new PingCommand(this.plugin));
		executors.addExecutor(new PurgeCommand(this.plugin));
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event){
		if(!event.getMessage().getContent().startsWith("!") || event.getMessage().getContent().startsWith("!!")){
			return;
		}
		String[] components = event.getMessage().getContent().split(" ", 2);
		IDiscordCommand command = this.executors.get(components[0]);
		if(command == null){
			DiscordUtil.flagDestructive(event.getMessage());
			unknownCommand(event.getChannel(), components[0], event.getAuthor());
			return;
		}
		if(command.rolesPermitted() != null){
			boolean accessible = false;
			for(Role r : event.getMember().getRoles()){
				if(command.rolesPermitted().contains(r.getName()))
					accessible = true;
					break;
			}
			if(!accessible){
				DiscordUtil.flagDestructive(event.getMessage());
				insufficientPerms(event.getChannel(), components[0], event.getAuthor());
				return;
			}
		}
		command.handleExecution(event.getMessage(), components.length < 2 ? "" : components[1]);
	}
	
	private void unknownCommand(MessageChannel channel, String command, User author){
		DiscordUtil.sendDestructiveMessage(channel, TregmineEmbedBuilder.errorEmbed("Command Not Found", "Sorry, `"+command+"` is not a valid command.", author));
	}
	
	private void insufficientPerms(MessageChannel channel, String command, User author){
		DiscordUtil.sendDestructiveMessage(channel, TregmineEmbedBuilder.errorEmbed("Insufficient Permissions", "Sorry, you don't have authorization to perform `"+command+"`", author));
	}
	
}