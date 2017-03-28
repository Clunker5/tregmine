package info.tregmine.database;

import info.tregmine.api.TregminePlayer;

public interface IInviteDAO {
    void addInvite(TregminePlayer inviter, TregminePlayer invitee) throws DAOException;
}
