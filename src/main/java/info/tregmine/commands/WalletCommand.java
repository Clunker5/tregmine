package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Rank;
import info.tregmine.api.TextComponentBuilder;
import info.tregmine.api.math.MathUtil;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IWalletDAO;
import net.md_5.bungee.api.ChatColor;

import java.text.NumberFormat;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class WalletCommand extends AbstractCommand {
    private final static NumberFormat FORMAT = NumberFormat.getNumberInstance();

    public WalletCommand(Tregmine tregmine) {
        super(tregmine, "wallet");
    }

    private boolean balance(GenericPlayer player) {
        try (IContext ctx = tregmine.createContext()) {
            IWalletDAO walletDAO = ctx.getWalletDAO();

            long balance = walletDAO.balance(player);
            if (balance >= 0) {
                player.sendMessage("You have " + GOLD + FORMAT.format(balance) + WHITE + " Tregs.");
            } else {
                error(player, "An error occured.");
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private boolean donate(GenericPlayer player, GenericPlayer target, int amount) {
        if (MathUtil.calcDistance2d(player.getLocation(), target.getLocation()) > 5) {
            if (player.canSee(target.getDelegate())) {
                player.sendMessage(
                        RED + target.getName() + " is to far away for a wallet transaction, please move closer");
            }
            return true;
        }

        try (IContext ctx = tregmine.createContext()) {
            IWalletDAO walletDAO = ctx.getWalletDAO();

            if (walletDAO.take(player, amount)) {
                walletDAO.add(target, amount);
                walletDAO.insertTransaction(player.getId(), target.getId(), amount);

                player.sendMessage(AQUA + "You donated to " + target.getName() + " " + GOLD
                        + FORMAT.format(amount) + AQUA + " Tregs.");
                target.sendMessage(AQUA + "You received " + GOLD + FORMAT.format(amount) + AQUA + " Tregs from a "
                        + "secret admirer.");
                LOGGER.info(amount + ":TREG_DONATED " + player.getName() + "(" + walletDAO.balance(player) + ")"
                        + " => " + target.getName() + "(" + walletDAO.balance(target) + ")");
            } else {
                error(player, "You cant give more then you have!");
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private boolean give(GenericPlayer player, GenericPlayer target, int amount) {
        if (MathUtil.calcDistance2d(player.getLocation(), target.getLocation()) > 5) {
            if (player.canSee(target.getDelegate())) {
                player.sendMessage(
                        RED + target.getName() + " is to far away for a wallet transaction, please move closer");
            }
            return true;
        }

        try (IContext ctx = tregmine.createContext()) {
            IWalletDAO walletDAO = ctx.getWalletDAO();

            if (walletDAO.take(player, amount)) {
                walletDAO.add(target, amount);
                walletDAO.insertTransaction(player.getId(), target.getId(), amount);

                player.sendMessage(
                        AQUA + "You gave " + target.getName() + " " + GOLD + FORMAT.format(amount) + AQUA + " Tregs.");
                target.sendMessage(AQUA + "You received " + GOLD + FORMAT.format(amount) + AQUA + " Tregs from "
                        + player.getName() + ".");
                LOGGER.info(amount + ":TREG " + player.getName() + "(" + walletDAO.balance(player) + ")" + " => "
                        + target.getName() + "(" + walletDAO.balance(target) + ")");
            } else {
                error(player, "You cant give more then you have!");
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length == 0) {
            error(player, "Incorrect usage! Try:");
            player.sendMessage(AQUA + "/wallet tell <player>");
            player.sendMessage(AQUA + "/wallet balance");
            player.sendMessage(AQUA + "/wallet donate <player> <amount>");
            player.sendMessage(AQUA + "/wallet give <player> <amount>");
            return true;
        }

        String cmd = args[0];

        // inform people that syntax has changed
        if ("tell".equalsIgnoreCase(cmd) && args.length == 1) {
            error(player, "Usage: /wallet tell <player>");
            return true;
        }
        // new version with player parameter
        else if ("tell".equalsIgnoreCase(cmd) && args.length == 2) {
            return tell(player, args[1]);
        } else if ("balance".equalsIgnoreCase(cmd)) {
            return balance(player);
        } else if ("donate".equalsIgnoreCase(cmd) && args.length == 3) {
            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                return true;
            }

            List<GenericPlayer> candidates = tregmine.matchPlayer(args[1]);
            if (candidates.size() != 1) {
                error(player, "Unknown Player: " + args[1]);
                return true;
            }

            GenericPlayer target = candidates.get(0);

            // Sneaky ;)
            if (target.hasFlag(GenericPlayer.Flags.INVISIBLE)) {
                error(player, "Unknown Player: " + args[1]);
                return true;
            }
            if (player.getRank() == Rank.BUILDER || target.getRank() == Rank.BUILDER) {
                player.sendMessage(new TextComponentBuilder("Builders cannot participate in the economy").setColor(ChatColor.DARK_RED).setBold(true).build());
                return true;
            }
            return donate(player, target, amount);
        } else if ("give".equalsIgnoreCase(cmd) && args.length == 3) {
            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                return true;
            }

            List<GenericPlayer> candidates = tregmine.matchPlayer(args[1]);
            if (candidates.size() != 1) {
                error(player, "Unknown Player: " + args[1]);
                return true;
            }

            GenericPlayer target = candidates.get(0);

            if (target.hasFlag(GenericPlayer.Flags.INVISIBLE)) {
                error(player, "Unknown Player: " + args[1]);
                return true;
            }
            if (player.getRank() == Rank.BUILDER || target.getRank() == Rank.BUILDER) {
                player.sendMessage(new TextComponentBuilder("Builders cannot participate in the economy").setColor(ChatColor.DARK_RED).setBold(true).build());
                return true;
            }

            return give(player, target, amount);
        } else {
            player.sendMessage(AQUA + "/wallet tell <player>");
            player.sendMessage(AQUA + "/wallet balance");
            player.sendMessage(AQUA + "/wallet donate <player> <amount>");
            player.sendMessage(AQUA + "/wallet give <player> <amount>");
        }

        return false;
    }

    private boolean tell(GenericPlayer player, String name) {
        List<GenericPlayer> targets = tregmine.matchPlayer(name);
        if (targets.size() != 1) {
            error(player, "Usage: /wallet tell <player>");
            return true;
        }
        GenericPlayer target = targets.get(0);

        try (IContext ctx = tregmine.createContext()) {
            IWalletDAO walletDAO = ctx.getWalletDAO();

            long balance = walletDAO.balance(player);
            if (balance >= 0) {
                target.sendMessage(
                        player.getName() + AQUA + " has " + GOLD + FORMAT.format(balance) + AQUA + " Tregs.");
                player.sendMessage("You have " + GOLD + FORMAT.format(balance) + AQUA + " Tregs.");
            } else {
                error(player, "An error occured.");
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
