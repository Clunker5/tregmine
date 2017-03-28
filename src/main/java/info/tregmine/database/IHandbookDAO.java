package info.tregmine.database;

import java.util.List;

public interface IHandbookDAO {
    List<String[]> getHandbook() throws DAOException;
}
