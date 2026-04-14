package olmerk.exception;

public class MuseumNotFoundException extends RuntimeException {
    public MuseumNotFoundException(String username) {
        super("Museum with  '" + username + "' not found");
    }
}
