package info.tregmine.discord.entities;

import java.awt.Color;
import java.time.OffsetDateTime;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

public class TregmineEmbedBuilder {

	private static final String footer = "This is an auto-generated message by TregBot";

	public MessageEmbed createEmbed(EmbedAlertType alertType, String message, Color color) {
		return new EmbedBuilder(null).setColor(alertType.getColor() == null ? color : alertType.getColor()).setTitle(alertType.getDisplayName(), null).setDescription(message)
				.setFooter(TregmineEmbedBuilder.footer, null).build();
	}
	
	public MessageEmbed createEmbed(EmbedAlertType alertType){
		return new EmbedBuilder(null).setColor(alertType.getColor()).setTitle(alertType.getDisplayName(), null).setFooter(TregmineEmbedBuilder.footer, null).build();
	}
	
	public static MessageEmbed errorEmbed(String title, String description, User forUser){
		return new EmbedBuilder(null)
				.setColor(Color.RED)
				.setTitle(title, null)
				.setDescription(description)
				.setFooter("This message is for " + forUser.getName(), forUser.getAvatarUrl())
				.build();
	}
	
	public static MessageEmbed genericOperationEmbed(String title, String description, User forUser){
		return new EmbedBuilder(null)
				.setColor(Color.CYAN)
				.setTitle(title, null)
				.setDescription(description)
				.setFooter("This operation was initiated by " + forUser.getName(), forUser.getAvatarUrl())
				.setTimestamp(OffsetDateTime.now())
				
				.build();
	}

}
