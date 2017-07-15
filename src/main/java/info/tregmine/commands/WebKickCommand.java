package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.WebServer;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.PlayerReport;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerReportDAO;
import net.md_5.bungee.api.chat.TextComponent;

import static org.bukkit.ChatColor.*;


public class WebKickCommand extends AbstractCommand {
    public WebKickCommand(Tregmine tregmine) {
        super(tregmine, "webkick", Tregmine.PermissionDefinitions.STAFF_REQUIRED);
    }

    private String argsToMessage(String[] args) {
        StringBuffer buf = new StringBuffer();
        for (int i = 1; i < args.length; ++i) {
            buf.append(" ");
            buf.append(args[i]);
        }

        return buf.toString();
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(DARK_AQUA + "/webkick <player> <message>");
            return true;
        }

        String pattern = args[0];
        String message = argsToMessage(args);

        GenericPlayer victim = tregmine.getPlayerOffline(pattern);
        if (victim == null) {
            // TODO: error message
            return true;
        }

        WebServer webServer = tregmine.getWebServer();
        if (!webServer.isPlayerOnWeb(victim)) {
            error(player, "Player is not on web chat.");
            return true;
        }

        tregmine.broadcast(player.getChatName(),
                new TextComponent(AQUA + " webkicked "), victim.getChatName(), new TextComponent(AQUA + ": " + message));
        LOGGER.info(victim.getRealName() + " was webkicked by " + player.getName());
        // victim.kickPlayer("webkicked by " + player.getName() + ": " +
        // message);
        webServer.executeChatAction(new WebServer.KickAction(player, victim, message));

        try (IContext ctx = tregmine.createContext()) {
            PlayerReport report = new PlayerReport();
            report.setSubjectId(victim.getId());
            report.setIssuerId(player.getId());
            report.setAction(PlayerReport.Action.KICK);
            report.setMessage(message + " (webkick)");

            IPlayerReportDAO reportDAO = ctx.getPlayerReportDAO();
            reportDAO.insertReport(report);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
