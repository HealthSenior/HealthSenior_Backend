package out4ider.healthsenior;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication()
public class HealthseniorApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(HealthseniorApplication.class, args);
		RabbitAdmin rabbitAdmin = context.getBean(RabbitAdmin.class);
		rabbitAdmin.initialize();
	}

}
