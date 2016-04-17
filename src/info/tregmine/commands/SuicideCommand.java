package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class SuicideCommand extends AbstractCommand{
	Tregmine t;
	public SuicideCommand(Tregmine inst){
		super(inst, "suicide");
		t = inst;
	}
	public boolean handlePlayer(TregminePlayer player, String[] args){
		player.setHealth(0);
		return true;
	}
}
