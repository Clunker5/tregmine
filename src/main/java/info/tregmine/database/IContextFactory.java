package info.tregmine.database;

public interface IContextFactory {
	public IContext createContext() throws DAOException;

	public void regenerate() throws DAOException;
}
