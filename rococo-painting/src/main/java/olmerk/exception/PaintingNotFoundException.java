package olmerk.exception;

public class PaintingNotFoundException extends RuntimeException {
    public PaintingNotFoundException(String username) {
        super("Museum with  '" + username + "' not found");
    }
}
