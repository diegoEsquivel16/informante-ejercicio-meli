package informante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InformanteStartup {

        public static void main(String[] args) {
            SpringApplication.run(InformanteStartup.class, args);
        }
}
