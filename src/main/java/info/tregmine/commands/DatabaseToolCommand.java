package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IItemDAO;
import org.bukkit.ChatColor;
import org.bukkit.Server;

/**
 * Created by ericrabil on 3/25/17.
 */
public class DatabaseToolCommand extends AbstractCommand {

    private Tregmine plugin;

    public DatabaseToolCommand(Tregmine tregmine) {
        super(tregmine, "database");
        this.plugin = tregmine;
    }

    @Override
    public boolean handleOther(Server server, String[] args) {
        if (args.length < 1) {
            return false;
        }
        switch (args[0]) {
            case "repopulate":
                repopulate();
                break;
        }
        return true;
    }

    @Override
    public boolean handlePlayer(TregminePlayer player, String[] args) {
        player.sendStringMessage(ChatColor.RED + "Please use this command from the console.");
        return true;
    }

    private void repopulate() {
        Tregmine.LOGGER.info("[DATABASE] Beginning database repopulation...");
        try (IContext ctx = plugin.getContextFactory().createContext()) {
            IItemDAO idao = ctx.getItemDAO();
            idao.repopulateDatabase();
        } catch (DAOException e) {
            Tregmine.LOGGER.info("[DATABASE] Repopulation failed: ");
            e.printStackTrace();
        }
    }
}
