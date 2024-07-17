package ttakkeun.ttakkeun_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TtakkeunServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TtakkeunServerApplication.class, args);
	}

}
