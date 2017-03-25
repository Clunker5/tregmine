package info.tregmine.database;

import info.tregmine.api.TregminePlayer;

public interface IInviteDAO {
	public void addInvite(TregminePlayer inviter, TregminePlayer invitee) throws DAOException;
}
