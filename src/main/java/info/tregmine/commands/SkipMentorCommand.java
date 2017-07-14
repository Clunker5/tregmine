package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Rank;
import info.tregmine.api.TextComponentBuilder;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IMentorLogDAO;
import info.tregmine.database.IPlayerDAO;
import net.md_5.bungee.api.ChatColor;

import java.util.List;

import static org.bukkit.ChatColor.GREEN;

public class SkipMentorCommand extends AbstractCommand {

    public SkipMentorCommand(Tregmine tregmine) {
        super(tregmine, "skipmentor", Tregmine.PermissionDefinitions.ADMIN_REQUIRED);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length < 1) return this.invalidArguments(player, "/skipmentor <player>");
        List<GenericPlayer> matches = this.tregmine.matchPlayer(args[0]);
        if (matches.size() != 1) {
            player.sendMessage(new TextComponentBuilder("Could not find a player by the name of '" + args[0] + "'").setColor(ChatColor.DARK_RED).setBold(true).build());
            return true;
        }
        GenericPlayer student = matches.get(0);
        if (student.getRank() != Rank.TOURIST && student.getRank() != Rank.UNVERIFIED) {
            player.sendMessage(student.getChatName(), new TextComponentBuilder(" is already past the mentoring process.").setColor(ChatColor.RED).setBold(true).build());
            return true;
        }
        try (IContext ctx = tregmine.createContext()) {
            student.setRank(Rank.SETTLER);

            IPlayerDAO playerDAO = ctx.getPlayerDAO();
            playerDAO.updatePlayer(student);
            playerDAO.updatePlayerInfo(student);

            IMentorLogDAO mentorLogDAO = ctx.getMentorLogDAO();
            int mentorLogId = mentorLogDAO.getMentorLogId(student, player);

            mentorLogDAO.updateMentorLogEvent(mentorLogId, IMentorLogDAO.MentoringEvent.COMPLETED);
            player.sendMessage(GREEN + "Mentoring of " + student.getName() + GREEN + " has now finished!");
            player.giveExp(100);

            student.sendMessage(GREEN + "Congratulations! You have now achieved "
                    + "settler status. We hope you'll enjoy your stay on Tregmine!");
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}
