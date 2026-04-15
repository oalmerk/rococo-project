package olmerk.rococo;

import olmerk.rococo.service.PropertiesLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RococoAuthService {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RococoAuthService.class);
        springApplication.addListeners(new PropertiesLogger());
        springApplication.run(args);
    }
}
