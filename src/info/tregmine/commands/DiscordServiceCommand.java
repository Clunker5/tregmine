package info.tregmine.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import net.dv8tion.jda.core.entities.Icon;

public class DiscordServiceCommand extends AbstractCommand {

	private Tregmine plugin;

	public DiscordServiceCommand(Tregmine tregmine) {
		super(tregmine, "discordservice");
		this.plugin = tregmine;
	}

	@Override
	public boolean handlePlayer(TregminePlayer sender, String[] args) {
		if (args.length == 0) {
			if (!sender.isOp())
				sender.sendStringMessage("/discordsrv toggle/subscribe/unsubscribe");
			else
				sender.sendStringMessage("/discordsrv setpicture/reload/rebuild/debug/toggle/subscribe/unsubscribe");
			return true;
		}
		if (args[0].equalsIgnoreCase("setpicture")) {
			if (!sender.isOp()) {
				sender.sendStringMessage("Must be OP to use this command");
				return true;
			}
			if (args.length < 2) {
				sender.sendStringMessage("Must give URL to picture to set as bot picture");
				return true;
			}
			try {
				sender.sendStringMessage("Downloading picture...");
				ReadableByteChannel in = Channels.newChannel(new URL(args[1]).openStream());
				FileChannel out = new FileOutputStream(this.plugin.getDataFolder().getAbsolutePath() + "/picture.jpg")
						.getChannel();
				out.transferFrom(in, 0, Long.MAX_VALUE);
			} catch (IOException e) {
				sender.sendStringMessage("Download failed: " + e.getMessage());
				return true;
			}
			try {
				this.plugin.getDiscordSRV().getAPI().getSelfUser().getManager()
						.setAvatar(Icon.from(new File(this.plugin.getDataFolder().getAbsolutePath() + "/picture.jpg")))
						.complete();
				sender.sendStringMessage("Picture updated successfully");
			} catch (IOException e) {
				sender.sendStringMessage("Error setting picture as avatar: " + e.getMessage());
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("debug")) {
			if (!sender.isOp())
				return true;
			FileReader fr = null;
			try {
				fr = new FileReader(new File(new File(".").getAbsolutePath() + "/logs/latest.log").getAbsolutePath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			BufferedReader br = new BufferedReader(fr);

			List<String> discordsrvMessages = new ArrayList<>();
			discordsrvMessages.add(ChatColor.RED + "Lines for DiscordSRV from latest.log:");
			Boolean done = false;
			while (!done) {
				String line = null;
				try {
					line = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (line == null)
					done = true;
				if (line != null && line.toLowerCase().contains("discordsrv"))
					discordsrvMessages.add(line);
			}
			discordsrvMessages.add(ChatColor.AQUA + "Version: " + ChatColor.RESET + Bukkit.getVersion());
			discordsrvMessages.add(ChatColor.AQUA + "Bukkit version: " + ChatColor.RESET + Bukkit.getBukkitVersion());
			discordsrvMessages.add(ChatColor.AQUA + "OS: " + ChatColor.RESET + System.getProperty("os.name"));
			for (String message : discordsrvMessages)
				sender.sendStringMessage(message);
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("rebuild")) {
			if (!sender.isOp())
				return true;
			// buildJda();
			sender.sendStringMessage("Disabled because no workie");
			return true;
		}

		if (!(sender instanceof Player))
			return true;
		Player senderPlayer = (Player) sender;
		if (args[0].equalsIgnoreCase("toggle")) {
			Boolean subscribed = this.plugin.getDiscordSRV().getSubscribed(senderPlayer.getUniqueId());
			this.plugin.getDiscordSRV().setSubscribed(senderPlayer.getUniqueId(), !subscribed);

			String subscribedMessage = this.plugin.getDiscordSRV().getSubscribed(senderPlayer.getUniqueId())
					? "subscribed" : "unsubscribed";
			sender.sendStringMessage(ChatColor.AQUA + "You have been " + subscribedMessage + " to Discord messages.");
		}
		if (args[0].equalsIgnoreCase("subscribe")) {
			this.plugin.getDiscordSRV().setSubscribed(senderPlayer.getUniqueId(), true);
			sender.sendStringMessage(ChatColor.AQUA + "You have been subscribed to Discord messages.");
		}
		if (args[0].equalsIgnoreCase("unsubscribe")) {
			this.plugin.getDiscordSRV().setSubscribed(senderPlayer.getUniqueId(), false);
			sender.sendStringMessage(ChatColor.AQUA + "You are no longer subscribed to Discord messages.");
		}
		return true;
	}

}
