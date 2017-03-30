package info.tregmine.database;

import info.tregmine.api.GenericPlayer;

public interface IDiscordDAO {
    GenericPlayer isLinked(long discordID);

    long isLinked(GenericPlayer s);

    boolean link(GenericPlayer s, long discordID);

    boolean unlink(long discordID);

    boolean unlink(GenericPlayer s);
}
