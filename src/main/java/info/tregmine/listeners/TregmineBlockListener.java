package info.tregmine.listeners;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.coreprotect.TregmineCoreProtectAPI;
import info.tregmine.database.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import java.util.EnumSet;
import java.util.Set;

public class TregmineBlockListener implements Listener {
    private Set<Material> loggedMaterials = EnumSet.of(Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.GOLD_ORE,
            Material.LAPIS_ORE, Material.QUARTZ_ORE, Material.REDSTONE_ORE, Material.MOB_SPAWNER);

    private Tregmine plugin;

    public TregmineBlockListener(Tregmine instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        GenericPlayer player = plugin.getPlayer(event.getPlayer());
        if (!player.getRank().canParticipateInEconomy()) return;
        Block block = event.getBlock();
        Material material = block.getType();

        if (loggedMaterials.contains(material)) {
            try (IContext ctx = plugin.createContext()) {
                ILogDAO logDAO = ctx.getLogDAO();
                logDAO.insertOreLog(player, block.getLocation(), material);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
        }

        try (IContext ctx = plugin.createContext()) {
            if (player.getGameMode() != GameMode.SURVIVAL) {
                return;
            }
            IBlockDAO blockDAO = ctx.getBlockDAO();
            int blockvalue = plugin.getMinedPrice(block.getType());
            if (!new TregmineCoreProtectAPI().isPlaced(block) && !event.isCancelled() && blockvalue != 0) {
                if (block.getType() == Material.LEAVES || block.getType() == Material.LEAVES_2) {
                    if (player.getItemInHand().getType() != Material.SHEARS) {
                        return;
                    }
                }
                try (IContext ctxNew = plugin.createContext()) {
                    IWalletDAO walletDAO = ctx.getWalletDAO();
                    walletDAO.add(player, blockvalue);
                    return;
                } catch (DAOException e) {
                    e.printStackTrace();
                }
            }
        } catch (DAOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.getToBlock().getType() == Material.NETHER_WART_BLOCK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        GenericPlayer player = plugin.getPlayer(event.getPlayer());
        if (event.getBlock().getType().equals(Material.LAVA)) {
            if (!player.getRank().canPlaceBannedBlocks()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        Material sourceblock = event.getSource().getType();
        if (sourceblock == Material.FIRE) {
            event.setCancelled(true);
        }
    }
}
