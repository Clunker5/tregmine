package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.GenericPlayer.Flags;
import info.tregmine.api.Rank;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;

import java.util.List;

import static org.bukkit.ChatColor.*;

public class PromoteCommand extends AbstractCommand {

    private Tregmine tregmine;

    public PromoteCommand(Tregmine tregmine) {
        super(tregmine, "promote");
        this.tregmine = tregmine;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (player.getRank() != Rank.SENIOR_ADMIN && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "You certainly don't have permission to promote players!");
            return true;
        }
        // This player is a senior admin and is allowed to promote. Continue.
        if (args.length != 2) {
            // Player didn't enter two arguments, terminate.
            return false;
        }
        // The checks have finished, perform the command
        Rank rank;
        try {
            rank = Rank.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(RED + "That rank does not exist. Available ranks: ");
            player.sendMessage(RED + StringUtils.join(Rank.values(), " "));
            return true;
        }
        List<GenericPlayer> candidate = tregmine.matchPlayer(args[0]);
        if (candidate.size() != 1) {
            player.sendMessage(RED + "The player specified was not found. Please try again.");
            return true;
        }
        GenericPlayer user = candidate.get(0);
        if (rank == user.getRank()) {
            player.sendMessage(RED + "The player already has the desired rank.");
        }
        if (user.hasFlag(Flags.HARDWARNED)) {
            player.sendMessage(
                    RED + "The player specified has been hardwarned and is not eligible for promotion.");
            return true;
        }
        // Any other errors have now been checked and dealt with. Promote the
        // user.
        try (IContext ctx = tregmine.createContext()) {
            user.setRank(rank);
            if (rank != Rank.SENIOR_ADMIN && rank != Rank.GUARDIAN && rank != Rank.JUNIOR_ADMIN) {
                user.setStaff(false);
            }
            user.setMentor(null);

            IPlayerDAO playerDAO = ctx.getPlayerDAO();
            playerDAO.updatePlayer(user);
            playerDAO.updatePlayerInfo(user);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        this.tregmine.broadcast(new TextComponent("" + BLUE + ITALIC), user.getChatName(), new TextComponent(
                RESET + "" + GREEN + " has been promoted to " + RESET + BLUE + ITALIC + rank.getDiscordEquivalent() + "!"));
        return true;
    }

}
