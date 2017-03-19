package info.tregmine.database.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IDiscordDAO;

public class DBDiscordDAO implements IDiscordDAO {

	private Connection conn;

	public DBDiscordDAO(Connection conn) {
		this.conn = conn;
	}

	@Override
	public TregminePlayer isLinked(long discordID) {
		try {
			PreparedStatement stmt = this.conn.prepareStatement("SELECT 1 FROM player_discord WHERE discord_id = ?");
			stmt.setLong(1, discordID);
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			if (!rs.next()) {
				return null;
			} else {
				DBPlayerDAO playerdao = new DBPlayerDAO(this.conn);
				try {
					return playerdao.getPlayer(rs.getInt("player_id"));
				} catch (DAOException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public long isLinked(TregminePlayer s) {
		try {
			java.sql.PreparedStatement stmt = this.conn
					.prepareStatement("SELECT discord_id FROM player_discord WHERE player_id = ?");
			stmt.setInt(1, s.getId());
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			if (!rs.next()) {
				return -1;
			}
			return rs.getLong("discord_id");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public boolean link(TregminePlayer s, long discordID) {
		if (isLinked(s) != -1)
			return false;
		if (discordID == -1)
			return false;
		try {
			PreparedStatement stmt = this.conn
					.prepareStatement("INSERT INTO player_discord (player_id, discord_id) VALUES (?, ?)");
			stmt.setInt(1, s.getId());
			stmt.setLong(2, discordID);
			stmt.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean unlink(long discordID) {
		try {
			PreparedStatement stmt = this.conn.prepareStatement("DELETE FROM player_discord WHERE discord_id = ?");
			stmt.setLong(1, discordID);
			int deleted = stmt.executeUpdate();
			if (deleted == 1) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean unlink(TregminePlayer s) {
		if (isLinked(s) == -1)
			return false;
		try {
			PreparedStatement stmt = this.conn.prepareStatement("DELETE FROM player_discord WHERE player_id = ?");
			stmt.setInt(1, s.getId());
			stmt.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
