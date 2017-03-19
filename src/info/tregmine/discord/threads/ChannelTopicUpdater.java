package info.tregmine.discord.threads;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import info.tregmine.Tregmine;
import info.tregmine.api.Lag;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.Flags;
import info.tregmine.discord.DiscordSRV;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class ChannelTopicUpdater extends Thread {

	JDA api;
	Tregmine plugin;
	DiscordSRV srv;

	public ChannelTopicUpdater(DiscordSRV srv) {
		this.srv = srv;
		this.api = this.srv.getAPI();
		this.plugin = this.srv.getPlugin();
	}

	@Override
	public void run() {
		int rate = 60 * 1000;

		while (!isInterrupted()) {
			try {
				String chatTopic = applyFormatters(
						this.plugin.getConfig().getString("discord.topic-updater.chat-format"));
				String consoleTopic = applyFormatters(
						this.plugin.getConfig().getString("discord.topic-updater.console-format"));

				if ((this.srv.getChatChannel() == null && this.srv.getConsoleChannel() == null)
						|| (chatTopic.isEmpty() && consoleTopic.isEmpty()))
					interrupt();
				if (this.api == null || (this.api != null && this.api.getSelfUser() == null))
					continue;

				if (!chatTopic.isEmpty() && this.srv.getChatChannel() != null && !PermissionUtil.checkPermission(this.srv.getChatChannel(), 
						this.srv.getSelfMember(), Permission.MANAGE_CHANNEL))
					Tregmine.LOGGER
							.warning("Unable to update chat channel; no permission to manage channel");
				if (!consoleTopic.isEmpty() && this.srv.getConsoleChannel() != null && !PermissionUtil.checkPermission(this.srv.getConsoleChannel(), 
						this.srv.getSelfMember(), Permission.MANAGE_CHANNEL))
					Tregmine.LOGGER
							.warning("Unable to update console channel; no permission to manage channel");

				if (!chatTopic.isEmpty() && this.srv.getChatChannel() != null && PermissionUtil.checkPermission(this.srv.getChatChannel(), 
						this.srv.getSelfMember(), Permission.MANAGE_CHANNEL))
					this.srv.getChatChannel().getManager().setTopic(chatTopic).complete();
				if (!consoleTopic.isEmpty() && this.srv.getConsoleChannel() != null && PermissionUtil.checkPermission(this.srv.getConsoleChannel(), 
						this.srv.getSelfMember(), Permission.MANAGE_CHANNEL))
					this.srv.getConsoleChannel().getManager().setTopic(consoleTopic).complete();

				Thread.sleep(rate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String applyFormatters(String input) {
		if (this.plugin.getConfig().getBoolean("discord.debug.misc.print-timing"))
			Tregmine.LOGGER.info("Format start: " + input);
		long startTime = System.nanoTime();

		int onlineplayers = this.srv.getOnlinePlayers().size();
		for (Player online : this.srv.getOnlinePlayers()) {
			TregminePlayer player = plugin.getPlayer(online);
			if (player.hasFlag(Flags.INVISIBLE))
				onlineplayers = onlineplayers - 1;
		}

		this.plugin.getLag();
		input = input.replace("%playercount%", Integer.toString(onlineplayers))
				.replace("%playermax%", Integer.toString(Bukkit.getMaxPlayers()))
				.replace("%date%",
						new Date().toString())
				.replace("%totalplayers%",
						Integer.toString(
								new File(Bukkit.getWorlds().get(0).getWorldFolder().getAbsolutePath(), "/playerdata")
										.listFiles().length))
				.replace("%uptimemins%",
						Long.toString(TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - this.srv.getStartTime())))
				.replace("%uptimehours%",
						Long.toString(TimeUnit.NANOSECONDS.toHours(System.nanoTime() - this.srv.getStartTime())))
				.replace("%motd%", Bukkit.getMotd().replaceAll("&([0-9a-qs-z])", ""))
				.replace("%serverversion%", Bukkit.getBukkitVersion())
				.replace("%freememory%", Long.toString((Runtime.getRuntime().freeMemory()) / 1024 / 1024))
				.replace("%usedmemory%",
						Long.toString(
								(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024))
				.replace("%totalmemory%", Long.toString((Runtime.getRuntime().totalMemory()) / 1024 / 1024))
				.replace("%maxmemory%", Long.toString((Runtime.getRuntime().maxMemory()) / 1024 / 1024))
				.replace("%tps%", Double.toString(Lag.getTPS()));

		if (this.plugin.getConfig().getBoolean("discord.debug.misc.print-timing"))
			Tregmine.LOGGER.info(
					"Format done in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms: " + input);

		return input;
	}
}