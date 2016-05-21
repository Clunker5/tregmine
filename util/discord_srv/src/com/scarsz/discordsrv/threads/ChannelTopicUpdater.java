package com.scarsz.discordsrv.threads;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.scarsz.discordsrv.DiscordSRV;
import com.scarsz.discordsrv.Lag;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.Flags;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;

public class ChannelTopicUpdater extends Thread {

	JDA api;
	Tregmine plugin;
	DiscordSRV discordsrv;

    public ChannelTopicUpdater(JDA api, Tregmine tregmine, DiscordSRV srv) {
        this.api = api;
        this.plugin = tregmine;
        this.discordsrv = srv;
    }

    public void run() {
    	int rate = 60 * 1000;

	    while (!isInterrupted())
	    {
	    	try {
	    		String chatTopic = applyFormatters(DiscordSRV.plugin.getConfig().getString("ChannelTopicUpdaterChatChannelTopicFormat"));
	    		String consoleTopic = applyFormatters(DiscordSRV.plugin.getConfig().getString("ChannelTopicUpdaterConsoleChannelTopicFormat"));

	    		if ((DiscordSRV.chatChannel == null && DiscordSRV.consoleChannel == null) || (chatTopic.isEmpty() && consoleTopic.isEmpty())) interrupt();
	    		if (DiscordSRV.api == null || (DiscordSRV.api != null && DiscordSRV.api.getSelfInfo() == null)) continue;

	    		if (!chatTopic.isEmpty() && DiscordSRV.chatChannel != null && !DiscordSRV.chatChannel.checkPermission(DiscordSRV.api.getSelfInfo(), Permission.MANAGE_CHANNEL))
	    			DiscordSRV.plugin.getLogger().warning("Unable to update chat channel; no permission to manage channel");
	    		if (!consoleTopic.isEmpty() && DiscordSRV.consoleChannel != null && !DiscordSRV.consoleChannel.checkPermission(DiscordSRV.api.getSelfInfo(), Permission.MANAGE_CHANNEL))
	    			DiscordSRV.plugin.getLogger().warning("Unable to update console channel; no permission to manage channel");

	    		if (!chatTopic.isEmpty() && DiscordSRV.chatChannel != null && DiscordSRV.chatChannel.checkPermission(DiscordSRV.api.getSelfInfo(), Permission.MANAGE_CHANNEL))
	    			DiscordSRV.chatChannel.getManager().setTopic(chatTopic).update();
	    		if (!consoleTopic.isEmpty() && DiscordSRV.consoleChannel != null && DiscordSRV.consoleChannel.checkPermission(DiscordSRV.api.getSelfInfo(), Permission.MANAGE_CHANNEL))
	    			DiscordSRV.consoleChannel.getManager().setTopic(consoleTopic).update();

	    		Thread.sleep(rate);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
    }

    private String applyFormatters(String input) {
    	if (DiscordSRV.plugin.getConfig().getBoolean("PrintTiming")) DiscordSRV.plugin.getLogger().info("Format start: " + input);
    	long startTime = System.nanoTime();
    	
    	int onlineplayers = discordsrv.getOnlinePlayers().size();
    	for(Player online : discordsrv.getOnlinePlayers()){
    		TregminePlayer player = plugin.getPlayer(online);
    		if(player.hasFlag(Flags.INVISIBLE)) onlineplayers = onlineplayers - 1;
    	}
    	
    	input = input
    			.replace("%playercount%", Integer.toString(onlineplayers))
    			.replace("%playermax%", Integer.toString(Bukkit.getMaxPlayers()))
    			.replace("%date%", new Date().toString())
    			.replace("%totalplayers%", Integer.toString(new File(Bukkit.getWorlds().get(0).getWorldFolder().getAbsolutePath(), "/playerdata").listFiles().length))
    			.replace("%uptimemins%", Long.toString(TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - DiscordSRV.startTime)))
    			.replace("%uptimehours%", Long.toString(TimeUnit.NANOSECONDS.toHours(System.nanoTime() - DiscordSRV.startTime)))
    			.replace("%motd%", Bukkit.getMotd().replaceAll("&([0-9a-qs-z])", ""))
    			.replace("%serverversion%", Bukkit.getBukkitVersion())
    			.replace("%freememory%", Long.toString((Runtime.getRuntime().freeMemory())/1024/1024))
    			.replace("%usedmemory%", Long.toString((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024/1024))
    			.replace("%totalmemory%", Long.toString((Runtime.getRuntime().totalMemory())/1024/1024))
    			.replace("%maxmemory%", Long.toString((Runtime.getRuntime().maxMemory())/1024/1024))
    			.replace("%tps%", Lag.getTPSString())
    	;
    	

    	if (DiscordSRV.plugin.getConfig().getBoolean("PrintTiming")) DiscordSRV.plugin.getLogger().info("Format done in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms: " + input);

    	return input;
    }
}