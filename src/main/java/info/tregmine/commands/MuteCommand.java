package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.PlayerMute;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class MuteCommand extends AbstractCommand {
    private Tregmine tregmine;

    public MuteCommand(Tregmine p0) {
        super(p0, "mute", Tregmine.PermissionDefinitions.STAFF_REQUIRED);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length != 2) {
            error(player, "Invalid Arguments; /mute <player> <duration:-1 for indefinite>");
            return true;
        }
        if (tregmine.getPlayer(args[0]) == null) {
            player.sendMessage(ChatColor.YELLOW + "Player not found; Check the spelling and try again");
            return true;
        }
        GenericPlayer mutee = tregmine.getPlayer(args[0]);
        if (!mutee.getRank().canBeMuted()) {
            error(player, new TextComponent("You cannot mute "), mutee.decideVS(player),
                    new TextComponent(" because their rank bypasses muting."));
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
