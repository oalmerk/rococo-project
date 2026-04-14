package olmerk.exception;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String username) {
        super("Country with  '" + username + "' not found");
    }
}
