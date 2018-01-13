package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.Rank;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public class SetRankCommand extends AbstractCommand {
    public SetRankCommand(Tregmine tregmine) {
        super(tregmine, "setrank", Rank.SENIOR_ADMIN);
    }

    @Override
    public boolean handlePlayer(TregminePlayer player, String[] args)
    {
        if (args.length < 1) {
            return false;
        }

        String rawPlayer = args[0];
        TregminePlayer target = this.tregmine.getPlayer(rawPlayer);

        if (target == null) {
            player.sendError("The provided player does not exist.");
            return true;
        }

        String rawRank = args[1];
        boolean broadcast = args.length == 3 ? args[2].equalsIgnoreCase("true") : false;
        Rank rank;

        try {
            rank = Rank.valueOf(rawRank.toUpperCase());
        } catch (IllegalArgumentException ex) {
            player.sendError("The rank provided does not exist.");
            return true;
        }

        try (IContext ctx = tregmine.createContext()) {
            target.setRank(rank);

            IPlayerDAO playerDAO = ctx.getPlayerDAO();
            playerDAO.updatePlayer(target);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        String text = target.getChatName() + " is now a " + StringUtils.capitalize(rank.name().toLowerCase().replace('_', ' '));

        if (broadcast) {
            this.tregmine.getServer().spigot().broadcast(
                new ComponentBuilder("")
                    .append(
                            new BaseComponent[] {
                                    target.getChatNameTextComponent(false)
                            })
                    .append(" is now a ")
                    .append(rank.getProperName(true))
                    .create()
            );
        }

        return true;
    }
}
