package info.tregmine.database.db;

import info.tregmine.api.GenericPlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IInviteDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBInviteDAO implements IInviteDAO {

    private Connection c;

    public DBInviteDAO(Connection conn) {
        this.c = conn;
    }

    @Override
    public void addInvite(GenericPlayer inviter, GenericPlayer invitee) throws DAOException {
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
