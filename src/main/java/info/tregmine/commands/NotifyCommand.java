package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Rank;
import info.tregmine.discord.DiscordDelegate;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.WHITE;

public abstract class NotifyCommand extends AbstractCommand {
    protected Rank[] targets;

    public NotifyCommand(Tregmine tregmine, String command, Rank... targets) {
        super(tregmine, command);
        this.targets = targets;
    }

    private String argsToMessage(String[] args) {
        StringBuffer buf = new StringBuffer();
        buf.append(args[0]);
        for (int i = 1; i < args.length; ++i) {
            buf.append(" ");
            buf.append(args[i]);
        }

        return buf.toString();
    }

    protected abstract ChatColor getColor();

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String msg = argsToMessage(args);
        if (player.getRank().canUseChatColors()) {
            msg = ChatColor.translateAlternateColorCodes('#', msg);
        }

        // Don't send it twice
        if (!isTarget(player)) {
            player.sendMessage(new TextComponent(getColor() + " + "), player.decideVS(player),
                    new TextComponent(" " + WHITE + msg));
        }

        for (GenericPlayer to : tregmine.getOnlinePlayers()) {
            if (!isTarget(to)) {
                continue;
            }
            to.sendMessage(new TextComponent(getColor() + " + "), player.decideVS(to),
                    new TextComponent(" " + WHITE + msg));
        }

        if (this.tregmine.discordEnabled()) {
            DiscordDelegate delegate = this.tregmine.getDiscordDelegate();
            Guild guild = delegate.getChatChannel().getGuild();
            List<String> notified = new ArrayList<>();
            for (Rank r : this.targets) {
                List<Role> roles = guild.getRolesByName(r.getNiceName(), true);
                for (Role role : roles) {
                    for (Member member : guild.getMembersWithRoles(role)) {
                        if (notified.contains(member.getUser().getId())) continue;
                        else notified.add(member.getUser().getId());
                        if (!member.getUser().hasPrivateChannel())
                            member.getUser().openPrivateChannel().complete();
                        MessageEmbed embed = delegate.getEmbedBuilder().genericEmbed("From " + player.getName(), msg, Color.ORANGE);
                        embed = new EmbedBuilder(embed).setAuthor(r.getNiceName() + " Alert", null, null).build();
                        member.getUser().openPrivateChannel().complete().sendMessage(embed).complete();
                    }
                }
            }
        }


        return true;
    }

    protected abstract boolean isTarget(GenericPlayer player);
}
