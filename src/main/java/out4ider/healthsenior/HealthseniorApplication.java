package out4ider.healthsenior;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication()
public class HealthseniorApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthseniorApplication.class, args);
	}

}
