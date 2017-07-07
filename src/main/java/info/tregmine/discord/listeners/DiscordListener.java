package info.tregmine.discord.listeners;

import info.tregmine.api.Rank;
import info.tregmine.discord.DiscordDelegate;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eric on 7/7/17.
 */
public class DiscordListener extends ListenerAdapter {

    private DiscordDelegate delegate;
    private List<String> colorPermitted = new ArrayList<>();

    public DiscordListener(DiscordDelegate delegate) {
        this.delegate = delegate;
        for (Rank r : Rank.values()) {
            if (r.getDiscordEquivalent() == null)
                return;
            colorPermitted.add(r.getDiscordEquivalent());
        }
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
            message = message.replaceAll("&([0-9a-qs-z])", "");
        String authorAttr = event.getMember().getRoles().isEmpty()
                ? ""
                : this.convertRoleToMinecraftColor(this.getTopRole(event));
        message = "(" + authorAttr + ChatColor.RESET + ") " + message;
        this.delegate.getPlugin().broadcast(new TextComponent(message));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event != null && event.getAuthor().getId() != null && event.getJDA().getSelfUser().getId() != null
                && event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
            return;

        if (!event.isFromType(ChannelType.TEXT))
            return;

        if (event.getTextChannel().getId().equals(this.delegate.getChatChannel().getId()))
            this.onChatChannelReceived(event);
    }

    private Role getTopRole(MessageReceivedEvent event) {
        Role highestRole = null;
        for (Role role : event.getGuild().getMember(event.getAuthor()).getRoles()) {
            if (highestRole == null)
                highestRole = role;
            else if (highestRole.getPosition() < role.getPosition())
                highestRole = role;
        }
        return highestRole;
    }

    private String convertRoleToMinecraftColor(Role role) {
        if (role == null) {
            return "";
        }
        String before = String.format("%06x", role.getColor().getRGB() & 0x00FFFFFF).toUpperCase();

        if (before.equalsIgnoreCase("99AAB5"))
            return "&f";
        if (before.equalsIgnoreCase("1ABC9C"))
            return "&a";
        if (before.equalsIgnoreCase("2ECC71"))
            return "&a";
        if (before.equalsIgnoreCase("3498DB"))
            return "&9";
        if (before.equalsIgnoreCase("9B59B6"))
            return "&5";
        if (before.equalsIgnoreCase("E91E63"))
            return "&d";
        if (before.equalsIgnoreCase("F1C40F"))
            return "&e";
        if (before.equalsIgnoreCase("E67E22"))
            return "&6";
        if (before.equalsIgnoreCase("E74C3C"))
            return "&c";
        if (before.equalsIgnoreCase("95A5A6"))
            return "&7";
        if (before.equalsIgnoreCase("607D8B"))
            return "&8";
        if (before.equalsIgnoreCase("11806A"))
            return "&2";
        if (before.equalsIgnoreCase("1F8B4C"))
            return "&2";
        if (before.equalsIgnoreCase("206694"))
            return "&9";
        if (before.equalsIgnoreCase("71368A"))
            return "&5";
        if (before.equalsIgnoreCase("AD1457"))
            return "&d";
        if (before.equalsIgnoreCase("C27C0E"))
            return "&6";
        if (before.equalsIgnoreCase("A84300"))
            return "&6";
        if (before.equalsIgnoreCase("992D22"))
            return "&4";
        if (before.equalsIgnoreCase("979C9F"))
            return "&7";
        if (before.equalsIgnoreCase("546E7A"))
            return "&8";
        return "";
    }

}
