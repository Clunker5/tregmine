package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class MuteCommand extends AbstractCommand{
	Tregmine t;
	public MuteCommand(Tregmine instance){
		super(instance, "mute");
		t = instance;
	}
	public boolean handlePlayer(TregminePlayer a, String[] args){
		/*
		 * TODO:
		 * Allow muting of players
		 * With duration, or until they are unmuted by staff
		 * Log to their reports
		 * 
		 */
		
		return true;
	}
}
