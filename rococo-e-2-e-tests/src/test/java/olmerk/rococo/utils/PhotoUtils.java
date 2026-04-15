package olmerk.rococo.utils;

import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class PhotoUtils {

    public static String fromFile(String filePath) {
        ClassLoader classLoader = ResourceLoader.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filePath);
        byte[] imageBytes = null;
        try {
            imageBytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}
