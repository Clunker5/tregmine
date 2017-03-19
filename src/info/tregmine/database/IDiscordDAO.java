package info.tregmine.database;

import info.tregmine.api.TregminePlayer;

public interface IDiscordDAO {
	public TregminePlayer isLinked(long discordID);

	public long isLinked(TregminePlayer s);

	public boolean link(TregminePlayer s, long discordID);

	public boolean unlink(long discordID);

	public boolean unlink(TregminePlayer s);
}
