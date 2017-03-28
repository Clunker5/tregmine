package info.tregmine.database;

import info.tregmine.api.StaffNews;

import java.util.List;

public interface IStaffNewsDAO {
    List<StaffNews> getStaffNews() throws DAOException;

    void insertNews(StaffNews news) throws DAOException;
}
