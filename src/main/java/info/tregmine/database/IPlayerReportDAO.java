package info.tregmine.database;

import info.tregmine.api.PlayerReport;
import info.tregmine.api.TregminePlayer;

import java.util.List;

public interface IPlayerReportDAO {
    List<PlayerReport> getReportsBySubject(TregminePlayer player) throws DAOException;

    void insertReport(PlayerReport report) throws DAOException;
}
