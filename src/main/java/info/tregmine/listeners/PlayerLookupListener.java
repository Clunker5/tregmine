package info.tregmine.listeners;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.GenericPlayer.Flags;
import info.tregmine.api.PlayerReport;
import info.tregmine.api.PlayerReport.Action;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.ILogDAO;
import info.tregmine.database.IPlayerReportDAO;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PlayerLookupListener implements Listener {
    private Tregmine plugin;

    public PlayerLookupListener(Tregmine instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        GenericPlayer player = plugin.getPlayer(event.getPlayer());
        if (player == null) {
            event.getPlayer().kickPlayer(ChatColor.RED + "Something went wrong");
            Tregmine.LOGGER.info(event.getPlayer().getName() + " was not found " + "in players map.");
            return;
        }

        try (IContext ctx = plugin.createContext()) {
            IPlayerReportDAO report = ctx.getPlayerReportDAO();
            List<PlayerReport> list = report.getReportsBySubject(player);
            for (PlayerReport i : list) {
                if (i.getAction() != Action.HARDWARN && i.getAction() != Action.SOFTWARN) {
                    continue;
                }
                Date validUntil = i.getValidUntil();
                if (validUntil == null) {
                    continue;
                }
                if (validUntil.getTime() < System.currentTimeMillis()) {
                    continue;
                }

                SimpleDateFormat dfm = new SimpleDateFormat("dd/MM/yy hh:mm:ss a");
                player.sendMessage(ChatColor.RED + "[" + i.getAction() + "]" + i.getMessage() + " - Valid until: "
                        + dfm.format(i.getTimestamp()));
                break;
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        if (!player.hasFlag(GenericPlayer.Flags.HIDDEN_ANNOUNCEMENT)) {
            if (player.hasFlag(GenericPlayer.Flags.INVISIBLE)) {
                for (GenericPlayer to : plugin.getOnlinePlayers()) {
                    if (to.getRank().canSeeHiddenInfo()) {
                        if (player.getCountry() != null) {
                            to.sendMessage(new TextComponent("Welcome "), player.getChatNameStaff(),
                                    new TextComponent(" from " + player.getCountry() + "!"));
                            to.sendMessage(player.getChatNameStaff(),
                                    new TextComponent(ChatColor.DARK_AQUA + " is invisible!"));
                        } else {
                            to.sendMessage(new TextComponent(ChatColor.DARK_AQUA + "Welcome "),
                                    player.getChatNameStaff());
                            to.sendMessage(player.getChatNameStaff(),
                                    new TextComponent("" + ChatColor.DARK_AQUA + " is invisible!"));
                        }
                        if (player.hasFlag(Flags.CHILD)) {
                            to.sendMessage(player.getChatName(), new TextComponent(
                                    ChatColor.YELLOW + " is a child; Please be aware when sending messages."));
                        }
                    }
                }
            } else {
                if (player.getCountry() != null && !player.hasFlag(GenericPlayer.Flags.HIDDEN_LOCATION)) {
                    plugin.broadcast(new TextComponent(ChatColor.DARK_AQUA + "Welcome "), player.getChatName(),
                            new TextComponent(ChatColor.DARK_AQUA + " from " + player.getCountry() + "!"));
                } else {
                    plugin.broadcast(new TextComponent(ChatColor.DARK_AQUA + "Welcome "), player.getChatName());
                }
                if (player.hasFlag(Flags.CHILD)) {
                    plugin.broadcast(player.getChatName(), new TextComponent(
                            ChatColor.YELLOW + " is a child; Please be aware when sending messages."));
                }
            }

        }
        PermissionAttachment attachment = player.addAttachment(plugin);
        player.setAttachment(attachment);
        if (player.getRank().canUseAllCO()) {
            attachment.setPermission("coreprotect.*", true);
        }
        if (player.getRank().canInspect()) {
            attachment.setPermission("coreprotect.inspect", true);
        }
        if (player.getRank().canLookup()) {
            attachment.setPermission("coreprotect.lookup", true);
        }
        if (player.getRank().canRollback()) {
            attachment.setPermission("coreprotect.rollback", true);
        }
        if (player.getRank().canRestore()) {
            attachment.setPermission("coreprotect.restore", true);
        }
        if (player.getRank().canPurge()) {
            attachment.setPermission("coreprotect.purge", true);
        }
        if (player.getRank().canReload()) {
            attachment.setPermission("coreprotect.reload", true);
        }
        attachment.setPermission("coreprotect.help", true);

        if (player.getRank().WEGeneral()) {
            this.plugin.addPermissions(attachment, Arrays.asList(new String[] {
                "worldedit.chunkinfo",
                "worldedit.listchunks",
                "worldedit.clipboard.*",
                "worldedit.schematic.*",
                "worldedit.generation.*",
                "worldedit.history.*",
                "worldedit.region.*",
                "worldedit.selection.*",
                "worldedit.wand",
                "worldedit.analysis.*",
                "worldedit.snapshots.*",
                "worldedit.tool.*",
                "worldedit.brush.*",
                "worldedit.fill.*",
                "worldedit.fixlava",
                "worldedit.fixwater",
                "worldedit.removeabove",
                "worldedit.removebelow",
                "worldedit.removenear",
                "worldedit.replacenear",
                "worldedit.snow",
                "worldedit.thaw",
                "worldedit.green",
                "worldedit.extinguish",
                "worldedit.calc",
                "worldedit.fill",
                "worldedit.biome.*"
            }));
        }

        if (player.getRank().WENavigation()) {
            this.plugin.addPermissions(attachment, Arrays.asList(new String[] {
                "worldedit.regen",
                "worldedit.navigation.*"
            }));
        }
        attachment.setPermission("worldedit.help", true);

        String aliasList = null;
        try (IContext ctx = plugin.createContext()) {
            ILogDAO logDAO = ctx.getLogDAO();
            Set<String> aliases = logDAO.getAliases(player);

            StringBuilder buffer = new StringBuilder();
            String delim = "";
            for (String name : aliases) {
                buffer.append(delim);
                buffer.append(name);
                delim = ", ";
            }

            aliasList = buffer.toString();

            if (aliases.size() > 1) {
                Tregmine.LOGGER.info("Aliases: " + aliasList);

                for (GenericPlayer current : plugin.getOnlinePlayers()) {
                    if (!current.getRank().canSeeAliases()) {
                        continue;
                    }
                    if (player.hasFlag(GenericPlayer.Flags.INVISIBLE)
                            || player.hasFlag(GenericPlayer.Flags.HIDDEN_LOCATION)) {
                        continue;
                    }
                }
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
}
