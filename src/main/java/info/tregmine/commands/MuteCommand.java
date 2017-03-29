package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.PlayerMute;
import info.tregmine.api.TregminePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class MuteCommand extends AbstractCommand {
    private Tregmine tregmine;

    public MuteCommand(Tregmine p0) {
        super(p0, "mute");
    }

    @Override
    public boolean handlePlayer(TregminePlayer player, String[] args) {
        if (!player.getRank().canMute()) {
            player.sendMessage(ChatColor.RED + "You aren't allowed to mute players.");
            return true;
        }
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Invalid Arguments; /mute <player> <duration:-1 for indefinite>");
            return true;
        }
        if (tregmine.getPlayer(args[0]) == null) {
            player.sendMessage(ChatColor.YELLOW + "Player not found; Check the spelling and try again");
            return true;
        }
        TregminePlayer mutee = tregmine.getPlayer(args[0]);
        if (!mutee.getRank().canBeMuted()) {
            player.sendMessage(new TextComponent(ChatColor.RED + "You cannot mute "), mutee.decideVS(player),
                    new TextComponent(ChatColor.RED + " because their rank bypasses muting."));
            return true;
        }
        int duration;
        try {
            duration = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(
                    ChatColor.YELLOW + "Invalid duration specified; /mute <player> <duration:-1 for indefinite>");
            return true;
        }
        PlayerMute mute = new PlayerMute(player, mutee, duration);
        mutee.setMuted(true);
        mutee.setMute(mute);
        String suffix = (duration == -1) ? "ever" : " " + duration + " seconds";
        player.sendMessage(mutee.getChatName(),
                new TextComponent(ChatColor.YELLOW + " has been muted for" + suffix));
        return true;
    }
}
