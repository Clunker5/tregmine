package info.tregmine.discord.commands;

import info.tregmine.Tregmine;
import info.tregmine.discord.DiscordUtil;
import info.tregmine.discord.entities.EmbedAlertType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class PingCommand extends DiscordCommand{

	public PingCommand(Tregmine tregmine) {
		super(tregmine, "!ping");
	}
	
	@Override
	public boolean handleExecution(Message message, String arguments){
		MessageChannel fromChannel = message.getChannel();
		MessageEmbed embed = new EmbedBuilder(null).setTitle(EmbedAlertType.PING.getDisplayName(), null)
				.setColor(EmbedAlertType.PING.getColor())
				.setDescription("All systems are a-okay!")
				.setFooter("This operation was triggered by `" + message.getAuthor().getName() + "`", message.getAuthor().getAvatarUrl())
				.build();
		DiscordUtil.flagDestructive(message);
		DiscordUtil.sendDestructiveMessage(fromChannel, '[' + message.getAuthor().getAsMention() + ']');
		DiscordUtil.sendDestructiveMessage(fromChannel, embed, 10);
		return true;
	}

}
