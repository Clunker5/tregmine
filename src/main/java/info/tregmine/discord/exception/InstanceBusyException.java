package info.tregmine.discord.exception;

public class InstanceBusyException extends Throwable {
    private static final long serialVersionUID = -4140400857305074767L;
    private Throwable cause = null;

    public InstanceBusyException(String message) {
        super(message);
    }

    public InstanceBusyException(Throwable e) {
        super(e);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
