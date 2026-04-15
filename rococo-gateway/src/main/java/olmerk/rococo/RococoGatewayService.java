package olmerk.rococo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import olmerk.rococo.service.PropertiesLogger;

@SpringBootApplication
public class RococoGatewayService {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RococoGatewayService.class);
        springApplication.addListeners(new PropertiesLogger());
        springApplication.run(args);
    }
}
