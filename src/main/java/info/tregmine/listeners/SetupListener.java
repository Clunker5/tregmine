package info.tregmine.listeners;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Rank;
import info.tregmine.commands.MentorCommand;
import info.tregmine.database.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class SetupListener implements Listener {
    private Tregmine plugin;

    public SetupListener(Tregmine instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        GenericPlayer player = plugin.getPlayer(event.getPlayer());
        if (player.getChatState() == GenericPlayer.ChatState.SETUP && player.getRank() == Rank.TOURIST) {
            // Player is on second stage.
            String text = event.getMessage();
            event.setCancelled(true);
            if ("no".equalsIgnoreCase(text)) {
                player.sendMessage(ChatColor.GREEN + "You have now joined Tregmine "
                        + "and can talk with other players! Say Hi! :)");
                event.setMessage("%cancel%");
                player.setChatState(GenericPlayer.ChatState.CHAT);
                Tregmine.LOGGER.info("[SETUP] " + player.getChatName().getText() + " joined the server.");

                plugin.broadcast(new TextComponent(ChatColor.GREEN + "Welcome to Tregmine, "), player.getChatName(),
                        new TextComponent(ChatColor.GREEN + "!"));

                MentorCommand.findMentor(plugin, player);
                return;
            } else {
                try {
                    Integer inviter = Integer.parseInt(text);
                    // Test to see if the referral code matches any players. If
                    // not, try again.
                    try (IContext ctx = this.plugin.createContext()) {
                        IPlayerDAO pld = ctx.getPlayerDAO();
                        GenericPlayer inviterPlayer = pld.getPlayer(inviter);
                        if (inviterPlayer == null) {
                            player.sendMessage(ChatColor.RED + "You entered an invalid invite code. Please type");
                            player.sendMessage(ChatColor.RED + "a valid invite code or type no to skip.");
                            return;
                        }
                        IInviteDAO invdao = ctx.getInviteDAO();
                        invdao.addInvite(inviterPlayer, player);
                        IWalletDAO waldao = ctx.getWalletDAO();
                        waldao.add(inviterPlayer, 10000);
                        waldao.insertTransaction(0, inviterPlayer.getId(), 10000);
                        player.sendMessage(
                                ChatColor.GREEN + inviterPlayer.getChatNameNoColor() + " has recieved their reward.");
                        player.sendMessage(ChatColor.GREEN + "You have now joined Tregmine "
                                + "and can talk with other players! Say Hi! :)");
                        player.setChatState(GenericPlayer.ChatState.CHAT);
                        Tregmine.LOGGER.info("[SETUP] " + player.getChatName().getText() + " joined the server.");
                        plugin.broadcast(new TextComponent(ChatColor.GREEN + "Welcome to Tregmine, "),
                                player.getChatName(), new TextComponent(ChatColor.GREEN + "!"));

                        MentorCommand.findMentor(plugin, player);
                    } catch (DAOException e) {
                        e.printStackTrace();
                        player.sendMessage(ChatColor.RED + "Sorry, please try again later.");
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "You must either enter no or a numeric invite code.");
                    return;
                }
            }
            return;
        }
        if (player.getChatState() != GenericPlayer.ChatState.SETUP) {
            return;
        }

        event.setCancelled(true);

        String text = event.getMessage();
        player.sendMessage(text);

        Tregmine.LOGGER.info("[SETUP] <" + player.getChatNameNoHover() + "> " + text);

        try (IContext ctx = plugin.createContext()) {
            if ("yes".equalsIgnoreCase(text)) {
                // player.sendMessage("");
                // player.sendMessage(ChatColor.GREEN + "You have now
                // joined Tregmine "
                // + "and can talk with other players! Say Hi! :)");
                // player.setChatState(TregminePlayer.ChatState.CHAT);
                player.setRank(Rank.TOURIST);
                player.sendMessage(
                        "[SETUP] " + ChatColor.GREEN + "Did somebody invite you to the server? If so,");
                player.sendMessage("[SETUP] " + ChatColor.GREEN + "enter the invite code now. If not, say 'no'");
                IPlayerDAO playerDAO = ctx.getPlayerDAO();
                playerDAO.updatePlayer(player);

                // Tregmine.LOGGER.info("[SETUP] " + player.getChatName() + "
                // joined the server.");

                // server.broadcastMessage(ChatColor.GREEN + "Welcome to
                // Tregmine, " +
                // player.getChatName() + ChatColor.GREEN + "!");
                // plugin.broadcast(new TextComponent(ChatColor.GREEN + "Welcome
                // to Tregmine, "), player.getChatName(),
                // new TextComponent(ChatColor.GREEN + "!"));

                // MentorCommand.findMentor(plugin, player);
            } else if ("no".equalsIgnoreCase(text)) {
                player.sendMessage(ChatColor.YELLOW + "Unfortunately Tregmine has an "
                        + "age limit of 13 years and older. Your account has been flagged as a child.");

                player.setChatState(GenericPlayer.ChatState.CHAT);
                player.setFlag(GenericPlayer.Flags.CHILD);
                plugin.broadcast(player.getChatName(),
                        new TextComponent(ChatColor.YELLOW + " is a child; Please be aware when sending messages."));
                player.setRank(Rank.TOURIST);

                IPlayerDAO playerDAO = ctx.getPlayerDAO();
                playerDAO.updatePlayer(player);

                Tregmine.LOGGER.info("[SETUP] " + player.getChatNameNoHover() + " has been marked as a child.");
            } else {
                player.sendMessage(ChatColor.RED + "Please say \"yes\" or \"no\". "
                        + "You will not be able to talk to other players until you do.");
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        GenericPlayer player = plugin.getPlayer(event.getPlayer());
        if (player == null) {
            event.getPlayer().kickPlayer("Something went wrong");
            Tregmine.LOGGER.info(event.getPlayer().getName() + " was not found " + "in players map.");
            return;
        }

        if (player.getChatState() != GenericPlayer.ChatState.SETUP) {
            return;
        }

        Tregmine.LOGGER.info("[SETUP] " + player.getName() + " is a new player!");

        player.sendMessage(ChatColor.YELLOW + "Welcome to Tregmine!");
        player.sendMessage(ChatColor.YELLOW + "This is an age restricted server. "
                + "Please confirm that you are 13 years or older by typing \"yes\". "
                + "If you are younger than 13, please leave this server, or " + "type \"no\" to quit.");
        player.sendMessage(ChatColor.YELLOW + "You will not be able to talk "
                + "to other players until you've verified your age.");
        TextComponent msg = new TextComponent(ChatColor.RED + "" + ChatColor.UNDERLINE + "Are you 13 years or older?");
        msg.setHoverEvent(this.plugin.buildHover(ChatColor.AQUA + "Because we are a " + ChatColor.GOLD + "COPPA"
                + ChatColor.AQUA + " compliant server, we must enforce the age requirement."));
        player.sendMessage(msg);
    }
}
