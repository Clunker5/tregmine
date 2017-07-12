package info.tregmine.discord.listeners;

import info.tregmine.Tregmine;
import info.tregmine.api.DiscordCommandSender;
import info.tregmine.api.Rank;
import info.tregmine.commands.AbstractCommand;
import info.tregmine.discord.DiscordDelegate;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;

import java.util.*;

/**
 * Created by eric on 7/7/17.
 */
public class DiscordListener extends ListenerAdapter {

    private DiscordDelegate delegate;
    private List<String> colorPermitted = new ArrayList<>();

    private Map<String, DiscordCommandSender> commandSenderMap = new HashMap<>();

    public DiscordListener(DiscordDelegate delegate) {
        this.delegate = delegate;
        for (Rank r : Rank.values()) {
            if (r.getNiceName() == null)
                return;
            colorPermitted.add(r.getNiceName());
        }
    }

    public void onCommandChannelReceived(MessageReceivedEvent event) {
        String message = this.filterMessage(event.getMessage());
        if (!this.commandSenderMap.containsKey(event.getAuthor().getId())) {
            Rank rank = Rank.fromDiscordString(this.delegate.getTopRole(event.getMember().getRoles()).getName());
            if (rank == null) rank = Rank.RESIDENT;
            this.commandSenderMap.put(event.getAuthor().getId(), new DiscordCommandSender(this.delegate, rank, event.getMember(), event.getChannel()));
        }
        List<String> query = new ArrayList(Arrays.asList(message.substring(1).split(" ")));
        DiscordCommandSender sender = this.commandSenderMap.get(event.getAuthor().getId());
        sender.setResponseChannel(event.getChannel());
        if (this.delegate.getPlugin().getCommand(query.get(0)) == null) {
            sender.sendMessage("The command provided does not exist.");
        } else {
            Command command = this.delegate.getPlugin().getCommand(query.get(0));
            AbstractCommand abstractCommand = (AbstractCommand) this.delegate.getPlugin().getCommand(query.get(0)).getExecutor();
            query.remove(0);
            if (!abstractCommand.handlePlayer(sender, query.toArray(new String[query.size()]))) {
                StringBuilder builder = new StringBuilder(command.getUsage());
                builder.setCharAt(0, '!');
                sender.sendMessage(builder.toString());
            }
        }
        return;
    }

    private String filterMessage(Message messageData) {
        String message = messageData.getStrippedContent();
        if (message.length() == 0) return "";
        boolean stripColors = true;
        for (Role role : messageData.getMember().getRoles()) {
            if (this.colorPermitted.contains(role.getName()))
                stripColors = false;
        }
        if (stripColors)
            message = message.replaceAll("#([0-9a-qs-z])", "");
        return message;
    }

    public void onChatChannelReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getStrippedContent();
        if (message.length() == 0) return;
        boolean stripColors = true;
        for (Role role : event.getMember().getRoles()) {
            if (this.colorPermitted.contains(role.getName()))
                stripColors = false;
        }
        if (stripColors)
            message = message.replaceAll("#([0-9a-qs-z])", "");
        String authorAttr = event.getMember().getNickname() != null ? event.getMember().getNickname() : event.getAuthor().getName();
        System.out.println(authorAttr);
        String color = ChatColor.translateAlternateColorCodes('#', this.delegate.convertRoleToMinecraftColor(this.delegate.getTopRole(event.getMember().getRoles())));
        message = "(" + authorAttr + ChatColor.RESET + ") " + message;
        Tregmine.LOGGER.info("DISCORD " + ChatColor.stripColor(message));
        this.delegate.getPlugin().broadcast(new TextComponent(message));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event != null && event.getAuthor().getId() != null && event.getJDA().getSelfUser().getId() != null
                && event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
            return;

        if (!event.isFromType(ChannelType.TEXT))
            return;

        if (!event.getGuild().getId().equalsIgnoreCase(this.delegate.getChatChannel().getGuild().getId()))
            return;

        if (event.getAuthor().isBot())
            return;

        String message = event.getMessage().getContent();
        if (message.startsWith("!") && !message.startsWith("!" + "!") && !message.equals("!"))
            this.onCommandChannelReceived(event);
        else if (event.getTextChannel().getId().equals(this.delegate.getChatChannel().getId()))
            this.onChatChannelReceived(event);
    }

}
