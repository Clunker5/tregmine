package info.tregmine.discord.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import info.tregmine.Tregmine;
import info.tregmine.discord.DiscordSRV;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordListener extends ListenerAdapter {

	private Server server;
	private DiscordSRV srv;
	private Tregmine plugin;

	String lastMessageSent = "";

	public DiscordListener(DiscordSRV srv) {
		this.srv = srv;
		this.plugin = this.srv.getPlugin();
		this.server = this.plugin.getServer();
	}

	private void handleChat(MessageReceivedEvent event) {

		for (String phrase : this.plugin.getConfig().getStringList("discord.bridge-functionality.do-not-send-phrases"))
			if (event.getMessage().getContent().contains(phrase))
				return;

		synchronized (lastMessageSent) {
			if (lastMessageSent == event.getMessage().getId())
				return;
			else
				lastMessageSent = event.getMessage().getId();
		}

		String message = event.getMessage().getStrippedContent();
		if (message.length() == 0)
			return;
		if (this.plugin.getConfig().getBoolean("discord.bridge-functionality.commands.list.enabled")
				&& message.toLowerCase().startsWith(this.plugin.getConfig()
						.getString("discord.bridge-functionality.commands.list.command").toLowerCase())) {
			String playerlistMessage = "`"
					+ this.plugin.getConfig().getString("discord.bridge-functionality.commands.list.format.online")
							.replace("%playercount%", Integer.toString(this.srv.getOnlinePlayers().size()) + "/"
									+ Integer.toString(Bukkit.getMaxPlayers()))
					+ "\n";
			if (this.srv.getOnlinePlayers().size() == 0) {
				event.getChannel().sendMessage(
						this.plugin.getConfig().getString("discord.bridge-functionality.commands.list.format.offline"));
				return;
			}
			if (!Bukkit.getPluginManager().isPluginEnabled("VanishNoPacket"))
				for (Player playerNoVanish : Bukkit.getOnlinePlayers()) {
					if (playerlistMessage.length() < 2000)
						playerlistMessage += ChatColor.stripColor(playerNoVanish.getDisplayName()) + ", ";
				}
			else
				for (Player playerVanish : this.srv.getOnlinePlayers()) {
					if (playerlistMessage.length() < 2000)
						playerlistMessage += ChatColor.stripColor(playerVanish.getDisplayName()) + ", ";
				}
			playerlistMessage = playerlistMessage.substring(0, playerlistMessage.length() - 2);
			if (playerlistMessage.length() > 2000)
				playerlistMessage = playerlistMessage.substring(0, 1997) + "...";
			if (playerlistMessage.length() + 1 > 2000)
				playerlistMessage = playerlistMessage.substring(0, 2000);
			playerlistMessage += "`";
			this.srv.sendMessage((TextChannel) event.getChannel(), playerlistMessage);
			return;
		}
		if (message.length() > this.plugin.getConfig()
				.getInt("discord.bridge-functionality.discord-to-minecraft-max-char"))
			message = message.substring(0,
					this.plugin.getConfig().getInt("discord.bridge-functionality.discord-to-minecraft-max-char"));

		List<String> rolesAllowedToColor = this.plugin.getConfig()
				.getStringList("discord.bridge-functionality.roles-with-color-perm");

		String formatMessage = event.getGuild().getMember(event.getAuthor()).getRoles().isEmpty()
				? this.plugin.getConfig().getString("discord.bridge-functionality.formatting.from-discord.no-role")
				: this.plugin.getConfig().getString("discord.bridge-functionality.formatting.from-discord.with-role");

		Boolean shouldStripColors = true;
		for (Role role : event.getGuild().getMember(event.getAuthor()).getRoles())
			if (rolesAllowedToColor.contains(role.getName()))
				shouldStripColors = false;
		if (shouldStripColors)
			message = message.replaceAll("&([0-9a-qs-z])", ""); // color
																// stripping

		formatMessage = formatMessage.replace("%message%", message)
				.replace("%username%", event.getMessage().getAuthor().getName())
				.replace("%toprole%", this.srv.getRoleName(this.srv.getTopRole(event)))
				.replace("%toprolecolor%", this.srv.convertRoleToMinecraftColor(this.srv.getTopRole(event)))
				.replace("%allroles%", this.srv.getAllRoles(event)).replace("\\~", "~") // get
																						// rid
																						// of
																						// badly
																						// escaped
																						// characters
				.replace("\\*", "") // get rid of badly escaped characters
				.replace("\\_", "_"); // get rid of badly escaped characters

		formatMessage = formatMessage.replaceAll("&([0-9a-z])", "\u00A7$1");
		this.srv.broadcastMessageToMinecraftServer(formatMessage);
		Tregmine.LOGGER.info("DSV: " + ChatColor.stripColor(formatMessage));
	}

	private void handleConsole(MessageReceivedEvent event) {
		// general boolean for if command should be allowed
		Boolean allowed = false;
		// get if blacklist acts as whitelist
		Boolean DiscordConsoleChannelBlacklistActsAsWhitelist = this.plugin.getConfig()
				.getBoolean("discord.console-functionality.blacklist.is-whitelist");
		// get banned commands
		List<String> DiscordConsoleChannelBlacklistedCommands = this.plugin.getConfig()
				.getStringList("discord.console-functionality.blacklist.commands");
		// convert to all lower case
		for (int i = 0; i < DiscordConsoleChannelBlacklistedCommands.size(); i++)
			DiscordConsoleChannelBlacklistedCommands.set(i,
					DiscordConsoleChannelBlacklistedCommands.get(i).toLowerCase());
		// get message for manipulation
		String requestedCommand = event.getMessage().getContent();
		// remove all spaces at the beginning of the requested command to handle
		// pricks trying to cheat the system
		while (requestedCommand.substring(0, 1) == " ")
			requestedCommand = requestedCommand.substring(1);
		// select the first part of the requested command, being the main part
		// of it we care about
		requestedCommand = requestedCommand.split(" ")[0].toLowerCase(); // *op*
																			// person
		// command is on whitelist, allow
		if (DiscordConsoleChannelBlacklistActsAsWhitelist
				&& DiscordConsoleChannelBlacklistedCommands.contains(requestedCommand))
			allowed = true;
		else
			allowed = false;
		// command is on blacklist, deny
		if (!DiscordConsoleChannelBlacklistActsAsWhitelist
				&& DiscordConsoleChannelBlacklistedCommands.contains(requestedCommand))
			allowed = false;
		else
			allowed = true;
		// return if command not allowed
		if (!allowed)
			return;

		// log command to console log file, if this fails the command is not
		// executed for safety reasons unless this is turned off
		try (PrintWriter out = new PrintWriter(
				new BufferedWriter(new FileWriter(new File(new File(".").getAbsolutePath() + "/./"
						+ this.plugin.getConfig().getString("discord.console-functionality.logging.log"))
								.getAbsolutePath(),
						true)))) {
			out.println("[" + new Date() + " | ID " + event.getAuthor().getId() + "] " + event.getAuthor().getName()
					+ ": " + event.getMessage().getContent());
		} catch (IOException e) {
			Tregmine.LOGGER.warning("Error logging console action to "
					+ this.plugin.getConfig().getString("discord.console-functionality.logging.log"));
			if (this.plugin.getConfig().getBoolean("discord.debug.console.commands.cancel-if-log-failed"))
				return;
		}

		// if server is running paper spigot it has to have it's own little
		// section of code because it whines about timing issues
		if (!this.plugin.getConfig().getBoolean("discord.debug.console.commands.old-command-sender"))
			Bukkit.getScheduler().runTask(this.plugin, new Runnable() {
				@Override
				public void run() {
					server.dispatchCommand(server.getConsoleSender(), event.getMessage().getContent());
				}
			});
		else
			server.dispatchCommand(server.getConsoleSender(), event.getMessage().getContent());
	}

	private void handleDebug(MessageReceivedEvent event) {
		String message = event.getMessage().getContent();
		List<String> guildRoles = new ArrayList<String>();
		for (Role role : event.getGuild().getRoles())
			guildRoles.add(role.getName());
		List<String> guildTextChannels = new ArrayList<String>();
		for (TextChannel channel : event.getGuild().getTextChannels())
			guildTextChannels.add(channel.getName());
		List<String> guildVoiceChannels = new ArrayList<String>();
		for (VoiceChannel channel : event.getGuild().getVoiceChannels())
			guildVoiceChannels.add(channel.getName());
		message += "```\n";

		message += "GuildAfkChannelId: " + event.getGuild().getAfkChannel().getId() + "\n";
		message += "GuildAfkTimeout: " + event.getGuild().getAfkTimeout() + "\n";
		message += "GuildIconId: " + event.getGuild().getIconId() + "\n";
		message += "GuildIconUrl: " + event.getGuild().getIconUrl() + "\n";
		message += "GuildId: " + event.getGuild().getId() + "\n";
		message += "GuildName: " + event.getGuild().getName() + "\n";
		message += "GuildOwnerId: " + event.getGuild().getOwner().getUser().getId() + "\n";
		message += "GuildRegion: " + event.getGuild().getRegion().getName() + "\n";
		message += "GuildRoles: " + String.join(", ", guildRoles) + "\n";
		message += "GuildTextChannels: " + guildTextChannels + "\n";
		message += "GuildVoiceChannels: " + guildVoiceChannels + "\n";

		message += "```";
		sendMessage(event.getAuthor().getPrivateChannel(), message);
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event != null && event.getAuthor().getId() != null && event.getJDA().getSelfUser().getId() != null
				&& event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
			return;
		
			handleDebug(event);
		if (event.getTextChannel().equals(this.srv.getChatChannel()))
			handleChat(event);
		if (event.getTextChannel().equals(this.srv.getConsoleChannel()))
			handleConsole(event);
	}

	private void sendMessage(PrivateChannel channel, String message) {
		if (message.length() <= 2000) {
			channel.sendMessage(message);
			return;
		}
		channel.sendMessage(message.substring(0, 1999));
		message = message.substring(2000);
		sendMessage(channel, message);
	}
}