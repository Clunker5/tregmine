package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import net.dv8tion.jda.core.entities.Icon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

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
                sender.sendMessage("/discordsrv toggle/subscribe/unsubscribe");
            else
                sender.sendMessage("/discordsrv setpicture/reload/rebuild/debug/toggle/subscribe/unsubscribe");
            return true;
        }
        if (args[0].equalsIgnoreCase("setpicture")) {
            if (!sender.isOp()) {
                sender.sendMessage("Must be OP to use this command");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("Must give URL to picture to set as bot picture");
                return true;
            }
            try {
                sender.sendMessage("Downloading picture...");
                ReadableByteChannel in = Channels.newChannel(new URL(args[1]).openStream());
                FileOutputStream os = new FileOutputStream(this.plugin.getDataFolder().getAbsolutePath() + "/picture.jpg");
                FileChannel out = os.getChannel();
                out.transferFrom(in, 0, Long.MAX_VALUE);
                os.close();
            } catch (IOException e) {
                sender.sendMessage("Download failed: " + e.getMessage());
                return true;
            }
            try {
                this.plugin.getDiscordSRV().getAPI().getSelfUser().getManager()
                        .setAvatar(Icon.from(new File(this.plugin.getDataFolder().getAbsolutePath() + "/picture.jpg")))
                        .complete();
                sender.sendMessage("Picture updated successfully");
            } catch (IOException e) {
                sender.sendMessage("Error setting picture as avatar: " + e.getMessage());
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
                sender.sendMessage(message);
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
            sender.sendMessage("Disabled because no workie");
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
            sender.sendMessage(ChatColor.AQUA + "You have been " + subscribedMessage + " to Discord messages.");
        }
        if (args[0].equalsIgnoreCase("subscribe")) {
            this.plugin.getDiscordSRV().setSubscribed(senderPlayer.getUniqueId(), true);
            sender.sendMessage(ChatColor.AQUA + "You have been subscribed to Discord messages.");
        }
        if (args[0].equalsIgnoreCase("unsubscribe")) {
            this.plugin.getDiscordSRV().setSubscribed(senderPlayer.getUniqueId(), false);
            sender.sendMessage(ChatColor.AQUA + "You are no longer subscribed to Discord messages.");
        }
        return true;
    }

}
