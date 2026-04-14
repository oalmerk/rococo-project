package olmerk.exception;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(String username) {
        super("Artist with  '" + username + "' not found");
    }
}
