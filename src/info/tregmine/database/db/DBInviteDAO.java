package info.tregmine.database.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IInviteDAO;

public class DBInviteDAO implements IInviteDAO {

	private Connection c;
	private Tregmine plugin;

	public DBInviteDAO(Connection conn, Tregmine t) {
		this.c = conn;
		this.plugin = t;
	}

	@Override
	public void addInvite(TregminePlayer inviter, TregminePlayer invitee) throws DAOException {
		String sql = "INSERT INTO player_referlog (inviter_id, invitee_id) VALUES (?, ?)";
		try (PreparedStatement stmt = c.prepareStatement(sql)) {
			stmt.setInt(1, inviter.getId());
			stmt.setInt(2, invitee.getId());
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
