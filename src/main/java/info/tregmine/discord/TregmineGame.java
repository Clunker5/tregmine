package info.tregmine.discord;

import net.dv8tion.jda.core.entities.Game;

public class TregmineGame implements Game {

	private String n;
	private GameType t;

	public TregmineGame(String name, GameType type) {
		this.n = name;
		this.t = type;
	}

	@Override
	public String getName() {
		return n;
	}

	@Override
	public GameType getType() {
		return t;
	}

	@Override
	public String getUrl() {
		return null;
	}

}
