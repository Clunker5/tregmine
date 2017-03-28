package info.tregmine.discord.exception;

public class JDAFailedException extends Exception {

    private static final long serialVersionUID = -4140400857305074767L;
    private Throwable cause = null;

    public JDAFailedException(String message) {
        super(message);
    }

    public JDAFailedException(Throwable e) {
        super(e);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
