package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.FishyBlock;
import info.tregmine.api.GenericPlayer;
import info.tregmine.database.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;

import java.util.List;
import java.util.Map;

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
            case "repopulateItems":
                repopulateItems();
                break;
            case "reload":
                loadBFS();
                break;
        }
        return true;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        player.sendMessage(ChatColor.RED + "Please use this command from the console.");
        return true;
    }

    public void loadBFS() {
        Tregmine.LOGGER.info("[DATABASE] Reloading blessed, fishy blocks, strings...");
        try (IContext ctx = this.plugin.getContextFactory().createContext()) {
            IBlessedBlockDAO blessedBlockDAO = ctx.getBlessedBlockDAO();
            Map<Location, Integer> blessedBlocks = blessedBlockDAO.load(this.plugin.getServer());

            LOGGER.info("Loaded " + blessedBlocks.size() + " blessed blocks");

            IFishyBlockDAO fishyBlockDAO = ctx.getFishyBlockDAO();
            Map<Location, FishyBlock> fishyBlocks = fishyBlockDAO.loadFishyBlocks(this.plugin.getServer());

            LOGGER.info("Loaded " + fishyBlocks.size() + " fishy blocks");

            IBlockDAO blockDAO = ctx.getBlockDAO();
            Map<Material, Integer> minedBlockPrices = blockDAO.loadBlockMinePrices();

            IMiscDAO miscDAO = ctx.getMiscDAO();
            List<String> insults = miscDAO.loadInsults();
            List<String> quitMessages = miscDAO.loadQuitMessages();
            List<String> bannedWords = miscDAO.loadBannedWords();
            if (insults.size() == 0) {
                insults.add(0, "");
            }
            if (quitMessages.size() == 0) {
                quitMessages.add(0, "");
            }
            LOGGER.info("Loaded " + insults.size() + " insults and " + quitMessages.size() + " quit messages");
            this.plugin.setBlessedBlocks(blessedBlocks);
            this.plugin.setFishyBlocks(fishyBlocks);
            this.plugin.setMinedBlockPrices(minedBlockPrices);
            this.plugin.setInsults(insults);
            this.plugin.setQuitMessages(quitMessages);
            this.plugin.setBannedWords(bannedWords);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private void repopulateItems() {
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
