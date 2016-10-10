package info.tregmine.database;

import org.apache.commons.dbcp.BasicDataSource;

public interface IContextFactory {
	public IContext createContext() throws DAOException;
	public BasicDataSource getDataSource();
}
