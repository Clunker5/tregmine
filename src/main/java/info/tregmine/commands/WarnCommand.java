package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.PlayerReport;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerReportDAO;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Date;
import java.util.List;

import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.GREEN;

public class WarnCommand extends AbstractCommand {
    public WarnCommand(Tregmine tregmine) {
        super(tregmine, "warn");
    }

    private String argsToMessage(String[] args) {
        StringBuffer buf = new StringBuffer();
        for (int i = 2; i < args.length; ++i) {
            buf.append(" ");
            buf.append(args[i]);
        }

        return buf.toString();
    }

    @Override
    public boolean handlePlayer(TregminePlayer player, String[] args) {
        if (!player.getRank().canWarn()) {
            return true;
        }

        if (args.length < 3) {
            player.sendMessage(DARK_AQUA + "/warn <hard|soft> <player> <message>");
            return true;
        }

        String type = args[0].toLowerCase();
        String pattern = args[1];
        String message = argsToMessage(args);

        boolean hard = false;
        if ("soft".equals(type)) {
        } else if ("hard".equals(type)) {
            hard = true;
        } else {
            player.sendMessage(DARK_AQUA + "/warn <hard|soft> <player> <message>");
            return true;
        }

        List<TregminePlayer> candidates = tregmine.matchPlayer(pattern);
        if (candidates.size() != 1) {
            // TODO: error message
            return true;
        }

        TregminePlayer victim = candidates.get(0);
        if (hard) {
            player.sendMessage(new TextComponent(GREEN + "You hardwarned "), victim.decideVS(player),
                    new TextComponent(GREEN + ": " + message));
            LOGGER.info(victim.getName() + " hardwarned by " + player.getName());
        } else {
            player.sendMessage(new TextComponent(GREEN + "You warned "), victim.decideVS(player),
                    new TextComponent(GREEN + ": " + message));
            LOGGER.info(victim.getName() + " warned by " + player.getName());
        }

        victim.setTemporaryChatName(victim.getNameColor() + victim.getName());
        if (hard) {
            victim.setFlag(TregminePlayer.Flags.HARDWARNED);
        } else {
            victim.setFlag(TregminePlayer.Flags.SOFTWARNED);
        }

        try (IContext ctx = tregmine.createContext()) {
            PlayerReport report = new PlayerReport();
            report.setSubjectId(victim.getId());
            report.setIssuerId(player.getId());
            report.setAction(hard ? PlayerReport.Action.HARDWARN : PlayerReport.Action.SOFTWARN);
            report.setMessage(message);
            // three days default
            report.setValidUntil(new Date(System.currentTimeMillis() + 3 * 86400 * 1000l));

            IPlayerReportDAO reportDAO = ctx.getPlayerReportDAO();
            reportDAO.insertReport(report);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
