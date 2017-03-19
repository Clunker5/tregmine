package info.tregmine.discord.entities;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class TregmineEmbedBuilder {
	
	private final String footer = "This is an auto-generated message by TregBot";
	
	public MessageEmbed createEmbed(EmbedAlertType alertType, String message, Color color){
		return new EmbedBuilder(null)
		.setColor(color)
		.setTitle(alertType.getDisplayName(), null)
		.setDescription(message)
		.setFooter(this.footer, null)
		.build();
	}

}
