package info.tregmine.commands;

import static org.bukkit.ChatColor.RED;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class AfkCommand extends AbstractCommand {

	public AfkCommand(Tregmine tregmine) {
		super(tregmine, "afk");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length != 0) {
			player.sendStringMessage(RED + "This command does not need arguments.");

			return true;
		}
		if (player.isAfk()) {
			// Player is afk, wake them up
			player.setAfk(false);

		} else {
			// Player is not afk, make them afk and announce it.
			player.setAfk(true);
		}
		return true;
	}

}
