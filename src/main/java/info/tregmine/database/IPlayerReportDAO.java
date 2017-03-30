package info.tregmine.database;

import info.tregmine.api.GenericPlayer;
import info.tregmine.api.PlayerReport;

import java.util.List;

public interface IPlayerReportDAO {
    List<PlayerReport> getReportsBySubject(GenericPlayer player) throws DAOException;

    void insertReport(PlayerReport report) throws DAOException;
}
