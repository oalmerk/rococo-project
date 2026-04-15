package olmerk;

import olmerk.service.PropertiesLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RococoUserdataService {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RococoUserdataService.class);
        springApplication.addListeners(new PropertiesLogger());
        springApplication.run(args);
    }
}
