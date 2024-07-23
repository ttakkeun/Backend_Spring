package ttakkeun.ttakkeun_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableFeignClients
@SpringBootApplication
@EnableJpaAuditing
public class TtakkeunServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TtakkeunServerApplication.class, args);
	}

}
