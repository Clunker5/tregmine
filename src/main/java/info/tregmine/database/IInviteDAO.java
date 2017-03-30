package info.tregmine.database;

import info.tregmine.api.GenericPlayer;

public interface IInviteDAO {
    void addInvite(GenericPlayer inviter, GenericPlayer invitee) throws DAOException;
}
