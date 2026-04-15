package olmerk;

import olmerk.service.PropertiesLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RococoPaintingService {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RococoPaintingService.class);
        springApplication.addListeners(new PropertiesLogger());
        springApplication.run(args);
    }
}
