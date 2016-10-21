package info.tregmine.database;

public class DAOException extends Exception {
	private String query = null;
	private Throwable cause = null;

	public DAOException(String message, String query) {
		super(message);

		this.query = query;
	}

	public DAOException(String query, Throwable e) {
		super(e);
		this.cause = e;
		this.query = query;
	}

	public DAOException(Throwable e) {
		super(e);
	}

	@Override
	public Throwable getCause() {
		return this.cause;
	}

	public String getQuery() {
		return query;
	}
}
