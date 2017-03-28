package info.tregmine.database;

import info.tregmine.api.TregminePlayer;

public interface IDiscordDAO {
    TregminePlayer isLinked(long discordID);

    long isLinked(TregminePlayer s);

    boolean link(TregminePlayer s, long discordID);

    boolean unlink(long discordID);

    boolean unlink(TregminePlayer s);
}
