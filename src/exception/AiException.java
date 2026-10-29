package exception;

// An AI problem together with the HTTP status the browser should receive
public class AiException extends RuntimeException {

    private final int status;

    public AiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}