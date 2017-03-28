package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IDiscordDAO;
import net.dv8tion.jda.core.entities.User;
import org.bukkit.ChatColor;

public class DiscordCommand extends AbstractCommand {

    public static final String argOptions = "[link|verify|unlink] [discordID|confcode]";
    private Tregmine tregmine;

    public DiscordCommand(Tregmine t) {
        super(t, "discord");
        this.tregmine = t;
    }

    private void badArgs(TregminePlayer s) {
        s.sendStringMessage(ChatColor.RED + "Please specify one of the following: " + argOptions);
    }

    @Override
    public boolean handlePlayer(TregminePlayer sender, String[] args) {
        if (args.length < 1) {
            badArgs(sender);
            return true;
        }
        switch (args[0]) {
            case "link":
                if (args.length < 2) {
                    badArgs(sender);
                    break;
                }
                linkPlayer(sender, args[1]);
                break;
            case "verify":
                if (args.length < 2) {
                    badArgs(sender);
                    break;
                }
                try {
                    int codeSubmitted = Integer.parseInt(args[1]);
                    boolean validCode = this.tregmine.getDiscordSRV().getConfCodes().keySet().contains(codeSubmitted);
                    if (validCode) {
                        try (IContext ctx = this.tregmine.getContextFactory().createContext()) {
                            IDiscordDAO ddao = ctx.getDiscordDAO();
                            long linked = ddao.isLinked(sender);
                            if (linked != -1) {
                                sender.sendStringMessage(ChatColor.RED + "Already linked to `" + ChatColor.ITALIC
                                        + usernameFor(linked) + ChatColor.RESET + ChatColor.RED + "`");
                                return true;
                            }
                            long dID = this.tregmine.getDiscordSRV().getDiscordIDAndDeregisterCode(codeSubmitted);
                            boolean success = ddao.link(sender, dID);
                            if (success) {
                                sender.sendStringMessage(ChatColor.GREEN + "Successfully linked to `" + ChatColor.ITALIC
                                        + usernameFor(dID) + ChatColor.RESET + ChatColor.GREEN + "`");
                                this.tregmine.getDiscordSRV().getAPI().getUserById(dID + "").getPrivateChannel()
                                        .sendMessage("Successfully linked to `" + sender.getName() + "`");
                            } else {
                                sender.sendStringMessage(
                                        ChatColor.RED + "Failed to link to `" + ChatColor.ITALIC + usernameFor(dID)
                                                + ChatColor.RESET + ChatColor.RED + "`. Are you already linked?");
                            }
                        } catch (DAOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (NumberFormatException e) {
                    sender.sendStringMessage(ChatColor.RED + "Please submit a valid integer.");
                    e.printStackTrace();
                }
                break;
            case "unlink":
                try (IContext ctx = this.tregmine.getContextFactory().createContext()) {
                    IDiscordDAO ddao = ctx.getDiscordDAO();
                    long userid = ddao.isLinked(sender);
                    if (userid == -1) {
                        sender.sendStringMessage(ChatColor.RED + "You are not linked to a discord user.");
                    } else {
                        ddao.unlink(sender);
                        User usr = this.tregmine.getDiscordSRV().getAPI().getUserById(userid + "");
                        usr.getPrivateChannel().sendMessage(
                                "You've been unlinked from `" + sender.getName() + "` - You can now link to another user.");
                        sender.sendStringMessage(ChatColor.GREEN + "You've been unlinked from `" + usr.getName()
                                + "`. You can now link to another user.");

                    }
                } catch (DAOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                badArgs(sender);
                break;
        }
        return true;
    }

    private void linkPlayer(TregminePlayer s, String discordID) {
        try (IContext ctx = this.tregmine.getContextFactory().createContext()) {
            IDiscordDAO ddao = ctx.getDiscordDAO();
            long linked = ddao.isLinked(s);
            if (linked != -1) {
                s.sendStringMessage(ChatColor.RED + "Already linked to `" + ChatColor.ITALIC + usernameFor(linked)
                        + ChatColor.RESET + ChatColor.RED + "`");
                return;
            } else {
            }
        } catch (DAOException e) {
            e.printStackTrace();
        }

        this.tregmine.getDiscordSRV().sendConfirmationCode(discordID);
        s.sendStringMessage(ChatColor.AQUA + "Please check your Discord DMs for further instructions.");

    }

    private String usernameFor(long id) {
        return this.tregmine.getDiscordSRV().getAPI().getUserById(id + "").getName();
    }

}
