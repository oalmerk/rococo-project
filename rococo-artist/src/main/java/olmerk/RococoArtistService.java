package olmerk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import olmerk.service.PropertiesLogger;

@SpringBootApplication
public class RococoArtistService {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RococoArtistService.class);
        springApplication.addListeners(new PropertiesLogger());
        springApplication.run(args);
    }
}
